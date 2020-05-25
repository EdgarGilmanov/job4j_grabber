package ru.job4j.model;


import javax.persistence.*;
import java.util.Objects;

/**
 * This class is a container container for storing information about a vacancy unit.
 */
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name; //name of job vacancy
    private String description; //description of job vacancy
    private String link; //link to site where this job vacancy was announced
    private String created; //date when this job vacancy was announced

    public Post() {

    }

    public Post(String name, String description, String link, String created) {
        this.name = name;
        this.description = description;
        this.link = link;
        this.created = created;
    }

    public Post(int id, String name, String description, String link, String created) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getCreated() {
        return created;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(name, post.name) &&
                Objects.equals(link, post.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link, created);
    }

    @Override
    public String toString() {
        return "Post{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}