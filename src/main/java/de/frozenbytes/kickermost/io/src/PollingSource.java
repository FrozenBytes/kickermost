package de.frozenbytes.kickermost.io.src;

import de.frozenbytes.kickermost.dto.Story;
import de.frozenbytes.kickermost.dto.property.TeamName;
import de.frozenbytes.kickermost.dto.property.TeamScore;
import de.frozenbytes.kickermost.exception.MatchNotStartedException;
import de.frozenbytes.kickermost.exception.ReloadPollingSourceException;
import de.frozenbytes.kickermost.exception.TickerNotInSourceException;

public interface PollingSource {

    TeamName getTeamAName();
    TeamName getTeamBName();

    TeamScore getTeamAScore();
    TeamScore getTeamBScore();

    Story getStory();

    void reload() throws ReloadPollingSourceException, TickerNotInSourceException, MatchNotStartedException;

}
