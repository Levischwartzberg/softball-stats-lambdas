package org.morts.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class Scoresheet {

    List<InningDTO> innings;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
