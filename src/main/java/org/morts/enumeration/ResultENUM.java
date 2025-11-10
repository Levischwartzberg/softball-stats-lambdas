package org.morts.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResultENUM {

    @JsonProperty("Single")
    SINGLE,
    @JsonProperty("Double")
    DOUBLE,
    @JsonProperty("Triple")
    TRIPLE,
    @JsonProperty("Homerun")
    HOMERUN,
    @JsonProperty("Walk")
    WALK,
    @JsonProperty("Out(s)")
    OUT,
    @JsonProperty("Error")
    ERROR,
    @JsonProperty("Skip")
    SKIP;

    public static ResultENUM fromString(String str) {
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
