package com.smalljobs.jobseeker.models;

import com.google.api.client.util.Key;

public class JobPoster {

	@Key
	private String fields;

	
	public String getName() {
		return fields;
	}


	public void setName(String name) {
		this.fields = name;
	}


	@Override
    public String toString() {
        return "JobPoster [Name=" + fields + "]";
    }

}
