package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.JobPosting;
import com.smalljobs.jobseeker.models.JobsListing;
import com.smalljobs.jobseeker.models.Server;

/**
* Requirements Specifications Reference: 
* 3.2.2.3.3 Allow users to view all the jobs that they have successfully bid on,
*  filterable by whether or not they have been completed.
* 3.2.2.2.1 Permit users to browse available jobs, organized/filtered by Skills.
* 3.2.2.2.2 Permit users to organize/filter jobs by region.
*/

public class JobsGetRequest extends GoogleHttpClientSpiceRequest< JobsListing > {

    private String baseUrl;

    public JobsGetRequest( Context context, String type ) {
        super( JobsListing.class );
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/" + type;
    }
    
    public JobsGetRequest( Context context, String type, String location, String skills ) {
        super( JobsListing.class );
        
        String skillsQuery = null;
        if (!skills.isEmpty()) {
        	List<String> list = Arrays.asList(skills.split("\\s*,\\s*"));
        	skillsQuery = "/?skill=" + list.get(0).replaceAll("\\s+", "%20").trim();
        	for (int i = 1; i < list.size()-1; i++) {
        		skillsQuery.concat("&skill=" + list.get(i).replaceAll("\\s+", "%20").trim());
        	}
        }
        
        if (location.isEmpty()) {
        	if (skills.isEmpty()) {
        		this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/" + type;
        	} else {
        		this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/" + type + skillsQuery;
        	}
        } else if (skills.isEmpty()) {
        	this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/" + type + "/?region=" + location;
        } else {
        	
        	this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/" + type + skillsQuery + "&region=" + location;
        }
        
    }

    @Override
    public JobsListing loadDataFromNetwork() throws IOException {
    	
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ) );

        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        
        String result = request.execute().parseAsString();

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(result).getAsJsonArray();
        
        JobsListing postings = new JobsListing();
        
        for(JsonElement obj : jArray )
        {
            JobPosting posting = gson.fromJson( obj , JobPosting.class);
            postings.add(posting);
        }
        
        return postings;        
    }
    
    
}
