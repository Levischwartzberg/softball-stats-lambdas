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
    ERROR
}
