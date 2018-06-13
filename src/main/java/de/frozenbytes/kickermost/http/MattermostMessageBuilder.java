package de.frozenbytes.kickermost.http;

import de.frozenbytes.kickermost.dto.type.Country;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MattermostMessageBuilder {

    private static final String NEW_LINE = "\n";

    //TODO: Make configurable
    public static String createJsonMessage(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("username", "WM-Ticker");
        builder.add("channel", "town-square");
        builder.add("icon_url", "http://mediadb.kicker.de/2018/fussball/ligen/l/101_20151028668.png");
        // TODO: Build message
        builder.add("text", buildMattermostMessage());
        return builder.build().toString();
    }

    private static String buildMattermostMessage(){
        StringBuilder builder = new StringBuilder();

//        builder.append(Arrays.stream(Country.values()).map(country -> country.getName() + " " + country.getMattermostCode()).collect(Collectors.joining(" - ")));

//        builder.append("## :soccer: TOR!");
//        builder.append(NEW_LINE);
//        builder.append("### :portugal: Portugal 2 : 1 :nigeria: Nigeria");
//        builder.append(NEW_LINE);
//        builder.append("30. Nachrichtentext der Tickernachricht?");

        return builder.toString();
    }
}
