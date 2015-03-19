package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import roboguice.util.temp.Ln;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.Contractor;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.Server;
import com.smalljobs.jobseeker.models.User;

public class UserProfileRequest extends GoogleHttpClientSpiceRequest< Contractor > {

    private String baseUrl;

    public UserProfileRequest() {
        super( Contractor.class );
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/profile/";
    }

    @Override
    public Contractor loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ) );
        
        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        System.out.println("Profile request");
        System.out.println(cookieManager.getCookieStore().getCookies().size());
        
        String result = request.execute().parseAsString();

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(result).getAsJsonObject();
        
        Contractor contractor = gson.fromJson( obj , Contractor.class);
        
        User.getInstance().setContractor(contractor);
        
        return contractor;        
    }
    
    
}
