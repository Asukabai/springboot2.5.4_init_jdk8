package com.ss.system.util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * +---------------------------+
 * | 程序入口：main 方法       |
 * | 1. 初始化根目录 File 对象 |
 * | 2. 调用 processDirectory |
 * +---------------------------+
 *             |
 *             v
 * +---------------------------+
 * | processDirectory 方法     |
 * | 1. 判断是否为目录         |
 * |    是 -> 遍历子文件/目录   |
 * |        - 子目录递归调用   |
 * |        - entry.json 文件  |
 * |          调用 processJsonFile |
 * |    否 -> 结束             |
 * +---------------------------+
 *             |
 *             v
 * +---------------------------+
 * | processJsonFile 方法      |
 * | 1. 检查 entry.json 文件是否存在 |
 * |    否 -> 输出错误信息并结束 |
 * |    是 -> 继续处理         |
 * | 2. 读取 JSON 文件内容     |
 * | 3. 解析 JSON 数据         |
 * |    - 提取 owner_name      |
 * |    - 提取 download_subtitle|
 * |    - 提取 part            |
 * | 4. 判空逻辑               |
 * |    - 如果为空，设置默认值  |
 * | 5. 构造新文件名           |
 * | 6. 调用 searchAndRenameFiles |
 * | 7. 调用 copyMp3Files      |
 * +---------------------------+
 *             |
 *             v
 * +---------------------------+
 * | searchAndRenameFiles 方法 |
 * | 1. 遍历目录下的所有文件   |
 * |    - 子目录递归调用       |
 * |    - video.m4s 文件重命名 |
 * |      调用 renameFileIfNeeded |
 * |    - audio.m4s 文件重命名 |
 * |      调用 renameFileIfNeeded |
 * +---------------------------+
 *             |
 *             v
 * +---------------------------+
 * | renameFileIfNeeded 方法   |
 * | 1. 检查文件是否存在       |
 * |    否 -> 输出错误信息     |
 * |    是 -> 执行重命名操作   |
 * | 2. 输出重命名结果         |
 * +---------------------------+
 *             |
 *             v
 * +---------------------------+
 * | copyMp3Files 方法         |
 * | 1. 遍历目录下的所有文件   |
 * |    - 子目录递归调用       |
 * |    - MP3 文件复制         |
 * |      调用 copyMp3File     |
 * +---------------------------+
 *             |
 *             v
 * +---------------------------+
 * | copyMp3File 方法          |
 * | 1. 检查目标目录是否存在   |
 * |    否 -> 创建目标目录     |
 * | 2. 执行文件复制操作       |
 * | 3. 输出复制结果           |
 * +---------------------------+
 */
@Slf4j
public class BilibiliUtils {
    public static void main(String[] args) {
        File rootDir = new File("D:\\UP");
        processDirectory(rootDir);// 调用方法递归处理该目录
    }

    private static void processDirectory(File dir) { // 定义递归处理目录的方法
        if (dir.isDirectory()) {// 判断是否为目录
            File[] files = dir.listFiles(); // 获取目录下的所有文件和子目录
            if (files != null) {// 如果文件数组不为空
                for (File file : files) {// 遍历每个文件/目录
                    if (file.isDirectory()) {// 如果是子目录
                        processDirectory(file); // 递归调用处理子目录
                    } else if (file.getName().equalsIgnoreCase("entry.json")) {// 如果是 entry.json 文件
                        processJsonFile(dir); // 调用方法处理 JSON 文件
                    }
                }
            }
        }
    }

    private static void processJsonFile(File jsonDir) {// 定义处理 JSON 文件的方法
        File jsonFile = new File(jsonDir, "entry.json"); // 构造 entry.json 文件路径
        if (jsonFile.exists()) {// 检查文件是否存在
            try (FileReader reader = new FileReader(jsonFile);// 使用 FileReader 读取文件
                Scanner scanner = new Scanner(reader)) { // 使用 Scanner 逐行读取文件内容
                String jsonString = scanner.useDelimiter("\\A").next();// 将整个文件内容读取为字符串
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject(); // 解析 JSON 字符串为 JsonObject
                // 获取 owner_name
                String ownerName = jsonObject.get("owner_name") != null ? jsonObject.get("owner_name").getAsString() : "";
                // 获取 download_subtitle
                String downloadSubtitle = jsonObject.getAsJsonObject("page_data").get("download_subtitle") != null
                        ? jsonObject.getAsJsonObject("page_data").get("download_subtitle").getAsString()
                        : "";
                // 获取 part
                String part = jsonObject.getAsJsonObject("page_data").get("part") != null
                        ? jsonObject.getAsJsonObject("page_data").get("part").getAsString()
                        : "";
                // 判空逻辑
                if (downloadSubtitle.isEmpty()) {// 如果 download_subtitle 为空
                    System.out.println("Warning: download_subtitle is empty.");// 输出警告信息
                    log.error("download_subtitle is empty.");
                    downloadSubtitle = "Unknown_Title"; // 可以设置默认值
                }
                if (ownerName.isEmpty()) {
                    log.error("owner_name is empty.");
                    ownerName = "Unknown_Owner"; // 可以设置默认值
                }
                if (part.isEmpty()) {
                    System.out.println("Warning: part is empty.");
                    log.error("part is empty.");
                    part = "Unknown_Part"; // 可以设置默认值
                }
                // 构造新的文件名
                String newBaseName = downloadSubtitle + " - " + ownerName + " - " + part;
                System.out.println("New base name: " + newBaseName);
                // 重命名文件
                searchAndRenameFiles(jsonDir, newBaseName);
                // 复制MP3文件
                copyMp3Files(jsonDir);
                copyMp4Files(jsonDir); // 新增：复制 MP4 文件
            } catch (IOException e) {
                System.out.println("Failed to read or process JSON file: " + e.getMessage());
            }
        } else {
            System.out.println("entry.json not found in directory: " + jsonDir.getAbsolutePath());
        }
    }

