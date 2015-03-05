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
import com.smalljobs.jobseeker.models.JobPosting;
import com.smalljobs.jobseeker.models.JobsListing;

public class JobsGetRequest extends GoogleHttpClientSpiceRequest< JobsListing > {

    private String baseUrl;

    public JobsGetRequest( ) {
        super( JobsListing.class );
        this.baseUrl = "http://172.28.93.50:8000/job_seeking/jobs";
    }

    @Override
    public JobsListing loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ) );
                
        String result = request.execute().parseAsString();

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(result).getAsJsonArray();
        
        JobsListing postings = new JobsListing();
        
        for(JsonElement obj : jArray )
        {
        	JsonObject o = obj.getAsJsonObject();
        	obj = o.get("fields");
            JobPosting posting = gson.fromJson( obj , JobPosting.class);
            postings.add(posting);
        }
        
        return postings;        
    }
    
    
}
