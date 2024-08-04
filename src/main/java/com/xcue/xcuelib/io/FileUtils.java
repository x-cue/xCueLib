package com.xcue.xcuelib.io;

import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class FileUtils {

    public static void copy(InputStream paramInputStream, File paramFile) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
            byte[] arrayOfByte = new byte[1024];
            int i;
            while ((i = paramInputStream.read(arrayOfByte)) > 0)
                fileOutputStream.write(arrayOfByte, 0, i);
            fileOutputStream.close();
            paramInputStream.close();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public static void mkdir(File file) {
        try {
            file.mkdir();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Nullable
    public static File load(String fileName, Plugin plugin) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                plugin.saveResource(fileName, false);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error creating file" + fileName, e);
                return null;
            }
        }

        return file;
    }
}
