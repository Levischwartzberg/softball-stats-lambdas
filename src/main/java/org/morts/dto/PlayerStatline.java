package org.morts.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Value;
import org.morts.domain.Player;

@Builder
@Value
public class PlayerStatline {

    Player player;
    Statline statline;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
