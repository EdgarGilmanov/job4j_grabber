package ru.job4j.grabber;

import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import ru.job4j.model.Post;
import ru.job4j.parser.Parser;
import ru.job4j.store.Store;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class QuartzGrabberTest {
    private static final Store STORE;
    private static final Parser PARSE;

    /**
     * Here we initialize objects {@link ru.job4j.stores.Store} and {@link ru.job4j.parsers.Parser}
     * The peculiarity of the implementation is that in the test {@link ru.job4j.parsers.Parser}
     * there will not be DOM parsing HTML. */
    static {
        STORE = new Store() {
            List<Post> store = new ArrayList<>();

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

        PARSE = new Parser() {
            @Override
            public List<Post> getAllPosts(int startPage) {
                return List.of(
                        new Post("Test", "Test", "Test", "Test"));
            }

            @Override
            public List<Post> parsePage(Document document) {
                return null;
            }
        };
    }

    @Test
    public void test() throws IOException {
        try (InputStream in = new FileInputStream("src/test/resources/app.properties")) {
            Properties cfg = new Properties();
            cfg.load(in);
            QuartzGrabber grabber = new QuartzGrabber(cfg);
            grabber.init(PARSE, STORE);
            List<Post> rsl = STORE.getAll();
            Post test = new Post("Test", "Test", "Test", "Test");
            for (Post p : rsl) {
                Assert.assertEquals(test, p);
            }
        }
    }
}