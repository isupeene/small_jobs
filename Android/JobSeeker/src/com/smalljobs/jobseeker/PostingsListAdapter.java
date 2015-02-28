package com.smalljobs.jobseeker;

import java.util.ArrayList;
import java.util.Comparator;

import com.smalljobs.jobseeker.models.JobPosting;

import android.content.Context;
import android.provider.SyncStateContract.Constants;
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
		//final ListView list=(ListView)parent.findViewById(R.id.MainListView);
		TextView title = (TextView) convertView.findViewById(R.id.titleMain);
		TextView description = (TextView) convertView.findViewById(R.id.descriptionMain);
		
		final JobPosting posting = postings.get(position);
		
		title.setText(posting.getTitle());
		description.setText(posting.getDescription());
		
		return convertView;
	}
	

}
