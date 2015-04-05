package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.Server;

public class RatingGetRequest extends GoogleHttpClientSpiceRequest< String > {
	
    private String baseUrl;

    public RatingGetRequest(String posterId) {
        super( String.class );
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/job_poster_rating/" + posterId + "/";
    }

    @Override
    public String loadDataFromNetwork() throws IOException {
        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ) );
                
        HttpResponse response = request.execute();
		
        
        return response.parseAsString();
    }

}
