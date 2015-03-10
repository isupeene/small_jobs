package com.smalljobs.jobseeker;

import java.io.IOException;
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
        this.baseUrl = "http://172.28.216.12:8000/job_seeking/job_poster/" + id + "/";
    }

    @Override
    public JobPoster loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ) );
                
        String result = request.execute().parseAsString();

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(result).getAsJsonArray();
        
        ArrayList<JobPoster> posters = new ArrayList<JobPoster>();
        
        for(JsonElement obj : jArray )
        {
        	JsonObject o = obj.getAsJsonObject();
        	obj = o.get("fields");
            JobPoster poster = gson.fromJson( obj , JobPoster.class);
            posters.add(poster);
        }
        
        return posters.get(0);        
    }
    
    
}
