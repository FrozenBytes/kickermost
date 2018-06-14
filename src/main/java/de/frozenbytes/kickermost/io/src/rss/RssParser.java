package de.frozenbytes.kickermost.io.src.rss;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.dto.property.RssLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class RssParser {

    private static final String CSS_LINKS = "channel item link";

    private final Document document;


    public RssParser(String url) throws IOException {
        Preconditions.checkNotNull(url, "url should not be null!");
        Preconditions.checkArgument(!url.trim().isEmpty(), "url should not be empty!");
        this.document = Jsoup.connect(url).get();
    }

    public List<RssLink> parse(){
        return document.select(CSS_LINKS).stream()
                .map(Element::html)
                .map(RssLink::create)
                .collect(Collectors.toList());
    }

}
