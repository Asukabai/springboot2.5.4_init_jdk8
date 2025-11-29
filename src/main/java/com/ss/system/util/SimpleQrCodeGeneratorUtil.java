package com.ss.system.util;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * @author Asukabai
 * @date 2025/11/28
 * @description 无
 */


public class SimpleQrCodeGeneratorUtil {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private static final String CHARSET = "UTF-8";
    private static final String FORMAT_NAME = "png";

    /**
     * 生成指定数量的二维码图片，内容遵循 A1L-1-xxx 格式
     * @param count 生成二维码的数量
     */
    public static void generateQrCodes(int count) {
        try {
            // 创建输出目录
            File outputDir = new File("D:/qr_codes");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // 循环生成二维码
            for (int i = 1; i <= count; i++) {
                // 生成内容 A1L-1-xxx 格式
                String content = String.format("A1R-4-%03d", i);

                // 生成文件名
                String fileName = content + ".png";
                File outputFile = new File(outputDir, fileName);

                // 生成二维码图片
                generateQrCodeImage(content, outputFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成单个二维码图片
     * @param content 二维码内容
     * @param outputFile 输出文件
     * @throws WriterException
     * @throws IOException
     */
    private static void generateQrCodeImage(String content, File outputFile)
            throws WriterException, IOException {

        // 设置二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);

        // 生成二维码矩阵
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);

        // 创建带编号显示的图片 (增加50像素用于显示文本)
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT + 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        // 填充白色背景
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, WIDTH, HEIGHT + 50);

        // 绘制二维码部分
        graphics.setColor(Color.BLACK);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (bitMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }

        // 添加文件名文字
        graphics.setColor(Color.BLACK);
        Font font = new Font("Arial", Font.PLAIN, 16);
        graphics.setFont(font);
        FontMetrics fm = graphics.getFontMetrics();
        int textWidth = fm.stringWidth(content);
        int textX = (WIDTH - textWidth) / 2;
        int textY = HEIGHT + 30;
        graphics.drawString(content, textX, textY);

        graphics.dispose();

        // 写入文件
        ImageIO.write(image, FORMAT_NAME, outputFile);
    }

    public static void main(String[] args) {
        SimpleQrCodeGeneratorUtil.generateQrCodes(54);
    }
}
  
