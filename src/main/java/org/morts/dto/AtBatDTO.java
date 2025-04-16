package org.morts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.morts.domain.Player;
import org.morts.enumeration.ResultENUM;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtBatDTO {

    private Integer atBatId;
    private Player player;
    private InningDTO inningDTO;
    private Integer inningIndex;
    private Baserunners baserunners;
    private ResultENUM result;
    private Integer walk;
    private String scoring;
    private Integer region;
    private Character launchAngle;
    private Integer contactQuality;
    private String ballsAndStrikes;
    private List<Player> outs;
    private List<Player> runs;
}
