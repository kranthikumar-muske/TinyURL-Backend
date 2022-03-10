package com.tiny.url.dynamoDB;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tiny.url.dynamoDB.table.mapping.TinyURL;
import javafx.beans.binding.MapExpression;

import java.util.HashMap;
import java.util.Map;

public class HelloDynamo {

    private static final int DURATION = 5000;
    private static long activatedAt = Long.MAX_VALUE;
    private static int variable = 0;


    static Map<String, Map<String, Integer>> trackShortURLs = new HashMap<>();


    public static void main(String args[]) {

        String url1 = "www.hi.com";
        String url2 = "www.2.com";

        if(!trackShortURLs.containsKey(url1)){
            Map<String, Integer> innerMap = new HashMap<>();
            innerMap.put("24hrs",1);
            innerMap.put("week",1);
            innerMap.put("alltime",1);

            trackShortURLs.put(url1, innerMap);
        }

        if(trackShortURLs.containsKey(url1)){
            Map<String, Integer> innerMap = trackShortURLs.get(url1);
            innerMap.put("alltime", innerMap.get("alltime")+1);
            Map<String, Integer> innerMap2 = trackShortURLs.get(url1);
        }



        activate();
        if(isActive()){
            variable = variable+1;
        }
        System.out.println(variable);

    }

    public static void activate() {
        activatedAt = System.currentTimeMillis();
    }

    public static boolean isActive() {
        long activeFor = System.currentTimeMillis() - activatedAt;
        return activeFor >= 0 && activeFor <= DURATION;
    }
}
