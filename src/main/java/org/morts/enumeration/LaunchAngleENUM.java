package org.morts.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LaunchAngleENUM {

    @JsonProperty("Groundball")
    GROUNDBALL,
    @JsonProperty("Liner")
    LINER,
    @JsonProperty("Flyball")
    FLYBALL,
    @JsonProperty("Popup")
    POPUP;

    public static LaunchAngleENUM fromString(String str) {
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
