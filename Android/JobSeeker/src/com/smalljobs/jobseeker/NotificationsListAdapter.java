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
* Requirements Specifications Reference:
* 3.2.2.3.2 Raise a notification when important events occur
* 3.2.2.3.2.1 When a bid of theirs has been accepted or rejected, 
*             or when a job on which they have bid is modified.
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
	
}