    private static void copyMp4Files(File startDir) { // 新增：复制 MP4 文件
        File[] files = startDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    copyMp4Files(file); // 递归调用处理子目录
                } else if (file.getName().toLowerCase().endsWith(".mp4")) {
                    copyFileToDestination(file); // 调用通用复制方法
                }
            }
        }
    }

    private static void copyFileToDestination(File sourceFile) { // 通用文件复制方法
        File destDir = new File("D:\\UP");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File destFile = new File(destDir, sourceFile.getName());
        try {
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied: " + sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to copy: " + sourceFile.getAbsolutePath() + " - " + e.getMessage());
        }
    }

    private static void searchAndRenameFiles(File startDir, String newBaseName) {// 定义搜索并重命名文件的方法
        File[] files = startDir.listFiles();// 获取目录下的所有文件和子目录
        if (files != null) { // 如果文件数组不为空
            for (File file : files) { // 遍历每个文件/目录
                if (file.isDirectory()) {// 如果是子目录
                    searchAndRenameFiles(file, newBaseName);// 递归调用处理子目录
                } else if (file.getName().equalsIgnoreCase("video.m4s")) {// 如果是 video.m4s 文件
                    renameFileIfNeeded(file, newBaseName + ".mp4"); // 调用方法重命名为 MP4 文件
                } else if (file.getName().equalsIgnoreCase("audio.m4s")) {// 如果是 audio.m4s 文件
                    renameFileIfNeeded(file, newBaseName + ".mp3"); // 调用方法重命名为 MP3 文件
                }
            }
        }
    }

    private static void renameFileIfNeeded(File file, String newName) { // 定义重命名文件的方法
        if (file.exists()) {// 检查文件是否存在
            File newFile = new File(file.getParent(), newName);// 构造新文件路径
            boolean success = file.renameTo(newFile); // 尝试重命名文件
            if (success) {// 如果重命名成功
                System.out.println("Renamed to: " + newFile.getAbsolutePath());// 输出成功信息
            } else {
                System.out.println("Failed to rename: " + file.getAbsolutePath());// 输出失败信息
            }
        } else {
            System.out.println("File not found: " + file.getAbsolutePath()); // 如果文件不存在，输出提示
        }
    }

    private static void copyMp3Files(File startDir) {// 定义复制 MP3 文件的方法
        File[] files = startDir.listFiles(); // 获取目录下的所有文件和子目录
        if (files != null) {// 如果文件数组不为空
            for (File file : files) {// 遍历每个文件/目录
                if (file.isDirectory()) {// 如果是子目录
                    copyMp3Files(file);// 递归调用处理子目录
                } else if (file.getName().toLowerCase().endsWith(".mp3")) {// 如果是 MP3 文件
                    copyFileToDestination(file); // 调用通用复制方法
                }
            }
        }
    }

//    private static void copyMp3File(File mp3File) {// 定义复制单个 MP3 文件的方法
//        File destDir = new File("D:\\UP"); // 定义目标目录路径
//        if (!destDir.exists()) {// 如果目标目录不存在
//            destDir.mkdirs();// 创建目标目录
//        }
//        File destFile = new File(destDir, mp3File.getName()); // 构造目标文件路径
//        try {
//            Files.copy(mp3File.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);// 复制文件并覆盖已有文件
//            System.out.println("Copied: " + mp3File.getAbsolutePath() + " to " + destFile.getAbsolutePath());
//        } catch (IOException e) {
//            System.out.println("Failed to copy: " + mp3File.getAbsolutePath() + " - " + e.getMessage());
//        }
//    }
}

