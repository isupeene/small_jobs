package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import roboguice.util.temp.Ln;
import android.content.Context;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.Rating;
import com.smalljobs.jobseeker.models.Server;

public class RatingPostRequest extends GoogleHttpClientSpiceRequest< String > {
	
    private String baseUrl;
	private Rating rating;

    public RatingPostRequest(Context context, String poster, String contractor, int rating) {
        super( String.class );
        this.rating = new Rating(poster, contractor, rating);
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/rate_job_poster/" + poster + "/" + rating + "/";
    }

    @Override
    public String loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        
        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), rating);
        
        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest( new GenericUrl( baseUrl ), content);
                
        HttpResponse response = request.execute();
		
        return response.parseAsString();
    }

}
