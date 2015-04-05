package com.smalljobs.jobseeker.models;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobsListing extends ArrayList<JobPosting> {

	private static final long serialVersionUID = 1L;

	public JobsListing() {
	}

	
}
