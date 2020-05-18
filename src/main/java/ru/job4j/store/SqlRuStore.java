package ru.job4j.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import ru.job4j.model.Post;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The class is a tool for working with the PostgreSQL database;
 */
public class SqlRuStore implements Store {
    private final Logger log = Logger.getLogger(SqlRuStore.class);
    private final BasicDataSource pool;


    public SqlRuStore(BasicDataSource pool) {
        this.pool = pool;
    }

    @Override
    public Post save(Post vacancy) {
        try (Connection cnn = pool.getConnection()) {
             PreparedStatement ps = cnn.prepareStatement(
                     "INSERT INTO post " +
                     "(name, description, link, created) " +
                     "VALUES (?, ?, ?, ?)");
            ps.setString(1, vacancy.getName().trim());
            ps.setString(2, vacancy.getDesc());
            ps.setString(3, vacancy.getLink());
            ps.setString(4, vacancy.getCreated());
            ps.executeUpdate();
            log.info(String.format("The %s was saved", vacancy));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return vacancy;
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (Connection cnn = pool.getConnection()) {
            PreparedStatement ps = cnn.prepareStatement("SELECT * FROM post");
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                String name = set.getString("name");
                String desc = set.getString("description");
                String link = set.getString("link");
                String created = set.getString("created");
                rsl.add(new Post(name, desc, link, created));
            }
        } catch (SQLException e) {
            log.error(e);
        }
        log.info(String.format("All posts were collected. Their size amounted to %d posts", rsl.size()));
        return rsl;
    }

    @Override
    public Optional<Post> findById(String id) {
        Optional<Post> opt = Optional.empty();
        try (Connection cnn = pool.getConnection()) {
            PreparedStatement ps = cnn.prepareStatement("SELECT * FROM post WHERE id = ?");
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet set = ps.executeQuery()) {
                if (set.next()) {
                    log.info(String.format("The post by id = %s was found", id));
                    String name = set.getString("name");
                    String desc = set.getString("description");
                    String link = set.getString("link");
                    String created = set.getString("created");
                    opt = Optional.of(new Post(name, desc, link, created));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return opt;
    }
}