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
        File rootDir = new File("D:\\UP");
        processDirectory(rootDir);
    }

    private static void processDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        processDirectory(file);
                    } else if (file.getName().equalsIgnoreCase("entry.json")) {
                        processJsonFile(dir);
                    }
                }
            }
        }
    }

    private static void processJsonFile(File jsonDir) {
        File jsonFile = new File(jsonDir, "entry.json");
        if (jsonFile.exists()) {
            try (FileReader reader = new FileReader(jsonFile);
                 Scanner scanner = new Scanner(reader)) {
                String jsonString = scanner.useDelimiter("\\A").next();
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                String title = jsonObject.get("title").getAsString();
                String ownerName = jsonObject.get("owner_name").getAsString();
                String newBaseName = title + " - " + ownerName;
                System.out.println("New base name: " + newBaseName);

                // 重命名文件
                searchAndRenameFiles(jsonDir, newBaseName);

                // 复制MP3文件
                copyMp3Files(jsonDir);

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

    private static void copyMp3Files(File startDir) {
        File[] files = startDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    copyMp3Files(file);
                } else if (file.getName().toLowerCase().endsWith(".mp3")) {
                    copyMp3File(file);
                }
            }
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

