package org.morts.domain;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Season {

    private Integer id;
    private Integer year;
    private String session;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
