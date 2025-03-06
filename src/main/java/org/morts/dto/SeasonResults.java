package org.morts.dto;

import lombok.Builder;
import lombok.Value;
import org.morts.domain.Result;
import org.morts.domain.Season;

import java.util.List;

@Builder
@Value
public class SeasonResults {

    Season season;
    List<Result> results;
}
