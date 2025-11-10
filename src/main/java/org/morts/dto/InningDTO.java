package org.morts.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InningDTO {

    private Integer inningId;
    private Integer inning;
    private Integer opponentRuns;
    private Integer mortsRuns;
    private Integer gameInfoId;
    List<AtBatDTO> atBats;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
