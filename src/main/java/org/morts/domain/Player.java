package org.morts.domain;

import com.google.gson.Gson;
import lombok.*;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private String firstName;
    private String lastName;
    private Date birthdate;
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
