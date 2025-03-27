package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAuthorizer implements RequestStreamHandler {

    private static final String ADMIN_USERNAME = System.getenv("ADMIN_USER");
    private static final String COGNITO_USER_POOL_ISS = System.getenv("COGNITO_USER_POOL_ISS");
    private static final String JWKS_URL = COGNITO_USER_POOL_ISS + "/.well-known/jwks.json";

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> input = mapper.readValue(inputStream, Map.class);
        Map<String, String> headers = (Map<String, String>) input.get("headers");

        String token = headers.get("authorization").replace("Bearer ", "");

        Boolean isAuthorized = false;
        try {
            PublicKey publicKey = getPublicKeyFromJwks(JWKS_URL, getKeyIdFromToken(token));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.get("cognito:username", String.class);
            String iss = claims.getIssuer();

            isAuthorized = ADMIN_USERNAME.equals(username) && COGNITO_USER_POOL_ISS.equals(iss);

        } catch (Exception e) {
            context.getLogger().log("Exception: " + e.getMessage());
            isAuthorized = false;
        }

        Map<String, Object> response = new HashMap<>();

        response.put("isAuthorized", isAuthorized);
        response.put("context", new HashMap<String, String>());

        mapper.writeValue(outputStream, response);
    }

    private PublicKey getPublicKeyFromJwks(String jwksUrl, String kid) throws Exception {
        InputStream inputStream = new URL(jwksUrl).openStream();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jwks = mapper.readValue(inputStream, Map.class);
        Map<String, String> key = ((List<Map<String, String>>) jwks.get("keys")).stream()
                .filter(k -> kid.equals(k.get("kid")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Key ID not found in JWKS"));

        byte[] n = Base64.getUrlDecoder().decode(key.get("n"));
        byte[] e = Base64.getUrlDecoder().decode(key.get("e"));

        return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(new BigInteger(1, n), new BigInteger(1, e)));
    }

    private String getKeyIdFromToken(String token) throws JsonProcessingException {
        Map<String, String> decodedTokenHeaders = decodeToken(token);

        return decodedTokenHeaders.get("kid");
    }

    private Map<String, String> decodeToken(String token) throws JsonProcessingException {

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[0]));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(payload, Map.class);
    }
}

