package com.smalljobs.jobseeker.views;

import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.JobsGetRequest;
import com.smalljobs.jobseeker.PostingsListAdapter;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.JobsListing;
import com.smalljobs.jobseeker.models.User;

/**
 * A screen that displays jobs that a user bid on
 * 
 * Requirements Specifications Reference:
 * 
 * 3.2.2.3.3 Allow users to view all the jobs that they have successfully bid on,
 *  filterable by whether or not they have been completed.
 */

public class MyJobsActivity extends BaseActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_jobs);


		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mSectionsPagerAdapter.notifyDataSetChanged();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_jobs, menu);
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
	

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {			
			return JobsListFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section3).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section1).toUpperCase(l);
			}
			return null;
		}
		
	}
	
	public static class JobsListFragment extends ListFragment {
        int section_number;
        String[] strings = {"Cheese", "Pepperoni", "Black Olives"};
        JobsListing jobs = new JobsListing();
        JobsGetRequest jobsRequest;
		String cacheKey = null;
		PostingsListAdapter postingsViewAdapter;

        static JobsListFragment newInstance(int num) {
            JobsListFragment fragment = new JobsListFragment();

            Bundle args = new Bundle();
			args.putInt("section_number", num+1);
			fragment.setArguments(args);

            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            section_number = getArguments() != null ? getArguments().getInt("section_number") : 1;
        }

        @Override
		public void onStart() {
			super.onStart();
			

			System.out.println("onStart " + getArguments().getInt("section_number"));
			
			switch (section_number) {
			case 3:
				jobsRequest = new JobsGetRequest(getActivity(), "completed_jobs");
				cacheKey = "comp";
				break;
			case 2:
				jobsRequest = new JobsGetRequest(getActivity(), "current_jobs");
				cacheKey = "curr";
				break;
			case 1:
				jobsRequest = new JobsGetRequest(getActivity(), "prospective_jobs");
				cacheKey = "pros";
				//jobs = DataHolder.getInstance().getPotentialJobs();
				break;
			}
			
			((BaseActivity) getActivity()).getSpiceManager().execute( jobsRequest, User.getInstance().getContractor().getId()+cacheKey, DurationInMillis.ALWAYS_EXPIRED, new JobsRequestListener() );
		}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	System.out.println("onCreateView " + getArguments().getInt("section_number"));
        	
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            return v;
        }

		@Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            postingsViewAdapter = new PostingsListAdapter(getActivity(),
					R.layout.main_row_layout, jobs);
            setListAdapter(postingsViewAdapter);
            //setListAdapter(new ArrayAdapter<String>(getActivity(),
            //        android.R.layout.simple_list_item_1, strings));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
            selectJob(position);
        }
        
		public void selectJob(int position) {
			Intent detailIntent = new Intent(getActivity(),
					ViewPostingActivity.class);
			detailIntent.putExtra("job", jobs.get(position));
			startActivity(detailIntent);
			getActivity().overridePendingTransition(0, 0);
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
