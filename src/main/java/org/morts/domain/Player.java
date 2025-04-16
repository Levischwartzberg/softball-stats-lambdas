package org.morts.domain;

import com.google.gson.Gson;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private String firstName;
    private String lastName;
    private String birthdate;
    private String height;
    private Integer weight;
    private String batHand;
    private String throwHand;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
