package org.morts.dto;

import lombok.*;
import org.morts.domain.Player;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Baserunners {

    private Player first;
    private Player second;
    private Player third;
}
