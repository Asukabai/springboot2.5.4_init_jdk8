package com.ss.system.util;

import com.ss.excel.factory.SSExcel07Workbook;
import com.ss.excel.factory.cell.SSExcel07Cell;
import com.ss.excel.factory.sheet.SSExcel07Sheet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName MergeExcel_EPC1
 * @Description 合并Excel数据工具类，支持按产品编号统一数据源策略
 * @Version 1.0
 */
public class MergeExcel_EPC1 {

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
//                    if (cellValue != null & cellValue.trim().equals(searchText)) {
//                        return cell;
//                    }
//当 cellValue 为 null 时，使用单个 & 操作符仍会执行 cellValue.trim().equals(searchText)
//调用 null.trim() 会抛出
//                    && 是短路逻辑与运算符
//  如果第一个操作数为false，就不会计算第二个操作数
//  这种短路特性可以提高性能并避免不必要的计算
//  & 是按位与运算符，也可以用作逻辑与
//  即使第一个操作数为false，也会计算第二个操作数
//  不具备短路特性，会执行所有操作数的计算
                    // 方式1：使用短路逻辑运算符（推荐）
                    if (cellValue != null && cellValue.trim().equals(searchText)) {
                        return cell;
                    }

                    // 方式2：分离判断逻辑
//                    if (cellValue != null) {
//                        if (cellValue.trim().equals(searchText)) {
//                            return cell;
//                        }
//                    }
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
        String excelPath = "H:\\2311工作量以及绩效-EPC.xlsx";
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

            // 第一步：分析每个产品编号应该从哪一列获取数据
            Map<String, String> prodNoSourceMap = analyzeDataSource(sheet);

            // 新增：用于存储从D列获取数据的产品编号的最新记录
            Map<String, Map<String, PaiGongDRecord>> dColumnLatestRecords = new HashMap<>();

