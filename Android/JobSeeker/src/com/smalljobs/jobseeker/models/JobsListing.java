package com.smalljobs.jobseeker.models;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobsListing extends ArrayList<JobPosting> {

	public JobsListing() {
	}

	
}
