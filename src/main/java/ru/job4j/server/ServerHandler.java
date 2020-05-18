package ru.job4j.server;

import org.apache.log4j.Logger;
import ru.job4j.model.Post;
import ru.job4j.store.Store;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * An object of this class is the unit for processing a client request.
 * One object of the ServerHandler class serves one client. */
public class ServerHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class);
    private final Store store;
    private final Socket connect;

    public ServerHandler(Store store, Socket connect) {
        this.store = store;
        this.connect = connect;
    }

    /**
     * Here is the processing of client requests, as well as the response to these requests. */
    @Override
    public void run() {
        try (OutputStream out = connect.getOutputStream();
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(connect.getInputStream()))) {
            String response = in.readLine();
            out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            try (BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_16))) {
                switch (response) {
                    case "GET /?msg=posts HTTP/1.1":
                        sendPosts(wr);
                        LOGGER.info("Server showed all posts");
                        break;
                    case "GET /?msg=info HTTP/1.1":
                        sendInfo(wr);
                        LOGGER.info("Server showed info about app properties");
                        break;
                    default:
                        out.write("this command is not supported".getBytes());
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    /**
     * A method for displaying formatted job information stored in a database.
     * @param wr is the implementation thread {@link java.io.Writer} and means
     *           It is a stream to output information to the connection of the object {@link java.net.Socket}
     */
    private void sendPosts(Writer wr) throws IOException {
        List<Post> rsl = store.getAll();
        int count = 0;
        for (Post p : rsl) {
            wr.write(String.format("%d) %s %s", ++count, p.getName(), p.getLink()));
            wr.write(System.lineSeparator());
        }
    }


    /**
     * Method for formatted output of information about program operation.
     * @param wr is the implementation thread {@link java.io.Writer} and means
     *          It is a stream to output information to the connection of the object {@link java.net.Socket}
     */
    private void sendInfo(Writer wr) {
        try {
            Visitor v = new Visitor();
            Files.walkFileTree(Path.of("./"), v);
            for (Path p : v.getFiles()) {
                try (BufferedReader r = new BufferedReader(new FileReader(p.toFile()))) {
                    wr.write(String.format("%s:%s", p.getFileName(), System.lineSeparator()));
                    for (int i = 0; i < 100; i++) {
                        wr.write("-");
                    }
                    wr.write(System.lineSeparator());
                    r.lines().forEach(l -> {
                        try {
                            wr.write(l);
                            wr.write(System.lineSeparator());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    for (int i = 0; i < 100; i++) {
                        wr.write("-");
                    }
                    wr.write(System.lineSeparator());
                }
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}