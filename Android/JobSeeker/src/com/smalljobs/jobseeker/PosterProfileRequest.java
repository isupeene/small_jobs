package com.smalljobs.jobseeker;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import roboguice.util.temp.Ln;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.JobPoster;

public class PosterProfileRequest extends GoogleHttpClientSpiceRequest< JobPoster > {

    private String baseUrl;

    public PosterProfileRequest( ) {
        super( JobPoster.class );
        this.baseUrl = "http://192.168.1.75:8000/job_seeking/job_poster/2/";
    }

    @Override
    public JobPoster loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ) );
        request.setParser( new JacksonFactory().createJsonObjectParser() );
        GsonFactory gson = new GsonFactory();
        
        
        
        //JobPoster poster = new JobPoster();
        //poster.setName("John");
        String result = request.execute().parseAsString();
        //System.out.println(result);
        Type listType = new TypeToken<ArrayList<JobPoster>>() {
        }.getType();
        List<JobPoster> posters = new Gson().fromJson(result, listType);
        return posters.get(0);
        //return gson.createJsonParser(result).parse(getResultType());
        //return request.execute().parseAs(getResultType());
    }
    
    
}
