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
import com.smalljobs.jobseeker.models.Contractor;

public class CreateAccountRequest extends GoogleHttpClientSpiceRequest< Contractor > {

    private String baseUrl;
    private Contractor contractor;
    private Context context;
    
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Auth = "authKey"; 

    public CreateAccountRequest(Context context, String email, String name) {
        super( Contractor.class );
        contractor = new Contractor(name, email);
        this.context = context;
        this.baseUrl = "http://192.168.1.75:8000/job_seeking/create_account/";
    }

    @Override
    public Contractor loadDataFromNetwork() throws IOException {
        Ln.d( "Call web service " + baseUrl );
        
        JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), contractor);

        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        System.out.println("is it empty" + cookieManager.getCookieStore().getCookies().isEmpty());
        
        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest( new GenericUrl( baseUrl ), content);
        
        HttpResponse response = request.execute();
        
        //SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //Editor editor = sharedpreferences.edit();
        //editor.putString(Auth, response.parseAsString());
        //editor.commit();
        System.out.println("is it empty" + cookieManager.getCookieStore().getCookies().isEmpty());
        System.out.println(response.getHeaders().get("Set-cookie"));

        System.out.println(cookieManager.getCookieStore().getCookies().get(0).toString());
        
        return contractor;        
    }
    
    
}