package org.morts.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Value;
import org.morts.domain.Season;

import java.util.List;

@Builder
@Value
public class SeasonGames {

    Season season;
    List<GameInfoDTO> games;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
