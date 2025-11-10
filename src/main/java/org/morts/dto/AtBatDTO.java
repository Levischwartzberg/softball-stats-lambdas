package org.morts.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.morts.domain.Player;
import org.morts.enumeration.LaunchAngleENUM;
import org.morts.enumeration.RegionENUM;
import org.morts.enumeration.ResultENUM;

import java.util.List;
import java.util.Set;

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
    private RegionENUM region;
    private LaunchAngleENUM launchAngle;
    private Integer exitVelocity;
    private String ballsAndStrikes;
    private Set<Player> outs;
    private Set<Player> runs;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
