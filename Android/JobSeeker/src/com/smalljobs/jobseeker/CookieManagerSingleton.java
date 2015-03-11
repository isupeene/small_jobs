package com.smalljobs.jobseeker;

import java.io.IOException;
import java.net.CookieManager;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public class CookieManagerSingleton {

	    private static DefaultHttpClient _defaultClient;
	    private static String session_id;
	    private static CookieManager cookieManager;
	    private static CookieManagerSingleton _me;
	    private CookieManagerSingleton() {

	    }
	    public static DefaultHttpClient getDefaultHttpClient(){
	        if ( _defaultClient == null ) {
	            _defaultClient = new DefaultHttpClient();
	            _me = new CookieManagerSingleton();
	            _defaultClient.addResponseInterceptor(_me.new SessionKeeper());
	            _defaultClient.addRequestInterceptor(_me.new SessionAdder());
	        }
	        return _defaultClient;
	    }

	    private class SessionAdder implements HttpRequestInterceptor {

	        @Override
	        public void process(HttpRequest request, HttpContext context)
	                throws HttpException, IOException {
	            if ( session_id != null ) {
	                request.setHeader("Cookie", session_id);
	            }
	        }

	    }

	    private class SessionKeeper implements HttpResponseInterceptor {

	        @Override
	        public void process(HttpResponse response, HttpContext context)
	                throws HttpException, IOException {
	            Header[] headers = response.getHeaders("Set-Cookie");
	            if ( headers != null && headers.length == 1 ){
	                session_id = headers[0].getValue();
	            }
	        }

	    }
	    
	    public static CookieManager getCookieManager() {
	        if ( cookieManager == null ) {
	        	cookieManager = new CookieManager();
	            _me = new CookieManagerSingleton();
	        }
	        return cookieManager;
	    }
}
