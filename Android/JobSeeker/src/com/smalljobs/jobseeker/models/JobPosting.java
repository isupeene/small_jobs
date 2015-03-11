package com.smalljobs.jobseeker.models;

import java.io.Serializable;

import com.google.api.client.util.Key;


public class JobPosting implements Serializable {

	@Key
	private String id;
	
	@Key
	private String poster;
	
	@Key
	private String contractor;
	
	@Key
	private String creation_date;
	
	@Key
	private String short_description;
	
	@Key
	private String description;
	
	@Key
	private String bidding_deadline;
	
	@Key
	private String bidding_confirmation_deadline;
	
	@Key
	private String compensation_amount;
	
	@Key
	private String completion_date;
	
	@Key
	private Boolean bid_includes_compensation_amount;
	
	@Key
	private Boolean bid_includes_completion_date;
	
	@Key
	private Boolean completed;
	
	@Key
	private Boolean marked_completed_by_contractor;
	
	@Key
	private String date_completed;
	
	public JobPosting() {
		// TODO Auto-generated constructor stub
	}
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
	public String getPosterID() {
		return poster;
	}
	public void setPosterID(String poster) {
		this.poster = poster;
	}
	public String getContractor() {
		return contractor;
	}
	public void setContractor(String contractor) {
		this.contractor = contractor;
	}
	public String getCreationDate() {
		return creation_date;
	}
	public void setCreationDate(String creation_date) {
		this.creation_date = creation_date;
	}
	public String getBiddingDeadline() {
		return bidding_deadline;
	}
	public void setBiddingDeadline(String bidding_deadline) {
		this.bidding_deadline = bidding_deadline;
	}
	public String getBiddingConfirmationDeadline() {
		return bidding_confirmation_deadline;
	}
	public void setBiddingConfirmationDeadline(
			String bidding_confirmation_deadline) {
		this.bidding_confirmation_deadline = bidding_confirmation_deadline;
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
	public Boolean getBidIncludesCompensationAmount() {
		return bid_includes_compensation_amount;
	}
	public void setBidIncludesCompensationAmount(
			Boolean bid_includes_compensation_amount) {
		this.bid_includes_compensation_amount = bid_includes_compensation_amount;
	}
	public Boolean getBidIncludesCompletionDate() {
		return bid_includes_completion_date;
	}
	public void setBidIncludesCompletionDate(Boolean bid_includes_completion_date) {
		this.bid_includes_completion_date = bid_includes_completion_date;
	}
	public Boolean getCompleted() {
		return completed;
	}
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	public Boolean getMarkedAsComplete() {
		return marked_completed_by_contractor;
	}
	public void markAsComplete(
			Boolean marked_completed_by_contractor) {
		this.marked_completed_by_contractor = marked_completed_by_contractor;
	}
	public String getDateCompleted() {
		return date_completed;
	}
	public void setDateCompleted(String date_completed) {
		this.date_completed = date_completed;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

	
}
