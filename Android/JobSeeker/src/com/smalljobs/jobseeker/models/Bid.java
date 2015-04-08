package com.smalljobs.jobseeker.models;

import com.google.api.client.util.Key;

public class Bid {
	
	@Key
	private String job;
	
	@Key
	private String contractor;
	
	@Key
	private String compensation_amount;
	
	@Key
	private String completion_date;
	
	@Key
	private String message;

	public Bid(String job, String contractor) {
		super();
		this.job = job;
		this.contractor = contractor;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	public String getCompensationAmount() {
		return compensation_amount;
	}

	public void setCompensationAmount(String compensation_amount) {
		this.compensation_amount = compensation_amount;
	}

	public String getCompletionDate() {
		return completion_date;
	}

	public void setCompletionDate(String completion_date) {
		this.completion_date = completion_date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
