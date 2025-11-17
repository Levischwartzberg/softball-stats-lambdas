package org.morts.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RunExpectancyDTO {

    Set<SituationRunExpectancyDTO> situationRunExpectancy;
    ResultRunExpectancyDTO resultRunExpectancy;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
