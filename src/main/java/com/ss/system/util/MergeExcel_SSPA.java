//package com.ss.system.util;
//
//import com.ss.excel.factory.SSExcel07Workbook;
//import com.ss.excel.factory.cell.SSExcel07Cell;
//import com.ss.excel.factory.sheet.SSExcel07Sheet;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @ClassName MergeExcel_SSPA
// * @Description 合并SSPA绩效Excel文件工具类
// * @Version 1.0
// */
//public class MergeExcel_SSPA {
//
//    /**
//     * 查找包含指定字符串的所有单元格坐标
//     *
//     * @param searchText 要查找的文本
//     * @return 单元格坐标列表 (格式: Sheet名称!A1)
//     */
//    public static Cell findCellsWithText(SSExcel07Sheet sheet, String searchText) {
//        // 遍历工作表中的所有行和单元格
//        for (Row row : sheet.getXSSFSheet()) {
//            for (Cell cell : row) {
//                // 判断单元格类型为字符串
//                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
//                    String cellValue = cell.getStringCellValue();
//                    // 检查字符串值是否匹配搜索文本
//                    if (cellValue != null & cellValue.trim().equals(searchText)) {
//                        return cell;
//                    }
//                } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
//                    // 如果是数字格式，转换为字符串查找
//                    String cellValue = String.valueOf(cell.getNumericCellValue());
//                    if (cellValue.contains(searchText)) {
//                        return cell;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 派工数据实体类
//     */
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    static class PaiGong {
//        String prodNo;          // 产品编号
//        String taskName;        // 工序名称
//        String startDate;       // 开始日期
//        String endDate;         // 结束日期
//        double biaoZhunGongShi = 0.0; // 标准工时，默认值为0.0
//    }
//
//    /**
//     * 主函数 - 处理Excel文件并导出结果
//     */
//    public static void main(String[] args) {
//        // 定义Excel文件路径
////        String excelPath = "D:\\浏览器下载\\201\\2022年绩效\\2201绩效\\2201绩效-SSPA.xlsx";
//        String excelPath = "H:\\2012绩效-SSPA.xlsx";
//
//        // 打开Excel工作簿
//        SSExcel07Workbook workbook = new SSExcel07Workbook().open(excelPath);
//
//        // 获取所有工作表名称
//        List<String> sheetNames = workbook.getAllSheetNames();
//
//        // 存储每个工作表处理后的数据
//        HashMap<String, List<PaiGong>> paiGongSheetData = new HashMap<>();
//
//        // 注释掉的代码可用于移除特定工作表
////        sheetNames.remove("总体绩效");
////        sheetNames.remove("组员合计");
////        sheetNames.remove("组长-王宇栋");
//
//        // 遍历每个工作表
//        for (String sheetName : sheetNames) {
//            SSExcel07Sheet sheet = workbook.getSheet(sheetName);
//            // 为当前工作表创建派工数据列表
//            ArrayList<PaiGong> paiGongs = new ArrayList<>();
//
//            // 处理最多100行数据（从第2行开始）
//            for (int i = 1; i <= 100; i++) {
//                // 检查C列是否有数据，无数据则跳过该行
//                SSExcel07Cell cell = sheet.getCell(i, 2);
//                if (cell == null || cell.getCellValue() == null
//                        || cell.getCellValue().toString().length() == 0) {
//                    continue;
//                }
//
//                // 初始化各列数据变量
//                String date = null; // A列：日期
//                String productNo = null; // B列：产品编号
//                String taskName = null; // C列：工序名称
//                Integer biaoZhunGongShi = null; // D列：标准工时
//
//                // 改进的数据读取部分
//                try {
//                    // 读取日期 (A列)，正确处理Excel日期格式
//                    Object dateObj = sheet.getCell(i, 0).getCellValue();
//                    if (dateObj != null) {
//                        // 检查是否为日期类型
//                        SSExcel07Cell dateCell = sheet.getCell(i, 0);
//                        if (dateCell != null && dateCell.getXSSFCell() != null) {
//                            Cell xssfCell = dateCell.getXSSFCell();
//                            if (xssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC &&
//                                    DateUtil.isCellDateFormatted(xssfCell)) {
//                                // 如果是日期格式，格式化为字符串
//                                Date dateValue = xssfCell.getDateCellValue();
//                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//                                date = sdf.format(dateValue);
//                            } else {
//                                date = dateObj.toString();
//                            }
//                        } else {
//                            date = dateObj.toString();
//                        }
//                    }
//
//                    // 读取产品编号
//                    Object productObj = sheet.getCell(i, 1).getCellValue();
//                    productNo = (productObj != null) ? productObj.toString() : null;
//
//                    // 读取工序名称并移除末尾数字
//                    Object taskObj = sheet.getCell(i, 2).getCellValue();
//                    if (taskObj != null) {
//                        taskName = taskObj.toString().replaceAll("\\d*\\.?\\d+$", "");
//                    }
//
//                    // 读取标准工时
//                    Object gongShiObj = sheet.getCell(i, 3).getCellValue();
//                    if (gongShiObj != null) {
//                        String s = gongShiObj.toString().replaceAll("_", "").trim();
//                        if (!s.isEmpty()) {
//                            biaoZhunGongShi = Double.valueOf(s).intValue();
//                        }
//                    }
//                } catch (Exception e) {
//                    // 记录异常但继续处理其他行
//                    System.err.println("处理第" + (i+1) + "行数据时出现异常: " + e.getMessage());
//                }
//
//                // 没有前三列数据为无效数据，跳过
//                if (date == null || productNo == null || taskName == null) {
//                    continue;
//                }
//
//                // 查找是否已存在相同产品和工序的记录
//                boolean found = false;
//                for (PaiGong pg : paiGongs) {
//                    // 比较产品编号和工序名称是否相同
//                    if (pg.getProdNo() != null && pg.getTaskName() != null &&
//                            pg.getProdNo().trim().equals(productNo.trim()) &&
//                            pg.getTaskName().trim().equals(taskName.trim())) {
//
//                        // 更新结束日期
//                        pg.setEndDate(date);
//
//                        // 更新标准工时（覆盖原有值），处理null情况
//                        pg.setBiaoZhunGongShi(biaoZhunGongShi != null ? biaoZhunGongShi : pg.getBiaoZhunGongShi());
//
//                        found = true;
//                        break;
//                    }
//                }
//
//                // 如果没找到相同记录，创建新记录
//                if (!found) {
//                    PaiGong newPg = new PaiGong();
//                    newPg.setProdNo(productNo.trim());        // 设置产品编号
//                    newPg.setTaskName(taskName.trim());       // 设置工序名称
//                    newPg.setStartDate(date);                 // 设置开始日期
//                    newPg.setEndDate(date);                   // 设置结束日期
//                    // 设置标准工时，处理null情况
//                    newPg.setBiaoZhunGongShi(biaoZhunGongShi != null ? biaoZhunGongShi : 0.0);
//                    paiGongs.add(newPg);                      // 添加到列表
//                }
//            }
//
//            // 将当前工作表处理后的数据存入总数据集合
//            paiGongSheetData.put(sheetName, paiGongs);
//        }
//
//        // 打印处理后的数据
//        System.out.println(paiGongSheetData);
//
//        // 导出数据到新的Excel文件
//        try {
//            exportToExcel(paiGongSheetData, excelPath + "_create.xlsx");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 将 paiGongSheetData 导出为 Excel 文件
//     *
//     * @param paiGongSheetData 数据源
//     * @param filePath         输出文件路径
//     */
//    public static void exportToExcel(HashMap<String, List<PaiGong>> paiGongSheetData,
//                                     String filePath) throws IOException {
//        // 创建新的Excel工作簿
//        Workbook workbook = new XSSFWorkbook();
//
//        // 遍历每个工作表的数据
//        for (Map.Entry<String, List<PaiGong>> entry : paiGongSheetData.entrySet()) {
//            String sheetName = entry.getKey();
//            List<PaiGong> paiGongList = entry.getValue();
//
//            // 处理工作表名称，避免特殊字符
//            sheetName = sanitizeSheetName(sheetName);
//
//            // 创建工作表
//            Sheet sheet = workbook.createSheet(sheetName);
//
//            // 创建标题行
//            createHeaderRow(sheet);
//
//            // 填充数据行
//            fillDataRows(sheet, paiGongList);
//
//            // 自动调整列宽
//            autoSizeColumns(sheet);
//
//            // 添加筛选器
//            sheet.setAutoFilter(new CellRangeAddress(0, sheet.getLastRowNum(), 0, 4));
//        }
//
//        // 写入文件
//        try (FileOutputStream fos = new FileOutputStream(filePath)) {
//            workbook.write(fos);
//            System.out.println("Excel文件导出成功: " + filePath);
//            System.out.println("共导出 " + paiGongSheetData.size() + " 个工作表");
//        } finally {
//            workbook.close();
//        }
//    }
//
//    /**
//     * 创建标题行
//     */
//    private static void createHeaderRow(Sheet sheet) {
//        // 创建第一行作为标题行
//        Row headerRow = sheet.createRow(0);
//
//        // 定义标题数组
//        String[] headers = {"产品编号", "工序名称", "开始日期", "结束日期", "标准工时"};
//
//        // 为每个标题创建单元格并设置值
//        for (int i = 0; i < headers.length; i++) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(headers[i]);
//        }
//    }
//
//    /**
//     * 填充数据行
//     */
//    private static void fillDataRows(Sheet sheet, List<PaiGong> paiGongList) {
//        // 从第二行开始填充数据
//        int rowNum = 1;
//
//        // 创建日期样式
//        CellStyle dateCellStyle = sheet.getWorkbook().createCellStyle();
//        CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
//        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd"));
//
//        // 遍历所有派工数据
//        for (PaiGong pg : paiGongList) {
//            Row row = sheet.createRow(rowNum++);
//
//            // 产品编号
//            Cell cell0 = row.createCell(0);
//            cell0.setCellValue(pg.prodNo != null ? pg.prodNo : "");
//
//            // 工序名称
//            Cell cell1 = row.createCell(1);
//            cell1.setCellValue(pg.taskName != null ? pg.taskName : "");
//
//            // 开始日期
//            Cell cell2 = row.createCell(2);
//            if (pg.startDate != null && !pg.startDate.isEmpty()) {
//                // 尝试解析日期字符串并设置为日期格式
//                try {
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//                    Date date = sdf.parse(pg.startDate);
//                    cell2.setCellValue(date);
//                    cell2.setCellStyle(dateCellStyle);
//                } catch (ParseException e) {
//                    cell2.setCellValue(pg.startDate);
//                }
//            }
//
//            // 结束日期
//            Cell cell3 = row.createCell(3);
//            if (pg.endDate != null && !pg.endDate.isEmpty()) {
//                // 尝试解析日期字符串并设置为日期格式
//                try {
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//                    Date date = sdf.parse(pg.endDate);
//                    cell3.setCellValue(date);
//                    cell3.setCellStyle(dateCellStyle);
//                } catch (ParseException e) {
//                    cell3.setCellValue(pg.endDate);
//                }
//            }
//
//            // 标准工时
//            Cell cell4 = row.createCell(4);
//            cell4.setCellValue(pg.biaoZhunGongShi);
//        }
//    }
//
//    /**
//     * 自动调整列宽
//     */
//    private static void autoSizeColumns(Sheet sheet) {
//        int columnCount = 5; // 我们有5列数据
//
//        // 遍历所有列并调整宽度
//        for (int i = 0; i < columnCount; i++) {
//            sheet.autoSizeColumn(i);
//
//            // 如果自动调整的宽度太窄，设置最小宽度
//            int currentWidth = sheet.getColumnWidth(i);
//            if (currentWidth < 3000) { // 约3个汉字宽度
//                sheet.setColumnWidth(i, 4000);
//            } else if (currentWidth > 15000) { // 限制最大宽度
//                sheet.setColumnWidth(i, 15000);
//            }
//        }
//    }
//
//    /**
//     * 清理工作表名称（符合Excel限制）
//     */
//    private static String sanitizeSheetName(String name) {
//        // 处理空名称情况
//        if (name == null || name.trim().isEmpty()) {
//            return "Sheet1";
//        }
//
//        // Excel工作表名称限制:
//        // 1. 不能超过31个字符
//        // 2. 不能包含字符: : \ / ? * [ ]
//        // 3. 不能以'开头
//
//        String sanitized = name.trim();
//
//        // 截断长度至31个字符
//        if (sanitized.length() > 31) {
//            sanitized = sanitized.substring(0, 31);
//        }
//
//        // 替换非法字符为下划线
//        sanitized = sanitized.replaceAll("[:\\\\/?*\\[\\]]", "_");
//
//        // 检查是否以'开头，如果是则替换为下划线
//        if (sanitized.startsWith("'")) {
//            sanitized = "_" + sanitized.substring(1);
//        }
//
//        // 检查是否为空
//        if (sanitized.isEmpty()) {
//            sanitized = "Sheet";
//        }
//
//        return sanitized;
//    }
//}
