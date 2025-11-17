package org.morts.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SituationRunExpectancyDTO {

    Boolean firstBaseOccupied;
    Boolean secondBaseOccupied;
    Boolean thirdBaseOccupied;
    Integer outs;
    Double runExpectancy;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
