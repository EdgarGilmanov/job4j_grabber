package ru.job4j.server;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ru.job4j.model.Post;
import ru.job4j.store.Store;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ServerTest {
    private static Store store;
    private static Server server;
    private static Properties cfg;

    public static void initStore() {
        store = new Store() {
            final List<Post> store = new ArrayList<>();

            @Override
            public Optional<Post> findById(String id) {
                for (Post post : store) {
                    if (post.getId() == Integer.parseInt(id)) {
                        return Optional.of(post);
                    }
                }
                return Optional.empty();
            }

            @Override
            public Post save(Post vacancy) {
                store.add(vacancy);
                return vacancy;
            }

            @Override
            public List<Post> getAll() {
                return store;
            }
        };
    }

    public static void startTestServer() throws IOException {
        cfg = new Properties();
        try (InputStream in = new FileInputStream("src/test/resources/app.properties")) {
            cfg.load(in);
        }
        server = new Server(store, cfg);
        Thread srv = new Thread(server);
        srv.setDaemon(true);
        srv.start();
    }

    @Ignore
    public void testPostRequest() throws IOException {
        initStore();
        startTestServer();

        store.save(new Post("test", "test", "test", "test"));

        String testSt = "1) test test";

        Socket cnn = new Socket("localhost", Integer.parseInt(cfg.getProperty("server.port")));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(cnn.getOutputStream()));

        writer.write("GET /?msg=posts HTTP/1.1");
        BufferedReader reader = new BufferedReader(new InputStreamReader(cnn.getInputStream()));

        String res = reader.readLine();

        System.out.println(res);

        Assert.assertEquals(testSt, res);

        server.shutdown();
    }

    @Ignore
    public void testInfoRequest() throws IOException {
        initStore();
        startTestServer();
        String testSt = "app.properties:\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "jdbc.url=jdbc:postgresql://127.0.0.1:5432/grabber\n" +
                "jdbc.username=postgres\n" +
                "jdbc.password=1234\n" +
                "jdbc.driver=org.postgresql.Driver\n" +
                "rabbit.interval=10\n" +
                "server.port=9090\n" +
                "fork.pool.size=4\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "log4j.properties:\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "log4j.rootLogger=DEBUG, console\n" +
                "log4j.appender.console=org.apache.log4j.ConsoleAppender\n" +
                "log4j.appender.console.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.console.layout.ConversionPattern=%d{ISO8601} %5p %c:%M:%L - %m%n\n" +
                "----------------------------------------------------------------------------------------------------";

        Socket cnn = new Socket("localhost", Integer.parseInt(cfg.getProperty("server.port")));
        cnn.getOutputStream().write("GET /?msg=posts HTTP/1.1".getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(cnn.getInputStream()));

        cnn.getOutputStream().write("GET /?msg=info HTTP/1.1".getBytes());
        String res = reader.readLine();

        System.out.println(res);

        Assert.assertEquals(testSt, res);
    }
}