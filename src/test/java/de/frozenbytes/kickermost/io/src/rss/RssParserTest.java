package de.frozenbytes.kickermost.io.src.rss;

import de.frozenbytes.kickermost.dto.property.RssLink;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class RssParserTest {

    private static final String URL = "http://rss.kicker.de/live/wm";

    private RssParser parser;

    @Before
    public void before() throws IOException {
        parser = new RssParser(URL);
    }

    @Test
    public void test() throws Exception {
        final List<RssLink> linkList = parser.parse();
        assertThat(linkList).isNotNull().isNotEmpty();
        assertThat(linkList.stream().allMatch(link -> link.getValue().matches("^http://.*$"))).isTrue();
        linkList.forEach(System.out::println);
    }

}
