package com.smalljobs.jobseeker.views;

import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.R.id;
import com.smalljobs.jobseeker.R.layout;
import com.smalljobs.jobseeker.R.menu;
import com.smalljobs.jobseeker.models.JobPoster;
import com.smalljobs.jobseeker.models.JobPosting;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class PosterProfileActivity extends Activity {


	private JobPoster jobPoster;
	private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceService.class);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poster_profile);
		
		jobPoster = (JobPoster) getIntent().getSerializableExtra("poster");
		
		displayProfile();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.poster_profile, menu);
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
	
	public void displayProfile() {
		((TextView) findViewById(R.id.jobPoster)).setText(jobPoster.getName());
		((TextView) findViewById(R.id.posterDescription)).setText(jobPoster.getDescription());
		((TextView) findViewById(R.id.phoneNumber)).setText(jobPoster.getPhoneNumber());
	}
}
