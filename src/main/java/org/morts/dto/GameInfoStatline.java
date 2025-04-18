package org.morts.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class GameInfoStatline {

    GameInfoDTO gameInfo;
    Statline statline;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
