package org.morts.dto;

import lombok.*;
import org.morts.domain.Season;

@Builder
@Value
public class SeasonStatline {

    Season season;
    Statline statline;
}
