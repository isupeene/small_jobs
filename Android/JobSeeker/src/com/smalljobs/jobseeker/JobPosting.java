package com.smalljobs.jobseeker;

public class JobPosting {

	private long id;
	private String title;
	private String description;
	private Poster poster;
	
	public JobPosting(long id2, String title2, String description2) {
		// TODO Auto-generated constructor stub
		this.id=id2;
		this.title=title2;
		this.description=description2;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Poster getPoster() {
		return poster;
	}
	public void setPoster(Poster poster) {
		this.poster = poster;
	}
	

	
}
