package org.morts.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Value;
import org.morts.domain.Result;

import java.util.List;

@Builder
@Value
public class Boxscore {

    GameInfoDTO gameInfo;
    List<PlayerStatline> playerStatlines;
    List<InningDTO> innings;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
