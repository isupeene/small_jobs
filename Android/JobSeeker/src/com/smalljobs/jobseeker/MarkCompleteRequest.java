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
import com.smalljobs.jobseeker.models.Server;

public class MarkCompleteRequest extends GoogleHttpClientSpiceRequest< String > {
	
    private String baseUrl;
	private Context context;
	private String jobID;

    public MarkCompleteRequest(Context context, String job_id) {
        super( String.class );
        this.context = context;
        this.jobID = job_id;
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/mark_complete/" + job_id + "/";
    }

    @Override
    public String loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        

        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), jobID);
        
        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest( new GenericUrl( baseUrl ), content);
                
        HttpResponse response = request.execute();
		
        
        return response.parseAsString();
    }
}
