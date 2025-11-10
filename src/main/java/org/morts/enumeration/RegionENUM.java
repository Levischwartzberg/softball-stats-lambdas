package org.morts.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RegionENUM {

    @JsonProperty("Foul Left")
    FOUL_LEFT,
    @JsonProperty("Left")
    LEFT,
    @JsonProperty("Left Center")
    LEFT_CENTER,
    @JsonProperty("Center")
    CENTER,
    @JsonProperty("Right Center")
    RIGHT_CENTER,
    @JsonProperty("Right")
    RIGHT,
    @JsonProperty("Foul Right")
    FOUL_RIGHT;

    public static RegionENUM fromString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return valueOf(str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
