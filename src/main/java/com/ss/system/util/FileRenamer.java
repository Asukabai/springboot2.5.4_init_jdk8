package com.ss.system.util;

import java.io.File;

public class FileRenamer {

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
                    } else if (file.getName().equalsIgnoreCase("video.m4s")) {
                        File newFile = new File(file.getParent(), "video.mp4");
                        boolean success = file.renameTo(newFile);
                        if (success) {
                            System.out.println("Renamed to: " + newFile.getAbsolutePath());
                        } else {
                            System.out.println("Failed to rename: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }
}