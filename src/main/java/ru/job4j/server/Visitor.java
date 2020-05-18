package ru.job4j.server;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * The class object searches for files related to the configuration of this App. All files found
 * It transfers to the external environment for further processing
 */
public class Visitor extends SimpleFileVisitor<Path> {
    private static final String PATTERN = ".properties";
    private final List<Path> rsl = new ArrayList<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.toString().contains(PATTERN) && !file.toString().contains("target") && !file.toString().contains("test")) {
            rsl.add(file);
        }
        return FileVisitResult.CONTINUE;
    }

    public List<Path> getFiles() {
        return rsl;
    }
}