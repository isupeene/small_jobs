package com.smalljobs.jobseeker.models;

import java.io.Serializable;

import com.google.api.client.util.Key;

public class JobPoster implements Serializable {

	private static final long serialVersionUID = 1L;

	@Key
	private String name;
	
	@Key
	private String email;

	@Key
	private String phone_number;
	
	@Key
	private String description;
	
	@Key
	private String id;
	
	@Key
	private String region;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhoneNumber() {
		return phone_number;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phone_number = phoneNumber;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Override
    public String toString() {
        return name;
    }

}
