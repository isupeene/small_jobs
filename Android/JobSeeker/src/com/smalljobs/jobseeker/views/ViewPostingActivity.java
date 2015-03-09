package com.smalljobs.jobseeker.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.JobPosting;

public class ViewPostingActivity extends Activity {

	private Context context=this;
	
	private JobPosting job;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_posting);
		
		job = (JobPosting) getIntent().getSerializableExtra("job");
		
		displayJob();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_posting, menu);
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
			NavUtils.navigateUpTo(this, new Intent(this,
					BrowseActivity.class));
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(0, 0);
	}
	
	public void displayJob() {
		((TextView) findViewById(R.id.jobTitle))
		.setText(job.getTitle());
		TextView des =(TextView) findViewById(R.id.jobDescription);
		des.setText(job.getDescription());
	}
	
	public void displayPoster(View v) {
		
		Intent intent = new Intent(this,
				PosterProfileActivity.class);
		startActivity(intent);
	}
	
	
}
