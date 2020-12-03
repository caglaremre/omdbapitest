package com.emrecaglar.test;

import com.emrecaglar.model.Movie;
import com.emrecaglar.model.SearchResponse;
import com.emrecaglar.model.SearchResult;
import com.emrecaglar.model.Type;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.get;

public class OmdbAPITest {

    static Properties properties;
    static InputStream stream;
    SearchResponse searchResponse;
    String imdbId;
    Movie movie;

    @Before
    public void readProperties() {
        properties = new Properties();
        try {
            stream = OmdbAPITest.class.getResourceAsStream("/test.properties");
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void get_url() {
        String bySearchUrl = properties.getProperty("omdbapi.url")
                + "?apikey=" + properties.getProperty("apikey")
                + "&s=Harry Potter"
                + "&page=";
        outerloop:
        for (int i = 1; i < 101; i++) {
            searchResponse = get(bySearchUrl + i)
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(SearchResponse.class);
            if (searchResponse.getResponse().equals("True")) {
                for (SearchResult sr : searchResponse.getSearchResults()) {
                    if (sr.getTitle().equals("Harry Potter and the Sorcerer's Stone") && sr.getType().equals(Type.movie)) {
                        imdbId = sr.getImdbId();
                        break outerloop;
                    }
                }
            }
        }
        String byIdurl = properties.getProperty("omdbapi.url")
                + "?apikey=" + properties.getProperty("apikey")
                + "&i=" + imdbId
                + "&type=" + Type.movie;
        movie = get(byIdurl)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(Movie.class);
        Assert.assertTrue(!movie.getTitle().equals("N/A"));
        Assert.assertTrue(!movie.getYear().equals("N/A"));
        Assert.assertTrue(!movie.getReleased().equals("N/A"));
    }

}
