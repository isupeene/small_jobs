package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;

import roboguice.util.temp.Ln;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.JobPoster;

public class PosterProfileRequest extends GoogleHttpClientSpiceRequest< JobPoster > {

    private String baseUrl;

    public PosterProfileRequest(String id) {
        super( JobPoster.class );
        this.baseUrl = "http://192.168.1.75:8000/job_seeking/job_poster/" + id + "/";
    }

    @Override
    public JobPoster loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ) );
        
        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        String result = request.execute().parseAsString();

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(result).getAsJsonObject();
        
        JobPoster poster = gson.fromJson( obj , JobPoster.class);
        
        return poster;        
    }
    
    
}
