package com.smalljobs.jobseeker.views;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
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
import com.smalljobs.jobseeker.BidPostRequest;
import com.smalljobs.jobseeker.DataHolder;
import com.smalljobs.jobseeker.PosterProfileRequest;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.JobPoster;
import com.smalljobs.jobseeker.models.JobPosting;

public class ViewPostingActivity extends Activity {

	private Context context=this;
	private PosterProfileRequest profileRequest;
	private BidPostRequest bidPostRequest;
	private JobPosting job;
	private JobPoster jobPoster;
	
	private TextView jobTitle;	
	private TextView jobDescription;	
	private TextView jobPosterName;	
	private TextView creationDate;	
	private TextView biddingDeadline;	
	private TextView bidConfDeadline;
	private TextView compensationAmount;	
	private TextView completionDate;
	
	private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceService.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_posting);
		
		job = (JobPosting) getIntent().getSerializableExtra("job");
		
		invalidateOptionsMenu();
		
		profileRequest = new PosterProfileRequest(job.getPosterID());
		
		jobTitle = (TextView) findViewById(R.id.jobTitle);
		jobDescription = (TextView) findViewById(R.id.jobDescription);
		jobPosterName = (TextView) findViewById(R.id.jobPoster);
		creationDate = (TextView) findViewById(R.id.creationDate);
		biddingDeadline = (TextView) findViewById(R.id.biddingDeadline);
		bidConfDeadline = (TextView) findViewById(R.id.bidConfDeadline);
		compensationAmount = (TextView) findViewById(R.id.compensationAmount);
		completionDate = (TextView) findViewById(R.id.completionDate);
		
		spiceManager.start(this);
		
        setProgressBarIndeterminate( true );
        setProgressBarVisibility( true );

        spiceManager.execute( profileRequest, "json", DurationInMillis.ONE_MINUTE, new ProfileRequestListener() );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_posting, menu);
		if(DataHolder.getInstance().getPotentialJobs().contains(job)){
			menu.findItem(R.id.action_bid).setEnabled(false);
		} else {
			System.out.println("Stupid");
			menu.findItem(R.id.action_bid).setEnabled(true);
		}
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
		if (id == R.id.action_bid) {
			DataHolder.getInstance().addPotentialJob(job);
			invalidateOptionsMenu();
			DialogFragment fm = new ConfirmBidDialogFragment();
			fm.show(getFragmentManager(), "tag");
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
        
        if (!spiceManager.isStarted()) {
        	spiceManager.start(this);
        }
    }
	
	public void displayJob() {
		
		SpannableString ss;
		
		jobTitle.setText(job.getTitle());
		
		jobPosterName.setText(jobPoster.getName());
		
		Date date = null;
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			date = df1.parse(job.getCreationDate());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ss =  new SpannableString("Posted: " + date);
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 7, 0);
		creationDate.setText(ss);
		
		
		((TextView) findViewById(R.id.descriptionTitle)).setVisibility(View.VISIBLE);
		jobDescription.setText(job.getDescription());
		
		date = null;
		try {
			date = df1.parse(job.getBiddingDeadline());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ss =  new SpannableString("Bidding Deadline: \n" + date);
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 17, 0);
		biddingDeadline.setText(ss);
		
		date = null;
		try {
			date = df1.parse(job.getBiddingConfirmationDeadline());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ss =  new SpannableString("Bid Confirmation By: \n" + date);
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 20, 0);
		bidConfDeadline.setText(ss);
		
		ss =  new SpannableString("Compensation Amount: Not specified");
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 20, 0);
		compensationAmount.setText(ss);
		
		ss =  new SpannableString("Completion Date: Not specified");
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 16, 0);
		completionDate.setText(ss);
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
    
    public final class BidPostRequestListener implements RequestListener< String > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
        	spiceException.printStackTrace();
            Toast.makeText( ViewPostingActivity.this, spiceException.getMessage(), Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final String result ) {
        	setProgressBarVisibility( false );
            Toast.makeText( ViewPostingActivity.this, "success", Toast.LENGTH_SHORT ).show();
            System.out.println(result);
        }
    }
    
    public class ConfirmBidDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        	LayoutInflater inflater = getActivity().getLayoutInflater();

        	// Inflate and set the layout for the dialog
        	// Pass null as the parent view because its going in the dialog layout
        	builder.setView(inflater.inflate(R.layout.dialog_bid, null))
        	.setMessage(R.string.please_confirm)
        	.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			bidPostRequest = new BidPostRequest(job.getId(), null, null);
        			spiceManager.execute( bidPostRequest, "json", DurationInMillis.ONE_MINUTE, new BidPostRequestListener() );
        		}
        	})
        	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			// User cancelled the dialog
        		}
        	});
        	// Create the AlertDialog object and return it
        	return builder.create();
        }
    }
}
