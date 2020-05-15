package ru.job4j.grabber;

import ru.job4j.store.Store;
import ru.job4j.parsers.Parser;

/**
 * Grab implements a scheduled repetition mechanism. In our case, we will be at regular intervals
 * update the database {@link ru.job4j.store.Store} with new search results by {@link Parser}
 */
public interface Grab {
    /**
     * @param parse instance of the implementation {@link Parser}. Its task is to find new external data
     *               and transfer it to the @param store.
     * @param store instance of the implementation {@link ru.job4j.store.Store}. Its task is to save new data to the database
     *              found by {@link Parser}
     */
    void init(Parser parse, Store store);
}