            // 第二步：收集数据
            for (int i = 1; i <= 100; i++) {
                SSExcel07Cell cell = sheet.getCell(i, 2);
                if (cell == null || cell.getCellValue() == null
                        || cell.getCellValue().toString().length() == 0) {
                    continue;
                }
                String date = null; // A列：日期
                String productNo = null; // B列：产品编号
                String taskName = null; // C列：工序名称
                Double biaoZhunGongShi = null; // 标准工时（从D或E列获取）
                try {
                    // 读取日期 (A列)，正确处理Excel日期格式
                    Object dateObj = sheet.getCell(i, 0).getCellValue();
                    if (dateObj != null) {
                        SSExcel07Cell dateCell = sheet.getCell(i, 0);
                        if (dateCell != null && dateCell.getXSSFCell() != null) {
                            Cell xssfCell = dateCell.getXSSFCell();
                            if (xssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                if (DateUtil.isCellDateFormatted(xssfCell)) {
                                    // 如果是日期格式，格式化为字符串
                                    Date dateValue = xssfCell.getDateCellValue();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                    date = sdf.format(dateValue);
                                    System.out.println("原始日期单元格值: " + xssfCell.getNumericCellValue() + ", 转换后: " + date);
                                } else {
                                    // 如果是数字但不是日期格式，当作序列号处理
                                    double serialNumber = xssfCell.getNumericCellValue();
                                    if (serialNumber > 2) {
                                        long millis = Math.round((serialNumber - 25569) * 24 * 60 * 60 * 1000);
                                        Date dateValue = new Date(millis);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                        date = sdf.format(dateValue);
                                        System.out.println("序列号: " + serialNumber + ", 转换后: " + date);
                                    } else {
                                        date = String.valueOf(serialNumber);
                                        System.out.println("小数值: " + serialNumber + ", 直接使用: " + date);
                                    }
                                }
                            } else {
                                date = dateObj.toString();
                                System.out.println("非数字日期值: " + date);
                            }
                        } else {
                            date = dateObj.toString();
                            System.out.println("普通日期值: " + date);
                        }
                    }

                    productNo = sheet.getCell(i, 1).getCellValue().toString().trim();
                    taskName = sheet.getCell(i, 2).getCellValue().toString().replaceAll("\\d*\\.?\\d+$", "");

                    // 根据产品编号确定应该从哪一列获取数据
                    String dataSource = prodNoSourceMap.getOrDefault(productNo, "D"); // 默认从D列获取

                    // 获取D列和E列的值
                    Double dColumnValue = parseCellValue(sheet.getCell(i, 3));
                    Double eColumnValue = parseCellValue(sheet.getCell(i, 4));

                    // 根据数据源策略选择工时数据
                    if ("D".equals(dataSource)) {
                        biaoZhunGongShi = dColumnValue;
                    } else {
                        biaoZhunGongShi = eColumnValue;
                    }

                    if (biaoZhunGongShi == null || biaoZhunGongShi <= 0) {
                        System.err.println("第" + (i+1) + "行标准工时为空或无效，产品编号: " + productNo +
                                ", D列: " + dColumnValue + ", E列: " + eColumnValue +
                                ", 数据源: " + dataSource);
                        continue; // 如果标准工时为空，跳过该行
                    }

                } catch (NullPointerException | NumberFormatException e) {
                    System.err.println("处理第" + (i+1) + "行时发生异常: " + e.getMessage());
                    continue; // 发生异常时跳过该行
                }
                //没有前3列数据为无效数据
                if (date == null || productNo == null || taskName == null) {
                    continue;
                }

                // 如果是从D列获取数据，则记录最新日期的数据
                if ("D".equals(prodNoSourceMap.getOrDefault(productNo, "D"))) {
                    // 初始化产品编号的记录映射
                    dColumnLatestRecords.putIfAbsent(productNo, new HashMap<>());
                    Map<String, PaiGongDRecord> taskRecords = dColumnLatestRecords.get(productNo);

                    // 获取当前任务已有的记录
                    PaiGongDRecord existingRecord = taskRecords.get(taskName);

                    // 创建当前记录
                    PaiGongDRecord currentRecord = new PaiGongDRecord(date, biaoZhunGongShi);

                    // 如果没有记录或者当前日期更新，则更新记录
                    if (existingRecord == null || isDateAfter(date, existingRecord.getDate())) {
                        taskRecords.put(taskName, currentRecord);
                    }
                } else {
                    // 如果是从E列获取数据，则累加工时（原有逻辑）
                    boolean found = false;
                    for (PaiGong pg : paiGongs) {
                        if (pg.getProdNo() != null && pg.getTaskName() != null &&
                                pg.getProdNo().trim().equals(productNo.trim()) &&
                                pg.getTaskName().trim().equals(taskName.trim())) {

                            // 更新结束日期
                            pg.setEndDate(date);

                            // 累计标准工时
                            pg.setBiaoZhunGongShi(pg.getBiaoZhunGongShi() + biaoZhunGongShi);

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
            }

            // 处理从D列获取的数据记录（取最新日期的数据）
            for (Map.Entry<String, Map<String, PaiGongDRecord>> prodEntry : dColumnLatestRecords.entrySet()) {
                String prodNo = prodEntry.getKey();
                Map<String, PaiGongDRecord> taskRecords = prodEntry.getValue();

                for (Map.Entry<String, PaiGongDRecord> taskEntry : taskRecords.entrySet()) {
                    String taskName = taskEntry.getKey();
                    PaiGongDRecord record = taskEntry.getValue();

                    PaiGong newPg = new PaiGong();
                    newPg.setProdNo(prodNo);
                    newPg.setTaskName(taskName);
                    newPg.setStartDate(record.getDate());
                    newPg.setEndDate(record.getDate());
                    newPg.setBiaoZhunGongShi(record.getBiaoZhunGongShi());
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
     * 判断日期date1是否晚于date2
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 如果date1晚于date2返回true，否则返回false
     */
    private static boolean isDateAfter(String date1, String date2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            return d1.after(d2);
        } catch (Exception e) {
            // 如果解析失败，简单比较字符串
            return date1.compareTo(date2) > 0;
        }
    }

    /**
     * 分析每个产品编号应该从哪一列获取数据
     * 策略：如果某个产品编号在任意一行的D列有有效数据，则该产品编号的所有行都从D列获取数据
     *       只有当该产品编号的所有行在D列都没有有效数据时，才从E列获取数据
     *
     * @param sheet 工作表对象
     * @return 产品编号到数据源列的映射关系
     */
    private static Map<String, String> analyzeDataSource(SSExcel07Sheet sheet) {
        Map<String, String> prodNoSourceMap = new HashMap<>();
        Map<String, Boolean> prodNoHasDData = new HashMap<>(); // 记录产品编号是否在D列有数据

        // 第一遍扫描：检查每个产品编号在D列是否有数据
        for (int i = 1; i <= 100; i++) {
            SSExcel07Cell cell = sheet.getCell(i, 2);
            if (cell == null || cell.getCellValue() == null
                    || cell.getCellValue().toString().length() == 0) {
                continue;
            }

            try {
                String productNo = sheet.getCell(i, 1).getCellValue().toString().trim();

                // 获取D列的值
                Double dColumnValue = parseCellValue(sheet.getCell(i, 3));

                // 如果该产品编号尚未记录是否有D列数据，则初始化
                if (!prodNoHasDData.containsKey(productNo)) {
                    prodNoHasDData.put(productNo, dColumnValue != null && dColumnValue > 0);
                }
                // 如果之前记录为false，但现在发现有数据，则更新为true
                else if (!prodNoHasDData.get(productNo) && dColumnValue != null && dColumnValue > 0) {
                    prodNoHasDData.put(productNo, true);
                }
            } catch (Exception e) {
                // 忽略异常，继续处理下一行
            }
        }

        // 第二遍扫描：根据检查结果确定每个产品编号的数据源
        for (int i = 1; i <= 100; i++) {
            SSExcel07Cell cell = sheet.getCell(i, 2);
            if (cell == null || cell.getCellValue() == null
                    || cell.getCellValue().toString().length() == 0) {
                continue;
            }

            try {
                String productNo = sheet.getCell(i, 1).getCellValue().toString().trim();

                // 如果该产品编号还没有确定数据源
                if (!prodNoSourceMap.containsKey(productNo)) {
                    // 如果该产品编号在D列有数据，则从D列获取
                    if (prodNoHasDData.getOrDefault(productNo, false)) {
                        prodNoSourceMap.put(productNo, "D");
                    } else {
                        // 否则从E列获取
                        prodNoSourceMap.put(productNo, "E");
                    }
                }
            } catch (Exception e) {
                // 忽略异常
            }
        }

        return prodNoSourceMap;
    }

    /**
     * 解析单元格值为Double类型
     *
     * @param cell 单元格对象
     * @return 解析后的Double值，如果无法解析则返回null
     */
    private static Double parseCellValue(SSExcel07Cell cell) {
        if (cell == null || cell.getCellValue() == null) {
            return null;
        }

        Object cellValue = cell.getCellValue();
        if (cellValue instanceof Number) {
            return ((Number) cellValue).doubleValue();
        } else {
            String strValue = cellValue.toString();
            strValue = strValue.replaceAll("_", "").replaceAll("[^\\d.]", "");
            if (!strValue.trim().isEmpty()) {
                try {
                    return Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    System.err.println("数据解析失败: '" + strValue + "'");
                }
            }
        }
        return null;
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

    /**
     * 用于存储从D列获取数据时的记录信息
     */
    @Data
    @AllArgsConstructor
    static class PaiGongDRecord {
        private String date;
        private double biaoZhunGongShi;
    }
}
