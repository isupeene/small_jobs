package com.smalljobs.jobseeker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smalljobs.jobseeker.models.Notification;

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

public class NotificationsListAdapter extends ArrayAdapter<Notification> {

	private ArrayList<Notification> notifications;
	private Context context;
	private int resId;
	
	
	public NotificationsListAdapter(Context context, int layoutResourceId, ArrayList<Notification> notifications) {
		super(context, layoutResourceId, notifications);
		this.notifications = notifications;
		this.context = context;
		this.resId=layoutResourceId;
	}
	
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(this.resId, parent, false);
		}

		TextView type = (TextView) convertView.findViewById(R.id.notiType);
		TextView title = (TextView) convertView.findViewById(R.id.notiTitle);
		
		final Notification notification = notifications.get(position);
				
		type.setText(notification.getType());
		
		title.setText(notification.getJob().getTitle());
		
		return convertView;
	}
	
	public void refresh(ArrayList<Notification> notifications)
    {
        this.notifications = notifications;
        notifyDataSetChanged();
    } 

}
