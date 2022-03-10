package com.tiny.url.lambdas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.tiny.url.dynamoDB.TinyURLDAO;
import com.tiny.url.dynamoDB.TinyURLDAOIMPL;
import com.tiny.url.dynamoDB.table.mapping.TinyURL;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Handler for requests to Lambda function.
 */
public class TinyURLLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private TinyURLDAO tinyURLDAO;
    private static final String TINYURL = "www.ty.l/";

    private int duration24HrsInMillis = (60000*60)*24;
    private int weekDurationInMillis = ((60000*60)*24)*7;
    private long activatedAtFor24hrsPeriod = Long.MAX_VALUE;
    private long activatedAtForWeekPeriod = Long.MAX_VALUE;

    private String mapkey24hrs = "urlAccessedPast24hrsCount";
    private String mapKeyPastWeek = "urlAccessedPastWeekCount";
    private String mapKeyAllTime = "urlAccessedAllTimeCount";

    private Map<String, Map<String,Integer>> trackShortURLMap = new HashMap<>();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
            if(tinyURLDAO == null) {
                tinyURLDAO = new TinyURLDAOIMPL();
            }

            switch (input.getResource()) {
                case "/getLongURL":
                    return getLongURL(input);
                case "/createShortURL":
                   return createShortURL(input);
                case "/deleteShortURL":
                    return deleteShortURL(input);
                case "/getShortURLStats":
                    return getShortURLStats(input);
                default:
                    throw new IllegalStateException("Unexpected value: " + input.getHttpMethod());
            }
    }

    private APIGatewayProxyResponseEvent getLongURL(APIGatewayProxyRequestEvent input) {
        String shortURL = input.getQueryStringParameters().get("shortURL");
        TinyURL tinyURL;
        try {
            tinyURL = tinyURLDAO.getLongURL(shortURL);
            trackURLAccessed(shortURL);
        } catch(Exception e){
            return constructErrorResponse(e);
        }
        return constructResponse(tinyURL);
    }

    private APIGatewayProxyResponseEvent createShortURL(APIGatewayProxyRequestEvent input){

        String longURL = input.getQueryStringParameters().get("longURL");
        try {
            if(doesShortURLExistsFor(longURL)) {
                return constructEmptyResponse();
            } else {
                String newShortURL = buildRandomUUID();
                TinyURL tinyURL = tinyURLDAO.createShortURL(newShortURL, longURL);
                return constructResponse(tinyURL);
            }
        } catch(Exception e){
            return constructErrorResponse(e);
        }
    }

    private APIGatewayProxyResponseEvent deleteShortURL(APIGatewayProxyRequestEvent input){
        try {
            TinyURL tinyURL = tinyURLDAO.deleteShortURL(input.getQueryStringParameters().get("shortURL"));
            return constructResponse(tinyURL);
        } catch(Exception e){
            return constructErrorResponse(e);
        }
    }

    private APIGatewayProxyResponseEvent getShortURLStats(APIGatewayProxyRequestEvent input){
        String shortURL = input.getQueryStringParameters().get("shortURL");
        try {
            Map<String, Integer> innerMap = trackShortURLMap.get(shortURL);
            Integer value24hrs = 0;
            Integer valuePastWeek = 0;

            if (isActive(duration24HrsInMillis, activatedAtFor24hrsPeriod)) {
                value24hrs = innerMap.get(mapkey24hrs);
            }

            if (isActive(weekDurationInMillis, activatedAtForWeekPeriod)) {
                valuePastWeek = innerMap.get(mapKeyPastWeek);
            }

            Stats stats = new Stats(value24hrs,valuePastWeek,innerMap.get(mapKeyAllTime));
            return constructResponse(stats);
        } catch(Exception e) {
            return constructErrorResponse(e);
        }
    }

    private boolean doesShortURLExistsFor(String longURL){
        List<TinyURL> results = tinyURLDAO.scanForLongURL(longURL);
        if(results.isEmpty() || results == null){
            return false;
        }
        return true;
    }


    private String buildRandomUUID() {
        return TinyURLLambda.TINYURL + RandomStringUtils.randomAlphanumeric(5);
    }

    private APIGatewayProxyResponseEvent constructResponse(Object tinyURL){
        Gson gson = new Gson();
        String output = gson.toJson(tinyURL);

        return constructHeadersResponse()
                .withStatusCode(200)
                .withBody(output);
    }

    private APIGatewayProxyResponseEvent constructErrorResponse(Exception e){
        return constructHeadersResponse()
                .withBody(e.toString())
                .withStatusCode(200);
    }

    private APIGatewayProxyResponseEvent constructEmptyResponse(){
        return constructHeadersResponse()
                .withStatusCode(200)
                .withBody("");
    }

    private APIGatewayProxyResponseEvent constructHeadersResponse(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        return new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
    }


    private void trackURLAccessed(String shortURL) {

        if(trackShortURLMap.containsKey(shortURL)){
            Map<String, Integer> innerMap = trackShortURLMap.get(shortURL);

            //allTime + 1
            innerMap.put(mapKeyAllTime, innerMap.get(mapKeyAllTime)+1);

            //24 hrs + 1
            if (isActive(duration24HrsInMillis, activatedAtFor24hrsPeriod)) {
                innerMap.put(mapkey24hrs, innerMap.get(mapkey24hrs)+1);
            } else {
                activate24hrsPeriod();
                innerMap.put(mapkey24hrs, 1);
            }

            //past week + 1
            if (isActive(weekDurationInMillis, activatedAtForWeekPeriod)) {
                innerMap.put(mapKeyPastWeek, innerMap.get(mapKeyPastWeek)+1);
            } else {
                activateWeekPeriod();
                innerMap.put(mapKeyPastWeek, 1);
            }
        } else {
            Map<String, Integer> innerMap = new HashMap<>();
            activate24hrsPeriod();
            innerMap.put(mapkey24hrs,1);
            activateWeekPeriod();
            innerMap.put(mapKeyPastWeek,1);
            innerMap.put(mapKeyAllTime,1);
            trackShortURLMap.put(shortURL, innerMap);
        }
    }

    public void activate24hrsPeriod() {
        activatedAtFor24hrsPeriod = System.currentTimeMillis();
    }

    public void activateWeekPeriod() {
        activatedAtForWeekPeriod = System.currentTimeMillis();
    }

    public boolean isActive(int DURATION, long activatedAt) {
        long activeFor = System.currentTimeMillis() - activatedAt;
        return activeFor >= 0 && activeFor <= DURATION;
    }
}
