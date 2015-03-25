package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import roboguice.util.temp.Ln;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.Server;

public class LogoutRequest extends GoogleHttpClientSpiceRequest< String > {

	public static final String PREFS_NAME = "Credentials";
	
    private String baseUrl;
	private Context context;

    public LogoutRequest(Context context) {
        super( String.class );
        this.context = context;
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/logout/";
    }

    @Override
    public String loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        

        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ));
                
        HttpResponse response = request.execute();
		
        SharedPreferences credentials = context.getSharedPreferences(PREFS_NAME, 0);
        
        SharedPreferences.Editor editor = credentials.edit();
        
        editor.clear();
        
        editor.commit();
        
        return response.parseAsString();
    }
    
    
}