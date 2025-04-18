package org.morts.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.morts.domain.Opponent;
import org.morts.domain.Season;

import java.util.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameInfoDTO {

    Integer gameInfoId;
    Season season;
    Boolean home;
    Integer runsFor;
    Integer runsAgainst;
    Opponent opponent;
    Date date;
    String field;
    Integer temperature;
    List<String> weatherConditions;
    String gameNotes;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
