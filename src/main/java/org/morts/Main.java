package org.morts;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.morts.dto.Boxscore;
import org.morts.dto.GameInfoDTO;
import org.morts.dto.GameScorekeepingDTO;
import org.morts.dto.Scoresheet;
import org.morts.lambdas.BoxscoreLambda;
import org.morts.lambdas.CreateScorekeepingGameLambda;
import org.morts.lambdas.ScoresheetLambda;
import org.morts.lambdas.SeasonGamesLambda;

import java.sql.SQLException;
import java.util.Map;

public class Main {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws SQLException, ClassNotFoundException, JsonProcessingException {

        String dto = "";

        GameScorekeepingDTO gameScorekeepingDTO = objectMapper.readValue(dto, GameScorekeepingDTO.class);

        System.out.println(gameScorekeepingDTO.toString());

        CreateScorekeepingGameLambda createScorekeepingGameLambda = new CreateScorekeepingGameLambda();

//        try {
//            GameInfoDTO createdGameInfoDTO = createScorekeepingGameLambda.createGameInfo(gameScorekeepingDTO.getGameInfo(), gameScorekeepingDTO.getSeason().getId());
//            createScorekeepingGameLambda.createInnings(gameScorekeepingDTO.getInnings(), createdGameInfoDTO.getGameInfoId());
//
//        } catch (Exception e) {
//
//            System.out.println(e.getMessage());
//        }
    }
}
