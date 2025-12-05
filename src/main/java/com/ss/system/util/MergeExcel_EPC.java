package com.ss.system.util;

import com.ss.excel.factory.SSExcel07Workbook;
import com.ss.excel.factory.cell.SSExcel07Cell;
import com.ss.excel.factory.sheet.SSExcel07Sheet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MergeExcel_SSPA
 * @Description TODO
 * @Version 1.0
 */
public class MergeExcel_EPC {

    /**
     * 查找包含指定字符串的所有单元格坐标
     *
     * @param searchText 要查找的文本
     * @return 单元格坐标列表 (格式: Sheet名称!A1)
     */
    public static Cell findCellsWithText(SSExcel07Sheet sheet, String searchText) {
        for (Row row : sheet.getXSSFSheet()) {
            for (Cell cell : row) {
                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    String cellValue = cell.getStringCellValue();
                    if (cellValue != null & cellValue.trim().equals(searchText)) {
                        return cell;
                    }
                } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    // 如果是数字格式，转换为字符串查找
                    String cellValue = String.valueOf(cell.getNumericCellValue());
                    if (cellValue.contains(searchText)) {
                        return cell;
                    }
                }
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class PaiGong {
        String prodNo;
        String taskName;
        String startDate;
        String endDate;
        double biaoZhunGongShi;
    }


    public static void main(String[] args) {
//        String excelPath = "D:\\浏览器下载\\201\\2022年绩效\\2201绩效\\2201绩效-SSPA.xlsx";
        String excelPath = "D:\\2206绩效统计信息_EPC.xlsx";
        SSExcel07Workbook workbook = new SSExcel07Workbook().open(
                excelPath);
        List<String> sheetNames = workbook.getAllSheetNames();
        HashMap<String, List<PaiGong>> paiGongSheetData = new HashMap<>();
//        sheetNames.remove("总体绩效");
//        sheetNames.remove("组员合计");
//        sheetNames.remove("组长-王宇栋");
        for (String sheetName : sheetNames) {
            SSExcel07Sheet sheet = workbook.getSheet(sheetName);
            ArrayList<PaiGong> paiGongs = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                SSExcel07Cell cell = sheet.getCell(i, 2);
                if (cell == null || cell.getCellValue() == null
                        || cell.getCellValue().toString().length() == 0) {
                    continue;
                }
                String date = null; // A列：日期
                String productNo = null; // B列：产品编号
                String taskName = null; // C列：工序名称
                Integer biaoZhunGongShi = null; // D列：标准工时
                try {
                    date = sheet.getCell(i, 0).getCellValue().toString();
                    //删除最后的数字
                    productNo = sheet.getCell(i, 1).getCellValue().toString();
                    taskName = sheet.getCell(i, 2).getCellValue().toString().replaceAll("\\d*\\.?\\d+$", "");
                    String s = sheet.getCell(i, 3).getCellValue().toString().replaceAll("_", "");
                    biaoZhunGongShi = Double.valueOf(s).intValue();
                } catch (NullPointerException e) {
                }

                //没有前3列数据为无效数据
                if (date == null || productNo == null || taskName == null) {
                    continue;
                }

                // 查找是否已存在相同产品和工序的记录
                boolean found = false;
                for (PaiGong pg : paiGongs) {
                    if (pg.getProdNo() != null && pg.getTaskName() != null &&
                            pg.getProdNo().trim().equals(productNo.trim()) &&
                            pg.getTaskName().trim().equals(taskName.trim())) {

                        // 更新结束日期
                        pg.setEndDate(date);

                        // 累计标准工时
//                        pg.setBiaoZhunGongShi(pg.getBiaoZhunGongShi() + biaoZhunGongShi);
//                        pg.setBiaoZhunGongShi(pg.getBiaoZhunGongShi());
                        pg.setBiaoZhunGongShi(biaoZhunGongShi);

                        found = true;
                        break;
                    }
                }

                // 如果没找到，创建新记录
                if (!found) {
                    PaiGong newPg = new PaiGong();
                    newPg.setProdNo(productNo.trim());
                    newPg.setTaskName(taskName.trim());
                    newPg.setStartDate(date);
                    newPg.setEndDate(date);
                    newPg.setBiaoZhunGongShi(biaoZhunGongShi);
                    paiGongs.add(newPg);
                }

            }
            paiGongSheetData.put(sheetName, paiGongs);
        }
        System.out.println(paiGongSheetData);
        try {
            exportToExcel(paiGongSheetData, excelPath + "_create.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将 paiGongSheetData 导出为 Excel 文件
     *
     * @param paiGongSheetData 数据源
     * @param filePath         输出文件路径
     */
    public static void exportToExcel(HashMap<String, List<PaiGong>> paiGongSheetData,
                                     String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // 遍历每个sheet的数据
        for (Map.Entry<String, List<PaiGong>> entry : paiGongSheetData.entrySet()) {
            String sheetName = entry.getKey();
            List<PaiGong> paiGongList = entry.getValue();

            // 处理sheet名称，避免特殊字符
            sheetName = sanitizeSheetName(sheetName);

            // 创建sheet
            Sheet sheet = workbook.createSheet(sheetName);

            // 创建标题行
            createHeaderRow(sheet);

            // 填充数据
            fillDataRows(sheet, paiGongList);

            // 自动调整列宽
            autoSizeColumns(sheet);

            // 添加筛选器
            sheet.setAutoFilter(new CellRangeAddress(0, sheet.getLastRowNum(), 0, 4));
        }

        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            System.out.println("Excel文件导出成功: " + filePath);
            System.out.println("共导出 " + paiGongSheetData.size() + " 个工作表");
        } finally {
            workbook.close();
        }
    }

    /**
     * 创建标题行
     */
    private static void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);

        String[] headers = {"产品编号", "工序名称", "开始日期", "结束日期", "标准工时"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    /**
     * 填充数据行
     */
    private static void fillDataRows(Sheet sheet, List<PaiGong> paiGongList) {
        int rowNum = 1;

        for (PaiGong pg : paiGongList) {
            Row row = sheet.createRow(rowNum++);

            // 产品编号
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(pg.prodNo != null ? pg.prodNo : "");

            // 工序名称
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(pg.taskName != null ? pg.taskName : "");

            // 开始日期
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(pg.startDate != null ? pg.startDate : "");

            // 结束日期
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(pg.endDate != null ? pg.endDate : "");

            // 标准工时
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(pg.biaoZhunGongShi);
        }
    }

    /**
     * 自动调整列宽
     */
    private static void autoSizeColumns(Sheet sheet) {
        int columnCount = 5; // 我们有5列数据

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);

            // 如果自动调整的宽度太窄，设置最小宽度
            int currentWidth = sheet.getColumnWidth(i);
            if (currentWidth < 3000) { // 约3个汉字宽度
                sheet.setColumnWidth(i, 4000);
            } else if (currentWidth > 15000) { // 限制最大宽度
                sheet.setColumnWidth(i, 15000);
            }
        }
    }

    /**
     * 清理sheet名称（Excel限制）
     */
    private static String sanitizeSheetName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Sheet1";
        }

        // Excel sheet名称限制
        // 1. 不能超过31个字符
        // 2. 不能包含字符: : \ / ? * [ ]
        // 3. 不能以'开头

        String sanitized = name.trim();

        // 截断长度
        if (sanitized.length() > 31) {
            sanitized = sanitized.substring(0, 31);
        }

        // 替换非法字符
        sanitized = sanitized.replaceAll("[:\\\\/?*\\[\\]]", "_");

        // 检查是否以'开头
        if (sanitized.startsWith("'")) {
            sanitized = "_" + sanitized.substring(1);
        }

        // 检查是否为空
        if (sanitized.isEmpty()) {
            sanitized = "Sheet";
        }

        return sanitized;
    }
}
