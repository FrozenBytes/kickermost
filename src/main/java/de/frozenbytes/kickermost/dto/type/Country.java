package de.frozenbytes.kickermost.dto.type;

import java.util.Arrays;

public enum Country {
    ARGENTINA("Argentinien", ":argentina:"),
    AUSTRALIA("Australien", ":australia:"),
    BELGIUM("Belgien", ":belgium:"),
    BRAZIL("Brasilien", ":brazil:"),
    COLOMBIA("Kolumbien", ":colombia:"),
    COSTA_RICA("Costa Rica", ":costa_rica:"),
    CROATIA("Kroatien", ":croatia:"),
    DENMARK("Dänemark", ":denmark:"),
    EGYPT("Ägypten", ":egypt:"),
    ENGLAND("England", ":uk:"),
    FRANCE("Frankreich", ":fr:"),
    GERMANY("Deutschland", ":de:"),
    ICELAND("Island", ":iceland:"),
    IR_IRAN("Iran", ":iran:"),
    JAPAN("Japan", ":jp:"),
    KOREA_REPUBLIC("Südkorea", ":kr:"),
    MEXICO("Mexiko", ":mexico:"),
    MOROCCO("Marokko", ":morocco:"),
    NIGERIA("Nigeria", ":nigeria:"),
    PANAMA("Panama", ":panama:"),
    PERU("Peru", ":peru:"),
    POLAND("Polen", ":poland:"),
    PORTUGAL("Portugal", ":portugal:"),
    RUSSIA("Russland", ":ru:"),
    SAUDI_ARABIA("Saudi-Arabien", ":saudi_arabia:"),
    SENEGAL("Senegal", ":senegal:"),
    SERBIA("Serbien", ":serbia:"),
    SPAIN("Spanien", ":es:"),
    SWEDEN("Schweden", ":sweden:"),
    SWITZERLAND("Schweiz", ":switzerland:"),
    TUNISIA("Tunesien", ":tunisia:"),
    URUGUAY("Uruguay", ":uruguay:")
    ;
    final String name;
    final String mattermostCode;

    Country(String name, String mattermostCode) {
        this.name = name;
        this.mattermostCode = mattermostCode;
    }

    public String getName() {
        return name;
    }

    public String getMattermostCode() {
        return mattermostCode;
    }

    public static Country getCountryByName(final String name){
        return Arrays.stream(values()).filter(country -> country.getName().equals(name)).findFirst().orElse(null);
    }
}
