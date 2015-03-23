package com.smalljobs.jobseeker.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
		overridePendingTransition(android.R.anim.fade_in, 0);
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
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		setProgressBarIndeterminate( true );
        setProgressBarVisibility( true );

        getSpiceManager().execute( jobsRequest, "json", DurationInMillis.ONE_MINUTE, new JobsRequestListener() );
		
	}
	
	// ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class JobsRequestListener implements RequestListener< JobsListing > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
            Toast.makeText( BrowseActivity.this, "failure", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final JobsListing result ) {
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
