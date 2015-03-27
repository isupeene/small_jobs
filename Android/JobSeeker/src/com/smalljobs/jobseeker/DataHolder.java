package com.smalljobs.jobseeker;

import com.smalljobs.jobseeker.models.JobPosting;
import com.smalljobs.jobseeker.models.JobsListing;

public class DataHolder {

	private static DataHolder holder;
	private JobsListing potentialJobs = null;

	private DataHolder() {

	}

	public static DataHolder getInstance() {
		if (holder == null) {
			holder = new DataHolder();
		}
		return holder;
	}
	

	public JobsListing getPotentialJobs() {
		return potentialJobs;
	}
	
	public void setPotentialJobs(JobsListing jobs) {
		potentialJobs = jobs;
	}
	
	public void addPotentialJob(JobPosting job) {
		potentialJobs.add(job);
	}
	
}