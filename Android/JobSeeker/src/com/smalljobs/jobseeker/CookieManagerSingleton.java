package com.smalljobs.jobseeker;

import java.net.CookieManager;

public class CookieManagerSingleton {

	    private static CookieManager cookieManager;
	    private static CookieManagerSingleton _me;
	    
	    private CookieManagerSingleton() {

	    }
	    
	    public static CookieManager getCookieManager() {
	        if ( cookieManager == null ) {
	        	cookieManager = new CookieManager();
	            _me = new CookieManagerSingleton();
	        }
	        return cookieManager;
	    }
}
