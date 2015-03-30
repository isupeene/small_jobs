package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import roboguice.util.temp.Ln;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.Contractor;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.Server;
import com.smalljobs.jobseeker.models.User;

public class UserProfilePostRequest extends GoogleHttpClientSpiceRequest< String > {

    private String baseUrl;
    private Contractor contractor;

    public UserProfilePostRequest(Contractor contractor) {
        super( String.class );
        this.contractor = contractor;
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/profile/";
    }

    @Override
    public String loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        
        JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), contractor);
        
        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest( new GenericUrl( baseUrl ), content );
        
        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
                
        String result = request.execute().parseAsString();
        
        if (result.contains("OK")) {
        	User.getInstance().setContractor(contractor);
        }
        
        return result;        
    }
    
    
}
