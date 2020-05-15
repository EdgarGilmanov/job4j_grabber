package ru.job4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class Launcher {
    private final Properties cfg = new Properties();
    private final String path;

    public Launcher(String path) {
        this.path = path;
    }

    public void cfg() {
        try (BufferedReader in = new BufferedReader(new FileReader(new File(path)))) {
            cfg.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        Launcher l = new Launcher("./app.properties");
        l.cfg();
    }
}
