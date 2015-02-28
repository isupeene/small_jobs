package com.smalljobs.jobseeker.views;

import java.io.IOException;

import com.smalljobs.jobseeker.PostingsList;
import com.smalljobs.jobseeker.PostingsListAdapter;
import com.smalljobs.jobseeker.PostingsListController;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.R.id;
import com.smalljobs.jobseeker.R.layout;
import com.smalljobs.jobseeker.R.menu;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class BrowseActivity extends Activity {

	private Context context=this;
	private ListView postingsList;
	private PostingsList jobs=new PostingsList();
	private PostingsListController plc;
	private PostingsListAdapter postingsViewAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);
		
		postingsList = (ListView) findViewById(R.id.MainListView);
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

		plc = new PostingsListController(jobs);
		AsyncGet getTask=new AsyncGet();
		getTask.execute(new PostingsListController[] {plc});
		
		//System.out.println(jobs.size());
		
	}
	
	private class AsyncGet extends AsyncTask<PostingsListController, Void, Void> {

		@Override
		protected Void doInBackground(PostingsListController... params) {
			for (PostingsListController plc:params) {
				try {
					plc.refreshPostings();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			System.out.println(jobs.getJobs().size());
			postingsViewAdapter = new PostingsListAdapter(context,
					R.layout.main_row_layout, jobs.getJobs());
			postingsList.setAdapter(postingsViewAdapter);
			postingsViewAdapter.notifyDataSetChanged();
		}
		
	}
}
