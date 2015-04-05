package com.smalljobs.jobseeker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smalljobs.jobseeker.models.JobPosting;


public class PostingsListAdapter extends ArrayAdapter<JobPosting> {

	private ArrayList<JobPosting> postings;
	private Context context;
	private int resId;
	
	
	public PostingsListAdapter(Context context, int layoutResourceId, ArrayList<JobPosting> postings) {
		super(context, layoutResourceId, postings);
		this.postings = postings;
		this.context = context;
		this.resId=layoutResourceId;
	}
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(this.resId, parent, false);
		}
		
		TextView title = (TextView) convertView.findViewById(R.id.titleMain);
		TextView biddingDeadline = (TextView) convertView.findViewById(R.id.biddingDeadlineMain);
		TextView compensationAmount = (TextView) convertView.findViewById(R.id.compensationAmountMain);
		TextView completionDate = (TextView) convertView.findViewById(R.id.completionDateMain);
		
		JobPosting job = postings.get(position);
				
		SpannableString ss;
		
		title.setText(job.getTitle());
				
		Date date = null;
		
		date = parseDate(job.getBiddingDeadline());
		ss =  new SpannableString("Bidding Deadline: \n" + date);
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 17, 0);
		biddingDeadline.setText(ss);
		
		if (job.getCompensationAmount() != null) {
			ss =  new SpannableString("Compensation Amount: $" + job.getCompensationAmount());			
		} else {
			ss =  new SpannableString("Compensation Amount: Not specified");			
		}
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 20, 0);
		compensationAmount.setText(ss);
		
		date = null;
		if (job.getCompletionDate() != null) {
			date = parseDate(job.getCompletionDate());
			ss =  new SpannableString("Completion Date: " + date);
		} else {
			ss =  new SpannableString("Completion Date: Not specified");
		}
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 16, 0);
		completionDate.setText(ss);
		
		
		return convertView;
	}
	
	private Date parseDate(String dateString) {
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA);
		
		Date date = null;
		
		try {
			date = df1.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}

}
