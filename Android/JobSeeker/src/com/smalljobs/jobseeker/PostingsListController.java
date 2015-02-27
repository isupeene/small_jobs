package com.smalljobs.jobseeker;

import java.io.IOException;

public class PostingsListController {
	private PostingsList pl;
	private DataManager dm;

	public PostingsListController(PostingsList pl) {
		super();
		this.pl = pl;
	}
	
	public void refreshPostings() throws IOException {
		pl.setJobs(dm.loadPostings());
	}
}
