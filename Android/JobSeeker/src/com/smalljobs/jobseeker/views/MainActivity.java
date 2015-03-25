package com.smalljobs.jobseeker.views;

import java.io.IOException;
import java.net.CookieManager;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.DataHolder;
import com.smalljobs.jobseeker.JobsGetRequest;
import com.smalljobs.jobseeker.PostingsListAdapter;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.UserProfileRequest;
import com.smalljobs.jobseeker.models.Contractor;
import com.smalljobs.jobseeker.models.CookieManagerSingleton;
import com.smalljobs.jobseeker.models.JobsListing;
import com.smalljobs.jobseeker.models.User;
import com.smalljobs.jobseeker.views.MyJobsActivity.PlaceholderFragment.JobsRequestListener;

public class MainActivity extends BaseActivity {
	
	private TextView mLoremTextView;
	private int backButtonCount;
	
	
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
	public void onBackPressed() {
		if(backButtonCount >= 1)
	    {
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_HOME);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);
	    }
	    else
	    {
	        Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
	        backButtonCount++;
	    }
	}
	
	
	
	@Override
	protected void onStart() {
		super.onStart();
		if (DataHolder.getInstance().getPotentialJobs() == null) {
			JobsGetRequest jobsRequest = new JobsGetRequest(this, "prospective_jobs");
			getSpiceManager().execute( jobsRequest, "pros", DurationInMillis.ALWAYS_EXPIRED, new JobsRequestListener() );
		}
	}
	
    public final class JobsRequestListener implements RequestListener< JobsListing > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
            Toast.makeText( MainActivity.this, "failure", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final JobsListing result ) {
        	setProgressBarVisibility( false );
            //Toast.makeText( MainActivity.this, "success", Toast.LENGTH_SHORT ).show();
            DataHolder.getInstance().setPotentialJobs(result);
        }
    }

}
