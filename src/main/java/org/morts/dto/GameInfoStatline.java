package org.morts.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class GameInfoStatline {

    GameInfoDTO gameInfo;
    Statline statline;
}
