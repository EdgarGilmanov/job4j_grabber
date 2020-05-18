package ru.job4j.parser;

import org.jsoup.nodes.Document;
import ru.job4j.model.Post;
import java.util.List;

/**
 * Parser gives an idea of what responsibilities anyone should have
 * implementations of this interface. Parser alone means search for information about vacancies on any sites. */
public interface Parser {
    /**
     * The method is intended to collect information from all pages starting with @param page.
     * The vacancies found are loaded into the container object {@link Post}
     * @param startPage sets the start page of the search on the site
     * @return list of collected vacancies for an object of type {@link Post}
     */
    List<Post> getAllPosts(int startPage);

    /**
     * The method is parsing one page on a site.
     * @param document - object of {@link org.jsoup.nodes.Document}, which give to us
     *                 use DOM methods to navigate a HTML page
     * @return list of collected vacancies for an object of type {@link Post}
     */
    List<Post> parsePage(Document document);

}