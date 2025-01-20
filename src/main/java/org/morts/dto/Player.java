package org.morts.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Builder
@Value
public class Player {
    private Integer id;
    private String firstName;
    private String lastName;
    private String birthdate;
    private String imageUrl;
    private String height;
    private Integer weight;
    private String batHand;
    private String throwHand;

}
