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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();
    }
	
	public void displayProfile() {
		
		SpannableString ss;
		
		((TextView) findViewById(R.id.jobPoster)).setText(jobPoster.getName());
		((TextView) findViewById(R.id.posterDescription)).setText(jobPoster.getDescription());
		
		ss =  new SpannableString("Call " + jobPoster.getPhoneNumber());
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 4, 0);
		((TextView) findViewById(R.id.phoneNumber)).setText(ss);
		
		ss =  new SpannableString("Email " + jobPoster.getEmail());
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 5, 0);
		((TextView) findViewById(R.id.email)).setText(ss);
		
		((TextView) findViewById(R.id.descriptionTitle)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.contactTitle)).setVisibility(View.VISIBLE);
	}
}
