package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileReaderUtil {
    public static String readTextFile(String fileName) throws IOException {
        // Get the class loader
        ClassLoader classLoader = FileReaderUtil.class.getClassLoader();

        // Load the resource file from the resources folder
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + fileName);
            }

            // Read the content of the file
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }
}
