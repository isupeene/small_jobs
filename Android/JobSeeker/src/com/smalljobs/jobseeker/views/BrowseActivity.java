package com.smalljobs.jobseeker.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.JobsGetRequest;
import com.smalljobs.jobseeker.PostingsListAdapter;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.JobsListing;

public class BrowseActivity extends BaseActivity {

	private Context context=this;
	private ListView postingsList;
	private JobsGetRequest jobsRequest;
	private JobsListing jobs=new JobsListing();
	private PostingsListAdapter postingsViewAdapter;
	private DialogFragment fm;
	private String location;
	private String skills;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_PROGRESS );
		
		setContentView(R.layout.activity_browse);
		
		postingsList = (ListView) findViewById(R.id.MainListView);
		
		postingsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectJob(position);
			}
		});
		
		jobsRequest = new JobsGetRequest(context, "jobs");
	}

	public void selectJob(int position) {
		Intent detailIntent = new Intent(this,
				ViewPostingActivity.class);
		detailIntent.putExtra("job", jobs.get(position));
		startActivity(detailIntent);
		overridePendingTransition(0, 0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browse, menu);
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
		if (id == R.id.action_filter) {
			fm = new FilterDialogFragment();
			fm.show(getFragmentManager(), "tag");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		setProgressBarIndeterminate( true );
        setProgressBarVisibility( true );

        getSpiceManager().execute( jobsRequest, "json", DurationInMillis.ALWAYS_EXPIRED, new JobsRequestListener() );
		
	}

	
	// ============================================================================================
    // INNER CLASSES
    // ============================================================================================
	
	public class FilterDialogFragment extends DialogFragment {

		EditText locationSpecifier;
		EditText skillsSpecifier;

		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View dialogView = inflater.inflate(R.layout.dialog_filter, null);
			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog layout
			builder.setView(dialogView);

			locationSpecifier = (EditText) dialogView.findViewById(R.id.prompt_location);
			skillsSpecifier = (EditText) dialogView.findViewById(R.id.prompt_skills);
			
			locationSpecifier.setText(location);
			skillsSpecifier.setText(skills);

			builder.setTitle(R.string.filter_title);
			
			builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {


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
			AlertDialog d = (AlertDialog) getDialog();

			if(d != null)
			{
				Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						location = locationSpecifier.getText().toString();
						skills = skillsSpecifier.getText().toString();
						
						setProgressBarIndeterminate( true );
				        setProgressBarVisibility( true );
						
						jobsRequest = new JobsGetRequest(context, "jobs", location, skills);
						getSpiceManager().execute( jobsRequest, "json", DurationInMillis.ALWAYS_EXPIRED, new JobsRequestListener() );
					}
				});
			}
		}
	}

    public final class JobsRequestListener implements RequestListener< JobsListing > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
            Toast.makeText( BrowseActivity.this, "failure", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final JobsListing result ) {
        	if (fm != null) {
        		fm.dismiss();
        	}
        	setProgressBarVisibility( false );
            //Toast.makeText( BrowseActivity.this, "success", Toast.LENGTH_SHORT ).show();
            postingsViewAdapter = new PostingsListAdapter(context,
					R.layout.main_row_layout, result);
            jobs = result;
			postingsList.setAdapter(postingsViewAdapter);
			postingsViewAdapter.notifyDataSetChanged();
        }
    }
	
}
