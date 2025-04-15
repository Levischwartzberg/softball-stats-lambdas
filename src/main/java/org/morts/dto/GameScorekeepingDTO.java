package org.morts.dto;

import com.google.gson.Gson;
import lombok.*;
import org.morts.domain.Season;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameScorekeepingDTO {

    Season season;
    GameInfoDTO gameInfo;
    List<InningDTO> innings;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
