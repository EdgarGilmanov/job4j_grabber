package ru.job4j.server;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class VisitorTest {

    @Test
    public void testVisit() throws IOException {
        List<String> test = List.of("app.properties", "log4j.properties");
        Visitor visitor = new Visitor();
        Files.walkFileTree(Paths.get("./"), visitor);
        List<Path> r = visitor.getFiles();
        List<String> res = new ArrayList<>();

        for (Path p : r) {
            res.add(p.getFileName().toString());
        }

        for (String s : test) {
            assertTrue(res.contains(s));
        }
    }
}