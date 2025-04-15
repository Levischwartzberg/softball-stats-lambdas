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

    private Integer gameInfoId;
    private Season season;
    private Boolean home;
    private Integer runsFor;
    private Integer runsAgainst;
    private Opponent opponent;
    private Date date;
    private String field;
    private Integer temperature;
    private List<String> weatherConditions;
    private String gameNotes;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
