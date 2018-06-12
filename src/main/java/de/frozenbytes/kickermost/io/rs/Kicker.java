package de.frozenbytes.kickermost.io.rs;

import com.google.common.base.Preconditions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Kicker implements PollingTarget {

    private static final String url = "http://www.kicker.de/news/fussball/nationalelf/startseite/fussball-nationalteams-freundschaftsspiele/2018/4/4204625/livematch_deutschland_saudi-arabien.html";

    private static final String CSS_ROOT = "#tabSpielereignisse";
    private static final String CSS_MATCH = CSS_ROOT + " tr.trTickerBegegnung";
    private static final String CSS_SCOREBOARD = CSS_MATCH + " td.begErg > div.scoreboard";
    private static final String CSS_TEAM_A_SCORE = CSS_SCOREBOARD + " div.boardH";
    private static final String CSS_TEAM_B_SCORE = CSS_SCOREBOARD + " div.boardA";

    private final Document document;

    public Kicker(final String url) throws IOException {
        super();
        Preconditions.checkNotNull(url, "url should not be null!");
        Preconditions.checkArgument(!url.trim().isEmpty(), "url should not be empty!");
        this.document = Jsoup.connect(url).get();
    }

    public String getTeamAScore(){
        return getFirstText(document, CSS_TEAM_A_SCORE);
    }

    public String getTeamBScore(){
        return getFirstText(document, CSS_TEAM_B_SCORE);
    }

    private String getFirstText(final Document document, final String cssSelector){
        return document.select(cssSelector).first().text();
    }

}
