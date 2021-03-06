package com.smalljobs.jobseeker;

import java.net.CookieHandler;
import java.net.CookieManager;

import roboguice.util.temp.Ln;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.smalljobs.jobseeker.models.Bid;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.Server;
import com.smalljobs.jobseeker.models.User;

/**
 * Requirements specifications reference:
 * 3.2.2.3.1 Permit the users to bid on available jobs.
 * 3.2.2.3.1.1 Bids shall include a Compensation Amount and a Completion Date, if required by the Job Posting
 */

public class BidPostRequest extends GoogleHttpClientSpiceRequest< String > {

    private String baseUrl;
    private Bid bid;
    
	
	public BidPostRequest(String job, String compensationAmount, String completionDate, String message) {
		super(String.class);
		bid = new Bid(job, User.getInstance().getContractor().getId());
		if(compensationAmount != null) {
			bid.setCompensationAmount(compensationAmount);
		}
		if(completionDate != null) {
			bid.setCompletionDate(completionDate);
		}
		bid.setMessage(message);
        this.baseUrl = "http://"+ Server.ipaddress +":8000/job_seeking/bid/";
	}
	
	
	@Override
	public String loadDataFromNetwork() throws Exception {

        Ln.d( "Call web service " + baseUrl );
        
        JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), bid);

        CookieManager cookieManager = CookieManagerSingleton.getCookieManager();
        CookieHandler.setDefault(cookieManager);
        
        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest( new GenericUrl( baseUrl ), content);
        
        HttpResponse response = request.execute();
        
		return response.parseAsString();
	}

}
