package com.smalljobs.jobseeker.views;

import java.net.ConnectException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpStatus;

import roboguice.util.temp.Ln;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.HttpResponseException;
import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.BidPostRequest;
import com.smalljobs.jobseeker.MarkCompleteRequest;
import com.smalljobs.jobseeker.PosterProfileRequest;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.RatingPostRequest;
import com.smalljobs.jobseeker.models.JobPoster;
import com.smalljobs.jobseeker.models.JobPosting;
import com.smalljobs.jobseeker.models.User;


/**
 * A screen that displays a job posting.
 * 
 * Requirements specifications reference:
 * 3.2.2.3.1 Permit the users to bid on available jobs.
 * 3.2.2.3.1.1 Bids shall include a Compensation Amount and a Completion Date, if required by the Job Posting
 * 3.2.2.4.1 Allow users to provide a rating for Job Posters who have accepted a bid from them.
 * 3.2.2.4.2 Allow users to mark a job as complete.
 * 
 */

public class ViewPostingActivity extends Activity {

	public static final String PREFS_RATINGS = "ratings";
	public static final String PREFS_BIDS = "bids";
	
	private Context context = this;
	
	private PosterProfileRequest profileRequest;
	private BidPostRequest bidPostRequest;
	private MarkCompleteRequest markCompleteRequest;
	private RatingPostRequest ratingPostRequest;
	
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
	private TextView location;
	
	private SharedPreferences ratings;
	private SharedPreferences bids;
	
	private boolean fromNotifications;
	
	private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceService.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature( Window.FEATURE_PROGRESS );
		setContentView(R.layout.activity_view_posting);
		
		job = (JobPosting) getIntent().getSerializableExtra("job");
		
		fromNotifications = getIntent().getBooleanExtra("notification", false);
		
		invalidateOptionsMenu();
		
		jobTitle = (TextView) findViewById(R.id.jobTitle);
		jobDescription = (TextView) findViewById(R.id.jobDescription);
		jobPosterName = (TextView) findViewById(R.id.jobPoster);
		location = (TextView) findViewById(R.id.location);
		creationDate = (TextView) findViewById(R.id.creationDate);
		biddingDeadline = (TextView) findViewById(R.id.biddingDeadline);
		bidConfDeadline = (TextView) findViewById(R.id.bidConfDeadline);
		compensationAmount = (TextView) findViewById(R.id.compensationAmount);
		completionDate = (TextView) findViewById(R.id.completionDate);
		
		spiceManager.start(this);
		
        setProgressBarIndeterminate( true );
        setProgressBarVisibility( true );
		
