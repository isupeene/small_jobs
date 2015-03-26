package com.smalljobs.jobseeker.models;

import com.google.api.client.util.Key;

public class Rating {
	
	@Key
	private String poster;
	
	@Key
	private String contractor;
	
	@Key
	private int rating;

	public Rating(String poster, String contractor, int rating) {
		super();
		this.poster = poster;
		this.contractor = contractor;
		this.rating = rating;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	
}
