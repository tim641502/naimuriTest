package com.example.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileToArrayReader implements ITextFileReader {

    @Override
    public String[] ReadTextFile(String fileName) {
        try (InputStream in = Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResourceAsStream(fileName)) {
            if (in == null) {
                System.out.println("Resource not found on classpath: " + fileName);
                return new String[]{};
            }
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return r.lines().toArray(String[]::new);
            }
        } catch (Exception e) {
            System.out.println("Error reading resource: " + e.getMessage());
            return new String[]{};
        }
    }
}