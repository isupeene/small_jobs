package com.smalljobs.jobseeker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import com.smalljobs.jobseeker.models.JobPosting;

import android.content.Context;
import android.graphics.Color;
import android.provider.SyncStateContract.Constants;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This is a custom adapter for displaying a list of questions.
 * 
 * It is used in the MainActivity for the main list of questions
 * and in the UserThreadsActivity to display locally stored questions
 * with a few changes.
 *
 * This class extends ArrayAdapter<QuestionThread> and uses an ArrayList
 * of QuestionThread objects to display the summary of a list of questions using
 * a custom layout.
 *
 */

public class PostingsListAdapter extends ArrayAdapter<JobPosting> {

	private ArrayList<String> ids;
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(this.resId, parent, false);
		}
		
		TextView title = (TextView) convertView.findViewById(R.id.titleMain);
		TextView biddingDeadline = (TextView) convertView.findViewById(R.id.biddingDeadlineMain);
		TextView compensationAmount = (TextView) convertView.findViewById(R.id.compensationAmountMain);
		TextView completionDate = (TextView) convertView.findViewById(R.id.completionDateMain);
		
		final JobPosting job = postings.get(position);
				
		SpannableString ss;
		
		title.setText(job.getTitle());
		
		
		Date date = null;
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			date = df1.parse(job.getBiddingDeadline());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ss =  new SpannableString("Bidding Deadline: \n" + date);
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 17, 0);
		biddingDeadline.setText(ss);
		
		date = null;
		try {
			date = df1.parse(job.getBiddingConfirmationDeadline());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ss =  new SpannableString("Compensation Amount: Not specified");
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 20, 0);
		compensationAmount.setText(ss);
		
		ss =  new SpannableString("Completion Date: Not specified");
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 16, 0);
		completionDate.setText(ss);
		
		
		return convertView;
	}
	

}
