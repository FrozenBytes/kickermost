package de.frozenbytes.kickermost.io.src.rss;

import de.frozenbytes.kickermost.PropertiesLoader;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.property.RssLink;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class RssParserTest {

    private static PropertiesHolder propertiesHolder;
    private RssParser parser;

    @BeforeClass
    public static void beforeClass() throws Exception {
        propertiesHolder = PropertiesLoader.createPropertiesHolder();
    }

    @Before
    public void before() throws IOException {
        parser = new RssParser(propertiesHolder.getPollingRssFeedUrl());
    }

    @Test
    public void test() throws Exception {
        final List<RssLink> linkList = parser.parse();
        assertThat(linkList).isNotNull().isNotEmpty();
        assertThat(linkList.stream().allMatch(link -> link.getValue().matches("^http://.*$"))).isTrue();
        linkList.forEach(System.out::println);
    }

}
