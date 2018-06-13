package de.frozenbytes.kickermost.http;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class MattermostMessageBuilder {

    //TODO: Make configurable
    public static String createJsonMessage(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("username", "WM-Ticker");
        builder.add("channel", "town-square");
        builder.add("icon_url", "http://mediadb.kicker.de/2018/fussball/ligen/l/101_20151028668.png");
        // TODO: Build message
        builder.add("text", ":soccer: Testnachricht");
        return builder.build().toString();
    }
}
