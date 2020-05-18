package ru.job4j.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.store.Store;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;

/**
 * The class object is used to create a multi-threaded server that can work with many clients.
 * It is his responsibility to work out client connections to the server and redirect client services to
 * an object of class {@link ru.job4j.server.ServerHandler}.
 */
public class Server implements Runnable {
    private final Logger log = LoggerFactory.getLogger(Server.class);
    private final Properties cfg;
    private final Store store;

    public Server(Store store, Properties cfg) {
        this.store = store;
        this.cfg = cfg;
    }

    @Override
    public void run() {
        int size = Integer.parseInt(cfg.getProperty("fork.pool.size"));
        int port = Integer.parseInt(cfg.getProperty("server.port"));
        log.info("Server start : pool - {}, port - {}.", size, port);
        ForkJoinPool fork = new ForkJoinPool(size);
        try (ServerSocket svr = new ServerSocket(port)) {
            while (!svr.isClosed()) {
                Socket connect = svr.accept();
                fork.execute(new ServerHandler(store, connect));
            }
            log.warn("Server was shutdown");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}