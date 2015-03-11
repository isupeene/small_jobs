package com.smalljobs.jobseeker.models;


public class User {

	private Contractor contractor;

	private static User instance = null;

	protected User() {
		// Exists only to defeat instantiation.
	}
	public static User getInstance() {
		if(instance == null) {
			instance = new User();
		}
		return instance;
	}

	public Contractor getContractor() {
		return contractor;
	}

	public void setContractor(Contractor contractor) {
		contractor = contractor;
	}
}
