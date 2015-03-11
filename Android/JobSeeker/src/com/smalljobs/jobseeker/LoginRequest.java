package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import roboguice.util.temp.Ln;
import android.content.Context;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

public class LoginRequest extends GoogleHttpClientSpiceRequest< String > {

    private String baseUrl;
    private String email;
    private Context context;
    

    public LoginRequest(Context context, String email) {
        super( String.class );
        this.context = context;
        this.email = email;
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/login/";
    }

    @Override
    public String loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        

        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ));
        
        HttpHeaders headers = new HttpHeaders();
        
        headers.setBasicAuthentication(email, "none");
        
		request.setHeaders(headers);
        
        HttpResponse response = request.execute();
        
		
        return response.parseAsString();
    }
    
    
}