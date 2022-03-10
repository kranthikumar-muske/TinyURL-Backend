package com.tiny.url.cache;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import com.tiny.url.dynamoDB.TinyURLDAO;
import com.tiny.url.dynamoDB.table.mapping.TinyURL;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.util.concurrent.TimeUnit;

/** Cache used for Retrieving the S3 relationship data for a given clinical event version. */
public class LoadURLSFromCache
        extends CacheLoader<String, TinyURL> {

    private static LoadURLSFromCache instance;
    private LoadingCache<String, TinyURL> cache;
    private static DynamoDBMapper dynamoDBMapperinstance;

    private LoadURLSFromCache() {
        cache =
                CacheBuilder.newBuilder()
                        .maximumSize(1000)
                        .expireAfterAccess(60, TimeUnit.MINUTES)
                        .build(this);
    }

    public static synchronized LoadURLSFromCache getInstance(DynamoDBMapper dynamoDBMapper) {
        if (instance == null) {
            instance = new LoadURLSFromCache();
            dynamoDBMapperinstance = dynamoDBMapper;
        }
        return instance;
    }

    @Override
    public TinyURL load(String shortURL) throws Exception {
        return dynamoDBMapperinstance.load(TinyURL.class, shortURL);
    }

    public LoadingCache<String, TinyURL> getCache() {
        return cache;
    }
}
