package com.ss.system.util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;


public class BilibiliUtils {

    public static void main(String[] args) {

        // 创建一个 File 对象，指向 "D:\\UP" 目录，这是程序开始搜索的根目录。
        File rootDir = new File("D:\\UP");
        // 调用 processDirectory 方法来处理该目录及其子目录。
        processDirectory(rootDir);
    }

    // 声明一个私有的静态方法 processDirectory，接受一个 File 对象作为参数。
    private static void processDirectory(File dir) {
        // if (dir.isDirectory()) 确保传入的 File 对象是一个目录。
        if (dir.isDirectory()) {
            // dir.listFiles() 返回一个 File 数组，包含目录中的所有文件和子目录。
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 递归处理子目录：如果是子目录，递归调用 processDirectory 方法来进一步处理。
                    if (file.isDirectory()) {
                        processDirectory(file);
                        // 如果找到名为 entry.json 的文件，调用 processJsonFile 方法进行处理。
                    } else if (file.getName().equalsIgnoreCase("entry.json")) {
                        processJsonFile(dir);
                    }
                }
            }
        }
    }


    // 声明一个私有的静态方法 processJsonFile，接受一个 File 对象作为参数，这个 File 对象应指向包含 entry.json 文件的目录。
    private static void processJsonFile(File jsonDir) {
        // new File(jsonDir, "entry.json") 创建一个指向 entry.json 文件的 File 对象。
        File jsonFile = new File(jsonDir, "entry.json");
        if (jsonFile.exists()) {
            // 使用 FileReader 和 Scanner 读取文件内容。
            try (FileReader reader = new FileReader(jsonFile);
                 Scanner scanner = new Scanner(reader)) {
                // scanner.useDelimiter("\\A").next() 读取整个文件内容为一个字符串。
                String jsonString = scanner.useDelimiter("\\A").next();
                // 使用 JsonParser 解析 JSON 字符串，并转换为 JsonObject。
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                String title = jsonObject.get("title").getAsString();
                String ownerName = jsonObject.get("owner_name").getAsString();
                String newBaseName = title + " - " + ownerName;
                System.out.println("New base name: " + newBaseName);
                // 调用 searchAndRenameFiles 方法，在当前目录中查找 video.m4s 和 audio.m4s 文件并进行重命名。
                searchAndRenameFiles(jsonDir, newBaseName);
            } catch (IOException e) {
                System.out.println("Failed to read or process JSON file: " + e.getMessage());
            }
        } else {
            System.out.println("entry.json not found in directory: " + jsonDir.getAbsolutePath());
        }
    }


    private static void searchAndRenameFiles(File startDir, String newBaseName) {
        File[] files = startDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchAndRenameFiles(file, newBaseName);
                } else if (file.getName().equalsIgnoreCase("video.m4s")) {
                    renameFileIfNeeded(file, newBaseName + ".mp4");
                } else if (file.getName().equalsIgnoreCase("audio.m4s")) {
                    renameFileIfNeeded(file, newBaseName + ".mp3");
                } else if (file.getName().toLowerCase().endsWith(".mp3")) {
                    copyMp3File(file);
                }
            }
        }
    }
    private static void renameFileIfNeeded(File file, String newName) {
        if (file.exists()) {
            File newFile = new File(file.getParent(), newName);
            boolean success = file.renameTo(newFile);
            if (success) {
                System.out.println("Renamed to: " + newFile.getAbsolutePath());
            } else {
                System.out.println("Failed to rename: " + file.getAbsolutePath());
            }
        } else {
            System.out.println("File not found: " + file.getAbsolutePath());
        }
    }

    private static void copyMp3File(File mp3File) {
        File destDir = new File("D:\\UP");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File destFile = new File(destDir, mp3File.getName());
        try {
            Files.copy(mp3File.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied: " + mp3File.getAbsolutePath() + " to " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to copy: " + mp3File.getAbsolutePath() + " - " + e.getMessage());
        }
    }
}


