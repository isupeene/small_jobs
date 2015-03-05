package com.smalljobs.jobseeker.models;


public class JobPosting {

	private String poster;
	private String contractor;
	private String creation_date;
	private String short_description;
	private String description;
	private String bidding_deadline;
	private String bidding_confirmation_deadline;
	private String compensation_amount;
	private String completion_date;
	private String bid_includes_compensation_amount;
	private String bid_includes_completion_date;
	private String completed;
	private String marked_completed_by_contractor;
	private String date_completed;
	
	public JobPosting() {
		// TODO Auto-generated constructor stub
	}
//	public long getId() {
//		return id;
//	}
//	public void setId(long id) {
//		this.id = id;
//	}
	public String getTitle() {
		return short_description;
	}
	public void setTitle(String title) {
		this.short_description = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	

	
}
