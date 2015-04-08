package com.smalljobs.jobseeker.views;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.smalljobs.jobseeker.NotificationsListAdapter;
import com.smalljobs.jobseeker.NotificationsManager;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.Notification;
import com.smalljobs.jobseeker.models.User;

/**
 * Home screen that displays list of unread notifications
 * 
 * Requirements Specifications Reference:
 * 3.2.2.3.2 Raise a notification when important events occur
 * 3.2.2.3.2.1 When a bid of theirs has been accepted or rejected, 
 *             or when a job on which they have bid is modified.
 */

public class MainActivity extends BaseActivity {
	

	TextView welcomeTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        welcomeTextView = (TextView) findViewById( R.id.name );
		
        welcomeTextView.setText("Welcome " + User.getInstance().getContractor().getName() + "!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	public static class NotificationsListFragment extends ListFragment {
        String[] strings = {"Cheese", "Pepperoni", "Black Olives"};
        String[] strings1 = {};
        NotificationsManager nm;
        ArrayList<Notification> notifications = new ArrayList<Notification>();
		NotificationsListAdapter notificationsViewAdapter;

        @Override
		public void onResume() {
			super.onResume();
			
			notifications = nm.getNotifications();

			notificationsViewAdapter.clear();
			if (notifications != null) {
				notificationsViewAdapter.addAll(notifications);
			}
			notificationsViewAdapter.notifyDataSetChanged();	
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
            View v = inflater.inflate(R.layout.fragment_notifications, container, false);
            
            return v;
        }

		@Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            SharedPreferences credentials = getActivity().getSharedPreferences("credentials", 0);
            nm = new NotificationsManager(getActivity(), credentials.getString("email", "default"));
            notificationsViewAdapter = new NotificationsListAdapter(getActivity(),
					R.layout.notification_row_layout, notifications);
            setListAdapter(notificationsViewAdapter);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
            selectJob(position);
        }
        
		public void selectJob(int position) {
			Intent detailIntent = new Intent(getActivity(),
					ViewPostingActivity.class);
			Notification notification = notifications.get(position);
			nm.deleteNotification(notification);
			detailIntent.putExtra("job", notification.getJob());
			detailIntent.putExtra("notification", true);
			startActivity(detailIntent);
		}
		
    }

}
