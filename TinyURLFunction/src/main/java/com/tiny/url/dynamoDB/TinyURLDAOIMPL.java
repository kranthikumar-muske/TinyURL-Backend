package com.tiny.url.dynamoDB;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.cache.LoadingCache;
import com.tiny.url.cache.LoadURLSFromCache;
import com.tiny.url.dynamoDB.table.mapping.TinyURL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TinyURLDAOIMPL implements TinyURLDAO{

    private DynamoDBMapper dynamoDBMapper;

    public TinyURLDAOIMPL(){
        dynamoDBMapper = new DynamoDBMapper(buildDynamoDBClient());
    }

    @Override
    public TinyURL getLongURL(String shortURL) throws ExecutionException {
        final LoadingCache<String, TinyURL> cache =
                LoadURLSFromCache.getInstance(dynamoDBMapper).getCache();

        return cache.get(shortURL);
    }

    @Override
    public TinyURL createShortURL(String shortURL, String longURL) {
        TinyURL tinyURL = new TinyURL();
        tinyURL.setShortURL(shortURL);
        tinyURL.setLongURL(longURL);

        //write to DynamoDB
        dynamoDBMapper.save(tinyURL);

        //write to cache
        final LoadingCache<String, TinyURL> cache =
                LoadURLSFromCache.getInstance(dynamoDBMapper).getCache();
        cache.put(shortURL,tinyURL);

        return tinyURL;
    }

    @Override
    public TinyURL deleteShortURL(String shortURL) {
        //remove key from cache first
        final LoadingCache<String, TinyURL> cache =
                LoadURLSFromCache.getInstance(dynamoDBMapper).getCache();
        cache.invalidate(shortURL);

        //delete data from dynamoDB
        TinyURL tinyURL = dynamoDBMapper.load(TinyURL.class,shortURL);
        dynamoDBMapper.delete(tinyURL);
        return tinyURL;
    }

    @Override
    public List<TinyURL> scanForLongURL(String longURL) {
        Map<String, String> attributeNames = new HashMap<String, String>();
        attributeNames.put("#longURL", "longURL");

        Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
        attributeValues.put(":longURL", new AttributeValue().withS(longURL));

        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression()
                .withFilterExpression("begins_with(#longURL, :longURL)")
                .withExpressionAttributeNames(attributeNames)
                .withExpressionAttributeValues(attributeValues);

        return dynamoDBMapper.scan(TinyURL.class, dynamoDBScanExpression);
    }

    private AmazonDynamoDB buildDynamoDBClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .build();
    }

}
