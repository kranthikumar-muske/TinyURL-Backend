package com.tiny.url.dynamoDB;

import com.tiny.url.dynamoDB.table.mapping.TinyURL;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface TinyURLDAO {

    TinyURL getLongURL(String shortURL) throws ExecutionException;

    TinyURL createShortURL(String shortURL, String longURL);

    TinyURL deleteShortURL(String shortURL);

    List<TinyURL> scanForLongURL(String longURL);
}
