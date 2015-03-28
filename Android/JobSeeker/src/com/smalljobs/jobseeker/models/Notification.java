package com.smalljobs.jobseeker.models;

import com.google.api.client.util.Key;

public class Notification {

	@Key
	private JobPosting job;
	
	@Key
	private String type;
	
	public Notification(JobPosting job, String type) {
		super();
		this.job = job;
		this.type = type;
	}

	public JobPosting getJob() {
		return job;
	}

	public void setJob(JobPosting job) {
		this.job = job;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        Notification other = (Notification) obj;
        if ( !type.equals(other.getType()) ) {
            return false;
        }
        if (!job.equals(other.getJob())) {
        	return false;
        }
        return true;
    }
	
}
