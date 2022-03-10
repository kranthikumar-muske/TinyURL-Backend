package com.tiny.url.dynamoDB.table.mapping;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="TinyURL")
public class TinyURL {

    //Hash key and sort key
    @DynamoDBHashKey(attributeName = "shortURL")
    private String shortURL;

    //attributes on the table
    @DynamoDBAttribute(attributeName = "longURL")
    private String longURL;

    @DynamoDBHashKey
    public String getShortURL() {
        return shortURL;
    }

    public void setShortURL(String shortURL) {
        this.shortURL = shortURL;
    }

    public String getLongURL() {
        return longURL;
    }

    public void setLongURL(String longURL) {
        this.longURL = longURL;
    }
}
