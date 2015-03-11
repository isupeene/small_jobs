package com.smalljobs.jobseeker.models;

import java.io.Serializable;

import com.google.api.client.util.Key;
import com.google.gson.annotations.SerializedName;

public class JobPoster implements Serializable {

	@Key
	private int openid;
	
	@Key
	private String name;
	
	@Key
	private String email;

	@Key
	private String phone_number;
	
	@Key
	private String description;
	
	public int getOpenid() {
		return openid;
	}


	public void setOpenid(int openid) {
		this.openid = openid;
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


	@Override
    public String toString() {
        return name;
    }

}
