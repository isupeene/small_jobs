package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.JobPoster;
import com.smalljobs.jobseeker.models.Server;

/** 
* Requirements specifications reference:
* 3.2.2.2.3 Allow the user to view the profile of the job poster including their rating
*/

public class PosterProfileRequest extends GoogleHttpClientSpiceRequest< JobPoster > {

    private String baseUrl;

    public PosterProfileRequest(String id) {
        super( JobPoster.class );
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/job_poster/" + id + "/";
    }

    @Override
    public JobPoster loadDataFromNetwork() throws IOException {
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