		profileRequest = new PosterProfileRequest(job.getPosterID());
        spiceManager.execute( profileRequest, new ProfileRequestListener() );
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if (fromNotifications) {
			getMenuInflater().inflate(R.menu.view_posting_notification, menu);
			return true;
		}
		if (job.getContractor() == null) {
			getMenuInflater().inflate(R.menu.view_posting, menu);
			menu.findItem(R.id.action_bid).setEnabled(true);
			bids = context.getSharedPreferences(User.getInstance().getContractor().getName() + PREFS_BIDS, 0);
			if (bids.contains(job.getId())) {
				menu.findItem(R.id.action_bid).setEnabled(false);
			}
		} else if (!job.getMarkedAsComplete() && job.getContractor().equals(User.getInstance().getContractor().getId())) {
			getMenuInflater().inflate(R.menu.view_posting_in_progress, menu);
		} else {
			getMenuInflater().inflate(R.menu.view_posting_completed, menu);
			ratings = context.getSharedPreferences(User.getInstance().getContractor().getName() + PREFS_RATINGS, 0);
			if (ratings.contains(job.getPosterID())) {
				menu.findItem(R.id.action_rate).setEnabled(false);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_bid) {
			promptBidInfo();
			return true;
		}
		if (id == R.id.action_rate) {
			promptRatingInfo();
			return true;
		}
		if (id == R.id.action_mark_complete) {
			promptCompletionConfirmation();
			return true;
		}
		if (id == android.R.id.home) {
			NavUtils.navigateUpTo(this, new Intent(this,
					BrowseActivity.class));
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void promptBidInfo() {
		DialogFragment fm = new ConfirmBidDialogFragment();
		fm.show(getFragmentManager(), "tag");
	}
	
	private void promptRatingInfo() {
		DialogFragment fm = new RateDialogFragment();
		fm.show(getFragmentManager(), "tag");
	}
	
	private void promptCompletionConfirmation() {
		DialogFragment fm = new ConfirmCompletionDialogFragment();
		fm.show(getFragmentManager(), "tag");
	}
	
	private void displayJob() {
		
		SpannableString ss;
		
		jobTitle.setText(job.getTitle());
		
		jobPosterName.setText(jobPoster.getName());
		
		location.setText(jobPoster.getRegion());
		
		Date date = null;
		date = parseDate(job.getCreationDate());
		ss =  new SpannableString("Posted: " + date);
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 7, 0);
		creationDate.setText(ss);
		
		
		((TextView) findViewById(R.id.descriptionTitle)).setVisibility(View.VISIBLE);
		jobDescription.setText(job.getDescription());
		
		date = null;
		date = parseDate(job.getBiddingDeadline());
		ss =  new SpannableString("Bidding Deadline: \n" + date);
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 17, 0);
		biddingDeadline.setText(ss);
		
		date = null;
		date = parseDate(job.getBiddingConfirmationDeadline());
		ss =  new SpannableString("Bid Confirmation By: \n" + date);
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 20, 0);
		bidConfDeadline.setText(ss);
		
