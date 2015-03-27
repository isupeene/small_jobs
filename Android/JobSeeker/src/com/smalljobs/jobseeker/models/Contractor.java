package com.smalljobs.jobseeker.models;

import com.google.api.client.util.Key;

public class Contractor {
	
	@Key
	private String id;
	
	@Key
	private String name;
	
	@Key
	private String description;
	
	@Key
	private String phone_number;
	
	@Key
	private String email;
	
	@Key
	private String registration_id;

	public Contractor(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhoneNumber() {
		return phone_number;
	}

	public void setPhoneNumber(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getRegistrationId() {
		return registration_id;
	}

	public void setRegistrationId(String registration_id) {
		this.registration_id = registration_id;
	}

	public String toString() {
		return "Name:" + this.name;
		
	}
}
