package ru.job4j.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.model.Post;

import java.util.List;
import java.util.Optional;

public class HbmStore implements Store {
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    @Override
    public Optional<Post> findById(String id) {
        Session session = sf.openSession();
        session.beginTransaction();
        Post result = session.get(Post.class, id);
        session.getTransaction().commit();
        session.close();
        return Optional.of(result);
    }

    @Override
    public Post save(Post vacancy) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(vacancy);
        session.getTransaction().commit();
        session.close();
        return vacancy;
    }

    @Override
    public List<Post> getAll() {
        Session session = sf.openSession();
        session.beginTransaction();
        List result = session.createQuery("from ru.job4j.model.Post").list();
        session.getTransaction().commit();
        session.close();
        return result;
    }
}
