package ru.job4j.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Assert;
import org.junit.Test;
import ru.job4j.model.Post;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


public class SqlRuStoreTest {
    private static final Store STORE;
    private static final BasicDataSource POOL = new BasicDataSource();

    static {
        try (InputStream in = SqlRuStore.class.getResourceAsStream("/app.properties")) {
            Properties cfg = new Properties();
            cfg.load(in);
            POOL.setDriverClassName(cfg.getProperty("jdbc.driver"));
            POOL.setUsername(cfg.getProperty("jdbc.username"));
            POOL.setPassword(cfg.getProperty("jdbc.password"));
            POOL.setUrl(cfg.getProperty("jdbc.url"));
            STORE = new SqlRuStore(POOL);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testSave() throws Exception {
        Post testOb = new Post("saveTest", "saveTest", "saveTest", "saveTest");
        STORE.save(testOb);
        Assert.assertTrue(STORE.getAll().contains(testOb));
        try (Connection cnn = POOL.getConnection();
             PreparedStatement st = cnn.prepareStatement(
                     "DELETE FROM post WHERE id = 1")) {
            st.executeUpdate();
        }
    }
    @Test
    public void getAllTest() {
        List<Post> test = List.of(
                new Post(33, "first", "first", "first", "first"),
                new Post(34, "second", "second", "second", "second"));

        List<Post> res = STORE.getAll();

        for (int i = 0; i < test.size(); i++) {
            Assert.assertEquals(test.get(i), res.get(i));
        }
    }

    @Test
    public void findByIdTest() {
        Post test = new Post(34, "second", "second", "second", "second");
        Optional<Post> p = STORE.findById("34");
        Assert.assertTrue(p.isPresent());
        Post rsl = STORE.findById("34").get();
        Assert.assertEquals(test, rsl);
    }
}
