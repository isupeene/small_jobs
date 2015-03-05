package com.smalljobs.jobseeker.models;

import com.google.gson.annotations.SerializedName;

public class JobPoster {

	private int openid;
	
	private String name;
	
	private String email;

	@SerializedName("phone_number")
	private String phoneNumber;
	
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
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	private String description;
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
    public String toString() {
        return "JobPoster [Name=" + name + "]";
    }

}
