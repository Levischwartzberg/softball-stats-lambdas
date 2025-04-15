package org.morts.dto;

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
    private Integer gameInfoId;
    List<AtBatDTO> atBats;

}
