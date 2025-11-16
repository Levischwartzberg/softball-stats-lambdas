package org.morts.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.morts.enumeration.LaunchAngleENUM;
import org.morts.enumeration.RegionENUM;
import org.morts.enumeration.ResultENUM;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattedBallDataDTO {

    Integer exitVelocity;
    LaunchAngleENUM launchAngle;
    RegionENUM region;
    ResultENUM result;
    Double runsAboveAverage;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
