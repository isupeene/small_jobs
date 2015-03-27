package com.smalljobs.jobseeker.views;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.JobsGetRequest;
import com.smalljobs.jobseeker.PostingsListAdapter;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.JobsListing;
import com.smalljobs.jobseeker.models.User;

public class MainActivity extends BaseActivity {
	

	TextView mLoremTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//context = getApplicationContext();
        mLoremTextView = (TextView) findViewById( R.id.name );
		
        mLoremTextView.setText("Welcome " + User.getInstance().getContractor().getName() + "!");
		
		
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
        JobsListing jobs = new JobsListing();
        JobsGetRequest jobsRequest;
		String cacheKey = null;
		PostingsListAdapter postingsViewAdapter;

        @Override
		public void onStart() {
			super.onStart();
			
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
            //postingsViewAdapter = new PostingsListAdapter(getActivity(),
			//		R.layout.main_row_layout, jobs);
            //setListAdapter(postingsViewAdapter);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, strings1));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
            //selectJob(position);
        }
        
		public void selectJob(int position) {
			Intent detailIntent = new Intent(getActivity(),
					ViewPostingActivity.class);
			detailIntent.putExtra("job", jobs.get(position));
			startActivity(detailIntent);
		}
		
		public final class JobsRequestListener implements RequestListener< JobsListing > {

	        @Override
	        public void onRequestFailure( SpiceException spiceException ) {
	            Toast.makeText( getActivity(), "failure", Toast.LENGTH_SHORT ).show();
	        }
	        
	        @Override																																																																																																																																																					 																
	        public void onRequestSuccess( final JobsListing result ) {
	            //Toast.makeText( mActivity, "success", Toast.LENGTH_SHORT ).show();
	            jobs = result;
				postingsViewAdapter.clear();
				postingsViewAdapter.addAll(jobs);
				postingsViewAdapter.notifyDataSetChanged();																																																																																																																																																					
	        }
	    }
    }

}
