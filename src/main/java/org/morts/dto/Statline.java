package org.morts.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Statline {
    private Integer games;
    private Integer lineupSpot;
    private Integer atBats;
    private Integer hits;
    private Integer singles;
    private Integer doubles;
    private Integer triples;
    private Integer homeruns;
    private Integer walks;
    private Integer runs;
    private Integer rbi;
    private double avg;
    private double obp;
    private double slg;
    private double ops;
    private Integer wrcPlus;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

