package ru.job4j;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.grabber.Grab;
import ru.job4j.grabber.QuartzGrabber;
import ru.job4j.parser.Parser;
import ru.job4j.parser.ParserSqlRu;
import ru.job4j.server.Server;
import ru.job4j.store.SqlRuStore;
import ru.job4j.store.Store;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class Launcher {
    private final Properties cfg = new Properties();
    private final String path;
    private final BasicDataSource pool = new BasicDataSource();
    private Store store;
    private Grab grab;
    private Parser parser;

    public Launcher(String path) {
        this.path = path;
    }

    private void cfg() {
        try (BufferedReader in = new BufferedReader(new FileReader(new File(path)))) {
            cfg.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void pool() {
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
    }

    private void startServer() {
        Server server = new Server(store, cfg);
        Thread srv = new Thread(server);
        srv.setDaemon(true);
        srv.start();
    }

    private void initStore() {
        store = new SqlRuStore(pool);
    }

    private void startGrabber() {
        grab = new QuartzGrabber(cfg);
        grab.init(parser, store);
    }

    private void initParse() {
        parser = new ParserSqlRu();
    }

    public static void main(String[] args) {
        Launcher l = new Launcher("./app.properties");
        l.cfg();
        l.pool();
        l.initStore();
        l.initParse();
        l.startServer();
        l.startGrabber();
    }
}
