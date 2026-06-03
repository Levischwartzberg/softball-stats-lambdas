package org.morts.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Value;
import org.morts.domain.Opponent;

import java.util.List;

@Builder
@Value
public class OpponentGames {

    Opponent opponent;
    List<GameInfoDTO> games;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
