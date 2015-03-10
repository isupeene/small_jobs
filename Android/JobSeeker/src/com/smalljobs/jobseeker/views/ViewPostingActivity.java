package com.smalljobs.jobseeker.views;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.PosterProfileRequest;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.JobPoster;
import com.smalljobs.jobseeker.models.JobPosting;

public class ViewPostingActivity extends Activity {

	private Context context=this;
	private PosterProfileRequest profileRequest;
	private JobPosting job;
	private JobPoster jobPoster;
	
	private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceService.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_posting);
		
		job = (JobPosting) getIntent().getSerializableExtra("job");
		
		profileRequest = new PosterProfileRequest(job.getPosterID());
		
		
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
	
    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
	
	@Override
    protected void onStart() {
        super.onStart();

        spiceManager.start(this);
        
        setProgressBarIndeterminate( true );
        setProgressBarVisibility( true );

        spiceManager.execute( profileRequest, "json", DurationInMillis.ONE_MINUTE, new ProfileRequestListener() );
    }
	
	public void displayJob() {
		((TextView) findViewById(R.id.jobTitle)).setText(job.getTitle());
		((TextView) findViewById(R.id.jobPoster)).setText(jobPoster.getName());
		Date creationDate = null;
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		try {
			creationDate = df1.parse(job.getCreationDate());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((TextView) findViewById(R.id.jobDescription)).setText(job.getDescription());
	}
	
	public void displayPoster(View v) {
		Intent intent = new Intent(this,
				PosterProfileActivity.class);
		intent.putExtra("poster", jobPoster);
		startActivity(intent);
	}
	
	// ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class ProfileRequestListener implements RequestListener< JobPoster > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
            Toast.makeText( ViewPostingActivity.this, "failure", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final JobPoster result ) {
        	setProgressBarVisibility( false );
            Toast.makeText( ViewPostingActivity.this, "success", Toast.LENGTH_SHORT ).show();
            jobPoster = result;
            displayJob();
        }
    }
}
