package com.smalljobs.jobseeker.views;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.RatingGetRequest;
import com.smalljobs.jobseeker.models.JobPoster;

/**
 * A screen that displays a job poster's profile.
 * 
 * Requirements specifications reference:
 * 3.2.2.2.3 Allow the user to view the profile of the job poster including their rating
 * 
 */

public class PosterProfileActivity extends Activity {

	private JobPoster jobPoster;
	private RatingGetRequest getRatingRequest;
	private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceService.class);
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poster_profile);
		
		jobPoster = (JobPoster) getIntent().getSerializableExtra("poster");
		
		displayProfile();
		
		getRatingRequest = new RatingGetRequest(jobPoster.getId());
		
		spiceManager.start(this);
		
		spiceManager.execute(getRatingRequest, new RatingRequestListener());
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
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();
    }
	
    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
	
	@Override
    protected void onStart() {
        super.onStart(); 
        
        if (!spiceManager.isStarted()) {
        	spiceManager.start(this);
        }
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
	
    public final class RatingRequestListener implements RequestListener< String > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
        	setProgressBarVisibility( false );
        	spiceException.printStackTrace();
            Toast.makeText( PosterProfileActivity.this, "Could not fetch the rating.", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final String result ) {
        	setProgressBarVisibility( false );
            //Toast.makeText( ViewPostingActivity.this, "success", Toast.LENGTH_SHORT ).show();
        	System.out.println(result);
        	if (!result.isEmpty()) {
        		((RatingBar) findViewById(R.id.posterRatingBar)).setRating(Float.parseFloat(result));
        	} else {
        		((TextView) findViewById(R.id.noRating)).setVisibility(View.VISIBLE);
        	}
        }
    }
}
