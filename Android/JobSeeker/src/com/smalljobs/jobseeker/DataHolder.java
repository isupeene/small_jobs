package com.smalljobs.jobseeker;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {

	private static DataHolder holder;
	private List<String> bids = new ArrayList<String>();

	private DataHolder() {

	}

	public static DataHolder getInstance() {
		if (holder == null) {
			holder = new DataHolder();
		}
		return holder;
	}
	

	public List<String> getBids() {
		return bids;
	}
	
	public void addBid(String data) {
		this.bids.add(data);
	}
}