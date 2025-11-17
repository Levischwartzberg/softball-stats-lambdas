package org.morts.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.morts.enumeration.ResultENUM;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultRunExpectancyDTO {

    Map<ResultENUM, Double> runExpectancyByResult;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
