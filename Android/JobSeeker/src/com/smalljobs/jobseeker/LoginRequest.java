package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import roboguice.util.temp.Ln;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.Contractor;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.Server;
import com.smalljobs.jobseeker.models.User;

/** 
* Requirements Specifications Reference:
* 3.2.2.1.2 Allow the user to login with any account they have created
* 3.2.2.1.3 Permit the user to remain logged in indefinitely (i.e. across multiple sessions).
*
*/

public class LoginRequest extends GoogleHttpClientSpiceRequest< String > {

	public static final String PREFS_NAME = "Credentials";
	
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
        
        
        String result = response.parseAsString();

        
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(result).getAsJsonObject();

        Contractor contractor = gson.fromJson( obj , Contractor.class);

        User.getInstance().setContractor(contractor);
        
        SharedPreferences credentials = context.getSharedPreferences(PREFS_NAME, 0);
        
        SharedPreferences.Editor editor = credentials.edit();
        editor.putString("email", email);
        editor.commit();
        
        
        
        return result;
    }
    
    
}