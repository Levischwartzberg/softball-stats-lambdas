package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AdminAuthorizer implements RequestStreamHandler {

    private static final String ADMIN_USERNAME = System.getenv("ADMIN_USER");
    private static final String COGNITO_USER_POOL_ISS = System.getenv("COGNITO_USER_POOL_ISS");

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> input = mapper.readValue(inputStream, Map.class);
        Map<String, String> headers = (Map<String, String>) input.get("headers");

        String token = headers.get("authorization").replace("Bearer ", "");

        Boolean isAuthorized = false;
        try {
            Map<String, String> decodedJwtMap = decodeToken(token);
            String username = decodedJwtMap.get("cognito:username");
            String iss = decodedJwtMap.get("iss");

            isAuthorized = ADMIN_USERNAME.equals(username) && COGNITO_USER_POOL_ISS.equals(iss);

        } catch (Exception e) {
            mapper.writeValue(outputStream, false);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("isAuthorized", isAuthorized);
        response.put("context", new HashMap<String, String>());

        mapper.writeValue(outputStream, response);
    }

    public Map<String, String> decodeToken(String token) throws JsonProcessingException {

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(payload, Map.class);
    }
}

