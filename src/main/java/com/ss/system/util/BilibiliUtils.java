package com.ss.system.util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class BilibiliUtils {

    public static void main(String[] args) {
        File rootDir = new File("D:\\UP");
        renameFiles(rootDir);
    }

    private static void renameFiles(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        renameFiles(file); // Recursively search within subdirectories
                    } else {
                        if (file.getName().equalsIgnoreCase("entry.json")) {
                            processJsonFile(file.getParentFile());
                        }
                    }
                }
            }
        }
    }

    private static void processJsonFile(File parentDir) {
        File jsonFile = new File(parentDir, "entry.json");
        if (jsonFile.exists()) {
            try (FileReader reader = new FileReader(jsonFile);
                 Scanner scanner = new Scanner(reader)) {
                String jsonString = scanner.useDelimiter("\\A").next();
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

                String title = jsonObject.get("title").getAsString();
                String ownerName = jsonObject.get("owner_name").getAsString();
                String newBaseName = title + " - " + ownerName;

                System.out.println("New base name: " + newBaseName);

                File videoFile = new File(parentDir, "video.m4s");
                File audioFile = new File(parentDir, "audio.m4s");

                renameFileIfNeeded(videoFile, newBaseName + ".mp4");
                renameFileIfNeeded(audioFile, newBaseName + ".mp3");

            } catch (IOException e) {
                System.out.println("Failed to read or process JSON file: " + e.getMessage());
            }
        } else {
            System.out.println("entry.json not found in directory: " + parentDir.getAbsolutePath());
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
}


