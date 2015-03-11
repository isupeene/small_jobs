package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import roboguice.util.temp.Ln;
import android.content.Context;
import android.util.Base64;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.Contractor;

public class LoginRequest extends GoogleHttpClientSpiceRequest< Contractor > {

    private String baseUrl;
    private Contractor contractor;
    private Context context;
    
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Auth = "authKey"; 

    public LoginRequest(Context context, String email) {
        super( Contractor.class );
        this.context = context;
        this.baseUrl = "http://192.168.1.75:8000/job_seeking/login/";
    }

    @Override
    public Contractor loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        

        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        
        HttpRequest request = getHttpRequestFactory()//
                .buildGetRequest( new GenericUrl( baseUrl ));
        
        HttpHeaders headers = new HttpHeaders();
        final String basicAuth = "Basic " + Base64.encodeToString("mujda@example.com:".getBytes(), Base64.NO_WRAP);
        
        headers.setBasicAuthentication("mujda@example.com", "none");
        
		request.setHeaders(headers);
        
		
		System.out.println(headers.getAuthorization());
        HttpResponse response = request.execute();
        
        //SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //Editor editor = sharedpreferences.edit();
        //editor.putString(Auth, response.parseAsString());
        //editor.commit();
        
        System.out.println(response.parseAsString());
		//System.out.println("is it empty" + cookieManager.getCookieStore().getCookies().isEmpty());
        //System.out.println(response.getHeaders().get("Set-cookie"));
        //System.out.println(cookieManager.getCookieStore().getCookies().get(0).toString());
        
		contractor = new Contractor("Mujda", "mujda@example.com");
		
        return contractor;
    }
    
    
}