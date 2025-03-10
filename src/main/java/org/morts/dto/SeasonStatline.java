package org.morts.dto;

import com.google.gson.Gson;
import lombok.*;
import org.morts.domain.Season;

@Builder
@Value
public class SeasonStatline {

    Season season;
    Statline statline;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
