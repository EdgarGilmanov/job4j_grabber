package ru.job4j.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import ru.job4j.model.Post;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ParserSqlRuTest {

    @Test
    public void parsePageTest() throws IOException {
        Parser parser = new ParserSqlRu();
        File html = new File("src/test/resources/testSQLruPage1.html");
        Document document = Jsoup.parse(html, "UTF-8");
        List<Post> rsl = parser.parsePage(document);
        List<Post> test = List.of(
                new Post("Java-разработчик в Я.Маркет (з.п. от 100 до 300) [new]",
                        "test",
                        "https://www.sql.ru/forum/1322145/java-razrabotchik-v-ya-market-z-p-ot-100-do-300",
                        "test"),
                new Post("Главный специалист/Руководитель Отдела прикладного администрирования ИС (платформа PSI) [new]",
                        "test",
                        "https://www.sql.ru/forum/1325151/glavnyy-specialist-rukovoditel-otdela-prikladnogo-administrirovaniya-is-platforma-psi",
                        "test"));
        Assert.assertEquals(rsl, test);
    }
}