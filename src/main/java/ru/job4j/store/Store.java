package ru.job4j.store;

import ru.job4j.model.Post;
import java.util.List;
import java.util.Optional;


/**
 * The interface assumes a contract for database tools.
 */
public interface Store {
    /**
     * A method that searches for vacancies in the database via String id.
     * * @param id - parameter characterizing the vacancy ID in the database itself
     * @return returns an object of class {@link java.util.Optional}, which is either empty
     * either has an object of class {@link Post}
     */
    Optional<Post> findById(String id);

    /**
     * The method saves the transferred vacancy to the database
     * @param vacancy - object of class {@link Post}
     */
    Post save(Post vacancy);

    /**
     * Gets and returns a list of all the vacancies that are currently in the current database.
     */
    List<Post> getAll();
}