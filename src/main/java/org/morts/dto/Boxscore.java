package org.morts.dto;

import lombok.Builder;
import lombok.Value;
import org.morts.domain.Result;

import java.util.List;

@Builder
@Value
public class Boxscore {

    Result result;
    List<PlayerStatline> playerStatlines;
}
