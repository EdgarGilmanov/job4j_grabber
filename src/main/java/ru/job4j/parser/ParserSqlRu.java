package ru.job4j.parser;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.model.Post;
import java.io.IOException;
import java.util.*;
import java.util.Date;

/**
 * This class is an implementation of {@link Parser}
 * He finds vacancies on the site "www.sql.ru".
 * Internal {@code private} methods solve the problem of a site’s features for storing job information.
 * So, for example, the method {@code String parseDate (String date)} solves the problem of converting
 * the date to the desired us format for storage in the database.
 */
public class ParserSqlRu implements Parser {
    private static final Logger LOGGER = Logger.getLogger(ParserSqlRu.class);

    /**
     * {@code URL_FORMAT} is the URL to the search site. The last character {@code %d} is used
     * as a template for substituting the value of the search page number. */
    private final static String URL_FORMAT = "https://www.sql.ru/forum/job-offers/%d";

    /**
     * The date formatting pattern is similar to the date formatting on the site itself.
     * It finds its use in changing the date in the {@code String parseDate (String date)} method */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("dd MMM yy");

    /**
     * This is a list of headings that are exceptions.
     * The structure of the HTML code of the blocks listed below is similar to the structure of vacancies on the site.
     * In order to prevent these headers from appearing in the result, in the {@code boolean isTrueHead (String head)} method
     * a header is rejected using this list. */
    private static final List<String> HEAD_RESTRICTIONS = Arrays.asList(
            "Правила форума",
            "Сообщения от модераторов",
            "Шпаргалки");

    @Override
    public List<Post> getAllPosts(int page) {
        List<Post> rsl = new ArrayList<>();
        try {
            for (int i = page; i < page + 3; i++) {
                Document document = Jsoup
                        .connect(String.format(URL_FORMAT, i))
                        .userAgent("Chrome")
                        .referrer("referrer")
                        .get();
                List<Post> rslOnPage = parsePage(document);
                rsl.addAll(rslOnPage);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<Post> parsePage(Document document) {
        List<Post> rsl = new ArrayList<>();
        Elements elements = document.getElementsByClass("postslisttopic");
        if (elements.size() == 0) {
            return List.of();
        }
        for (Element head : elements) {
            if (isTrueHead(head.text().trim())) {
                String link = head.getElementsByTag("a").attr("href");
                Optional<Post> post = detail(link);
                if (post.isPresent()) {
                    Post newPost = post.get();
                    rsl.add(newPost);
                }
            }
        }
        LOGGER.info(String.format("SqlRuParser found %s posts", rsl.size()));
        return rsl;
    }

    private Optional<Post> detail(String link) {
        Optional<Post> rsl = Optional.empty();
        try {
            Document document = Jsoup.connect(link)
                    .userAgent("Chrome")
                    .referrer("referrer")
                    .get();
            Element elm = document.getElementsByClass("messageHeader").first();
            String name = elm.text().trim();
            Elements elements = document.getElementsByClass("msgBody");
            if (elements.size() == 0) {
                throw new IllegalArgumentException();
            }
            String desc = elements.get(1).text().trim();
            Element element = document.getElementsByClass("msgFooter").first();
            String date  = parseDate(element.text());
            rsl = Optional.of(new Post(name, desc, link, date));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    private String parseDate(String date) {
        String correctDate = date.split("\\[")[0];
        if (correctDate.contains("сегодня")) {
            String trueDate = DATE_FORMAT.print(new Date().getTime());
            return correctDate.replaceAll("сегодня", trueDate);
        }
        if (correctDate.contains("вчера")) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            String trueDate = DATE_FORMAT.print(calendar.getTime().getTime());
            return correctDate.replaceAll("вчера", trueDate);
        }
        return correctDate.trim();
    }

    private boolean isTrueHead(String head) {
        for (String st : HEAD_RESTRICTIONS) {
            if (head.contains(st)) {
                return false;
            }
        }
        return true;
    }
}