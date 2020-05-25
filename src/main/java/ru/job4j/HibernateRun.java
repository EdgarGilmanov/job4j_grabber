package ru.job4j;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.model.Post;

import java.util.List;

public class HibernateRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Post post = create(new Post("Learn Hibernate", "Learn Hibernate", "Learn Hibernate", "Learn Hibernate"), sf);
            System.out.println(post);
            post.setName("Learn Hibernate 5.");
            update(post, sf);
            System.out.println(post);
            Post rsl = findById(post.getId(), sf);
            System.out.println(rsl);
            delete(rsl.getId(), sf);
            List<Post> list = findAll(sf);
            for (Post it : list) {
                System.out.println(it);
            }
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static Post create(Post post, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(post);
        session.getTransaction().commit();
        session.close();
        return post;
    }

    public static void update(Post post, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.update(post);
        session.getTransaction().commit();
        session.close();
    }

    public static void delete(Integer id, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        Post item = new Post();
        item.setId(id);
        session.delete(item);
        session.getTransaction().commit();
        session.close();
    }

    public static List<Post> findAll(SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        List result = session.createQuery("from ru.job4j.model.Post").list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    public static Post findById(Integer id, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        Post result = session.get(Post.class, id);
        session.getTransaction().commit();
        session.close();
        return result;
    }
}