		if (job.getCompensationAmount() != null) {
			ss =  new SpannableString("Compensation Amount: $" + job.getCompensationAmount());			
		} else {
			ss =  new SpannableString("Compensation Amount: Not specified");			
		}
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 20, 0);
		compensationAmount.setText(ss);
		
		if (job.getCompletionDate() != null) {
			date = parseDate(job.getCompletionDate());
			ss =  new SpannableString("Completion Date: " + date);
		} else {
			ss =  new SpannableString("Completion Date: Not specified");
		}
		ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, 16, 0);
		completionDate.setText(ss);
	}
	
	private Date parseDate(String dateString) {
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA);
		
		Date date = null;
		
		try {
			date = df1.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return Calendar.getInstance().getTime();
		}
		
		return date;
	}
	
    private void markAsComplete() {
		markCompleteRequest = new MarkCompleteRequest(context, job.getId());
		spiceManager.execute(markCompleteRequest, new MarkCompleteRequestListener());
    }
    
    private void ratePoster(int rating) {
		ratingPostRequest = new RatingPostRequest(context, job.getPosterID(), job.getContractor(), rating);
		spiceManager.execute(ratingPostRequest, new RatingRequestListener());
    }
    
    private void bidOnJob(String date, String amount, String message) {
		bidPostRequest = new BidPostRequest(job.getId(), amount, date, message);
		spiceManager.execute(bidPostRequest, "bid", DurationInMillis.ALWAYS_EXPIRED, new BidPostRequestListener());    	
    }
    
	public void displayPoster(View v) {
		Intent intent = new Intent(this,
				PosterProfileActivity.class);
		intent.putExtra("poster", jobPoster);
		startActivity(intent);
	}
	
    
	// ============================================================================================
    // Request Listeners
    // ============================================================================================

    public final class ProfileRequestListener implements RequestListener< JobPoster > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
            Toast.makeText( ViewPostingActivity.this, "failure", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final JobPoster result ) {
        	setProgressBarVisibility( false );
            //Toast.makeText( ViewPostingActivity.this, "success", Toast.LENGTH_SHORT ).show();
            jobPoster = result;
            displayJob();
        }
    }
    
    public final class BidPostRequestListener implements RequestListener< String > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
        	setProgressBarVisibility( false );
        	if(spiceException.getCause() instanceof ConnectException)
            {
        		Toast.makeText( ViewPostingActivity.this, "Sorry, could not connect to the server.", Toast.LENGTH_SHORT ).show();
            }
            else if(spiceException.getCause() instanceof HttpResponseException)
            {
            	HttpResponseException exception = (HttpResponseException)spiceException.getCause();
            	if (exception.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            	}
            	if (exception.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            		Toast.makeText( ViewPostingActivity.this, "You have already bid on this job.", Toast.LENGTH_SHORT ).show();
            	}
            }
            else
            {
                Ln.d("Other exception");
            }
        }

        @Override
        public void onRequestSuccess( final String result ) {
        	setProgressBarVisibility( false );
            Toast.makeText( ViewPostingActivity.this, "Bid successful", Toast.LENGTH_SHORT ).show();
        	SharedPreferences.Editor editor = bids.edit();
        	editor.putInt(job.getId(), 1);
        	editor.commit();
			invalidateOptionsMenu();
        }
    }
    
    public final class MarkCompleteRequestListener implements RequestListener< String > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
        	spiceException.printStackTrace();
        	if(spiceException.getCause() instanceof ConnectException)
            {
        		Toast.makeText( ViewPostingActivity.this, "Sorry, could not connect to the server.", Toast.LENGTH_SHORT ).show();
            }
            else if(spiceException.getCause() instanceof HttpResponseException)
            {
            	HttpResponseException exception = (HttpResponseException)spiceException.getCause();
            	if (exception.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            	}
            	if (exception.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            		Toast.makeText( ViewPostingActivity.this, "This job has already been marked as complete.", Toast.LENGTH_SHORT ).show();
            	}
            }
            else
            {
                Ln.d("Other exception");
            }
        }

        @Override
        public void onRequestSuccess( final String result ) {
        	setProgressBarVisibility( false );
            Toast.makeText( ViewPostingActivity.this, "Job Marked as Complete", Toast.LENGTH_SHORT ).show();
        	job.markAsComplete(true);
			invalidateOptionsMenu();
        }
    }
    
    public final class RatingRequestListener implements RequestListener< String > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
        	setProgressBarVisibility( false );
        	if(spiceException.getCause() instanceof ConnectException)
            {
        		Toast.makeText( ViewPostingActivity.this, "Sorry, could not connect to the server.", Toast.LENGTH_SHORT ).show();
            }
            else if(spiceException.getCause() instanceof HttpResponseException)
            {
            	HttpResponseException exception = (HttpResponseException)spiceException.getCause();
            	if (exception.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            	}
            	if (exception.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            		Toast.makeText( ViewPostingActivity.this, "You have already rated this poster.", Toast.LENGTH_SHORT ).show();
            	}
            }
            else
            {
                Ln.d("Other exception");
            }
            SharedPreferences.Editor editor = ratings.edit();
            editor.remove(job.getPosterID());
        	editor.commit();
        }

        @Override
        public void onRequestSuccess( final String result ) {
        	setProgressBarVisibility( false );
            Toast.makeText( ViewPostingActivity.this, "Poster rated", Toast.LENGTH_SHORT ).show();
			invalidateOptionsMenu();
        }
    }
       
	// ============================================================================================
    // Dialog Fragments
    // ============================================================================================
    
    public class ConfirmBidDialogFragment extends DialogFragment {
    	
		EditText moneySpecifier;
    	DatePicker dateSpecifier;
    	EditText message;
    	
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        	LayoutInflater inflater = getActivity().getLayoutInflater();

        	View dialogView = inflater.inflate(R.layout.dialog_bid, null);
        	// Inflate and set the layout for the dialog
        	// Pass null as the parent view because its going in the dialog layout
        	builder.setView(dialogView);
        	
        	moneySpecifier = (EditText) dialogView.findViewById(R.id.specifyCompensationAmount);
        	dateSpecifier = (DatePicker) dialogView.findViewById(R.id.datePicker);
        	message = (EditText) dialogView.findViewById(R.id.message);
        	
        	
        	if (job.getBidIncludesCompensationAmount()) {
        		moneySpecifier.setVisibility(View.VISIBLE);
        		((TextView) dialogView.findViewById(R.id.promptAmount)).setVisibility(View.VISIBLE);
        	} 
        	
        	if (job.getBidIncludesCompletionDate()) {
        		dateSpecifier.setVisibility(View.VISIBLE);
        		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA);
        		
        		try {
					dateSpecifier.setMinDate(df1.parse(job.getBiddingConfirmationDeadline()).getTime());
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
        		if (job.getCompletionDate() != null) {
        			try {
        				Calendar calendar = Calendar.getInstance();
        				calendar.setTimeInMillis(df1.parse(job.getCompletionDate()).getTime());
        				int year = calendar.get(Calendar.YEAR);
        				int month = calendar.get(Calendar.MONTH);
        				int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    					dateSpecifier.updateDate(year, month, dayOfMonth);
    				} catch (ParseException e) {
    					e.printStackTrace();
    				}
        		}
        		
        		
        		((TextView) dialogView.findViewById(R.id.promptDate)).setVisibility(View.VISIBLE);
        	}
        	if ((moneySpecifier.getVisibility() == View.GONE) && (dateSpecifier.getVisibility() == View.GONE)) {
            	builder.setMessage(R.string.please_confirm);        		
        	}
        	
        	
        	builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			// Behavior defined in the onStart method
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
        
        @Override
        public void onStart() {
        	super.onStart();
        	AlertDialog dialog = (AlertDialog) getDialog();
        	
        	if(dialog != null)
        	{
        		Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
        		positiveButton.setOnClickListener(new View.OnClickListener()
        		{
        			@Override
        			public void onClick(View v)
        			{
        				Boolean wantToCloseDialog = true;
        				
        				String compensationAmount = null;
        				String completionDate = null;
        				String messageText = null;
        				
        				if (dateSpecifier.getVisibility() == View.VISIBLE) {
        					Calendar calendar = Calendar.getInstance();
        					calendar.set(Calendar.YEAR, dateSpecifier.getYear());
        					calendar.set(Calendar.MONTH, dateSpecifier.getMonth());
        					calendar.set(Calendar.DAY_OF_MONTH, dateSpecifier.getDayOfMonth());

        					Date pickedDate = calendar.getTime();
        					
        					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        				    completionDate = df.format(pickedDate);
        				}
        				
        				if (moneySpecifier.getVisibility() == View.VISIBLE) {
        					compensationAmount = moneySpecifier.getText().toString();
        					if (compensationAmount.isEmpty()) {
        						moneySpecifier.setError(getString(R.string.error_field_required));
        						wantToCloseDialog = false;
        					}
        				}
        				messageText = message.getText().toString();
        				
        				if(wantToCloseDialog) {
        					setProgressBarIndeterminate( true );
        			        setProgressBarVisibility( true );
        			        
        			        bidOnJob(completionDate, compensationAmount, messageText);
        					dismiss();
        				}
        			}
        		});
        	}
        }
    }

    public class ConfirmCompletionDialogFragment extends DialogFragment {


    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		// Use the Builder class for convenient dialog construction
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    		builder.setMessage(R.string.please_confirm)

    		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				setProgressBarIndeterminate( true );
    		        setProgressBarVisibility( true );
    		        markAsComplete();
    			}
    		})
    		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				// User cancelled the dialog
    			}
    		});
    		// Create the AlertDialog object and return it
    		return builder.create();
    	}
    }
    
    public class RateDialogFragment extends DialogFragment {
    	
		RatingBar ratingBar;
    	
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        	LayoutInflater inflater = getActivity().getLayoutInflater();

        	// Inflate and set the layout for the dialog
        	// Pass null as the parent view because its going in the dialog layout
        	View dialogView = inflater.inflate(R.layout.dialog_rate, null);
        	builder.setView(dialogView);
        	
        	ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        	
        	
        	builder.setPositiveButton(R.string.rate, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			int rating = (int) ratingBar.getRating();
        			SharedPreferences.Editor editor = ratings.edit();
                	editor.putLong(job.getPosterID(), rating);
                	editor.commit();
                	ratePoster(rating);
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
