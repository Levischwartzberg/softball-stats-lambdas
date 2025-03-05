package org.morts.dto;

import lombok.Builder;
import lombok.Value;
import org.morts.domain.Player;

@Builder
@Value
public class PlayerStatline {

    Player player;
    Statline statline;
}
