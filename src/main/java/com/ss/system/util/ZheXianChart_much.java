package com.ss.system.util;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Description : 在 ZheXianChart_Single 类的基础上修改为遍历整个文件夹中的某个 sheet 的某个区域的数据
 */

public class ZheXianChart_much {

    static {
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow", true));
    }

    public static void main(String[] args) {
        String folderPath = "C:\\Users\\32937\\Desktop\\7.12\\DataSource";
        List<String> excelFiles = listExcelFiles(folderPath);

        for (String fileName : excelFiles) {
            DataSetMinMax minMax = readExcelData(fileName);
            if (minMax != null) {
                JFreeChart lineChart = createLineChart(minMax.dataset, "Chart", "input_dBm", "output_dBm", minMax.minValue, minMax.maxValue);
                String outputFileName = fileName.replace(".xlsx", "_chart.png");
                displayChart(lineChart, outputFileName);
            } else {
                System.out.println("Excel 文件读取失败或数据格式不正确：" + fileName);
            }
        }
    }

    private static List<String> listExcelFiles(String folderPath) {
        List<String> excelFiles = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".xlsx")) {
                    excelFiles.add(file.getAbsolutePath());
                }
            }
        } else {
            System.out.println("指定路径不是一个有效的文件夹：" + folderPath);
        }

        return excelFiles;
    }

    private static DataSetMinMax readExcelData(String fileName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        try (FileInputStream fis = new FileInputStream(new File(fileName));
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheet("4540");
            if (sheet == null) {
                System.out.println("未找到名称为 '4540' 的 sheet 页面。");
                return null;
            }
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cellB = row.getCell(1); // B 列为第 2 列，索引为 1
                    Cell cellC = row.getCell(2); // C 列为第 3 列，索引为 2
                    if (cellB != null && cellC != null) {
                        String category;
                        double value;
                        if (cellB.getCellTypeEnum().equals(CellType.STRING)) {
                            category = cellB.getStringCellValue();
                        } else {
                            category = String.valueOf(cellB.getNumericCellValue());
                        }
                        if (cellC.getCellTypeEnum().equals(CellType.NUMERIC)) {
                            value = cellC.getNumericCellValue();
                        } else if (cellC.getCellTypeEnum().equals(CellType.STRING)) {
                            try {
                                value = Double.parseDouble(cellC.getStringCellValue());
                            } catch (NumberFormatException e) {
                                value = 0.000;
                            }
                        } else {
                            value = 0.000;
                        }
                        dataset.addValue(value, "product_change_line", category);
                        if (value < minValue) {
                            minValue = value;
                        }
                        if (value > maxValue) {
                            maxValue = value;
                        }
                    }
                }
            }
        } catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
            e.printStackTrace();
            return null;
        }
        return new DataSetMinMax(dataset, minValue, maxValue);
    }

    private static JFreeChart createLineChart(DefaultCategoryDataset dataset, String chartTitle, String xAxisLabel, String yAxisLabel, double minValue, double maxValue) {
        JFreeChart chart = ChartFactory.createLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL,
                true, // 是否显示图例
                true, // 是否生成工具提示
                false // 是否生成 URL 链接
        );

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setDefaultItemLabelGenerator(new LabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        chart.getCategoryPlot().setRenderer(renderer);
        chart.getCategoryPlot().getRangeAxis().setRange(minValue, maxValue + 0.1);
        return chart;
    }

    private static void displayChart(JFreeChart chart, String fileName) {
        SwingUtilities.invokeLater(() -> {
            ChartPanel panel = new ChartPanel(chart);
            panel.setPreferredSize(new java.awt.Dimension(800, 600));
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setVisible(true);

            // 保存图表到文件
            try {
                saveChartAsPNG(chart, fileName,800,600);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void saveChartAsPNG(JFreeChart chart, String outputPath,
                                  int weight, int height)throws Exception {
        FileOutputStream out = null;
        File outFile = new File(outputPath);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        out = new FileOutputStream(outputPath);
        // 保存为PNG
        BufferedImage image = chart.createBufferedImage(weight, height);
        ImageIO.write(image, "PNG", outFile);
        System.out.println("保存图表成功：" + outputPath);
        // 保存为JPEG
        // ChartUtilities.writeChartAsJPEG(out, chart, weight, height);
        out.flush();
        try {
            out.close();
        } catch (IOException e) {
            System.out.println("保存图表失败：" + e.getMessage());
        }
    }


    private static class DataSetMinMax {
        DefaultCategoryDataset dataset;
        double minValue;
        double maxValue;

        DataSetMinMax(DefaultCategoryDataset dataset, double minValue, double maxValue) {
            this.dataset = dataset;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
    }

    private static class LabelGenerator extends org.jfree.chart.labels.AbstractCategoryItemLabelGenerator
            implements org.jfree.chart.labels.CategoryItemLabelGenerator {
        LabelGenerator() {
            super("", new DecimalFormat("0.000"));
        }

        @Override
        public String generateLabel(CategoryDataset dataset, int row, int column) {
            Number num = dataset.getValue(row, column);
            return (num != null ? num.toString() : "");
        }
    }
}