package com.smalljobs.jobseeker.views;

import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.DataHolder;
import com.smalljobs.jobseeker.JobsGetRequest;
import com.smalljobs.jobseeker.PostingsListAdapter;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.JobsListing;
import com.smalljobs.jobseeker.models.User;

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
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
            Fragment fragment = new PlaceholderFragment(position+1);
 
            Bundle args = new Bundle();
			args.putInt("section_number", position+1);
			fragment.setArguments(args);
			
			return fragment;
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
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		private int sectionNumber;
		
		private JobsListing jobs = new JobsListing();

		private PostingsListAdapter postingsViewAdapter;

		private JobsGetRequest jobsRequest;
		
		private Activity mActivity;
		
		private ListView list;

		private String cacheKey = null;
		
		public PlaceholderFragment(int i) {
			sectionNumber = i;
		}

	    @Override
	    public void onAttach(Activity activity)
	    {
	        if (activity instanceof MyJobsActivity)
	        {
	            mActivity = (MyJobsActivity) activity;
	        }
	        super.onAttach(activity);
	    }
	    
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
						
			switch (sectionNumber) {
			case 1:
				jobsRequest = new JobsGetRequest(mActivity, "completed_jobs");
				cacheKey = "comp";
				break;
			case 2:
				jobsRequest = new JobsGetRequest(mActivity, "current_jobs");
				cacheKey = "curr";
				break;
			case 3:
				jobsRequest = new JobsGetRequest(mActivity, "prospective_jobs");
				cacheKey = "pros";
				//jobs = DataHolder.getInstance().getPotentialJobs();
				break;
			}
			
			((BaseActivity) getActivity()).getSpiceManager().execute( jobsRequest, User.getInstance().getContractor().getId()+cacheKey, DurationInMillis.ONE_MINUTE, new JobsRequestListener() );
		}

		@Override
		public void onStart() {
			super.onStart();
			
			
			System.out.println("on start" + sectionNumber);
			

		}
		
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_my_jobs,
					container, false);
			
			System.out.println("on create view" + sectionNumber);
			
			list = (ListView) rootView.findViewById(R.id.MyJobsListView);
			
			postingsViewAdapter = new PostingsListAdapter(mActivity,
					R.layout.main_row_layout, jobs);
			

			list.setAdapter(postingsViewAdapter);
			
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					selectJob(position);
				}
			});
			
			return rootView;
		}

		public void selectJob(int position) {
			Intent detailIntent = new Intent(mActivity,
					ViewPostingActivity.class);
			detailIntent.putExtra("job", jobs.get(position));
			startActivity(detailIntent);
		}
		
	    public final class JobsRequestListener implements RequestListener< JobsListing > {

	        @Override
	        public void onRequestFailure( SpiceException spiceException ) {
	            Toast.makeText( mActivity, "failure", Toast.LENGTH_SHORT ).show();
	        }
	        

	        @Override
	        public void onRequestSuccess( final JobsListing result ) {
	        	mActivity.setProgressBarVisibility( false );
	            //Toast.makeText( mActivity, "success", Toast.LENGTH_SHORT ).show();
	            jobs = result;
				System.out.println("request success" + getArguments().getInt(ARG_SECTION_NUMBER));
				postingsViewAdapter.notifyDataSetChanged();
				postingsViewAdapter.addAll(jobs);
	        }
	    }
	}

}
