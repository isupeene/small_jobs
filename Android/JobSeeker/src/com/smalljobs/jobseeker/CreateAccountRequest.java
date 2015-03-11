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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.Contractor;
import com.smalljobs.jobseeker.models.JobPoster;
import com.smalljobs.jobseeker.models.User;

public class CreateAccountRequest extends GoogleHttpClientSpiceRequest< Contractor > {

    private String baseUrl;
    private Contractor contractor;
    private Context context;
    

    public CreateAccountRequest(Context context, String email, String name) {
        super( Contractor.class );
        contractor = new Contractor(name, email);
        this.context = context;
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/create_account/";
    }

    @Override
    public Contractor loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        
        JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), contractor);

        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        
        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest( new GenericUrl( baseUrl ), content);
        
        HttpResponse response = request.execute();
        
        String result = response.parseAsString();
        
        System.out.println(result);
        
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(result).getAsJsonObject();

        contractor = gson.fromJson( obj , Contractor.class);

        User.getInstance().setContractor(contractor);
        
        return contractor;        
    }
    
    
}