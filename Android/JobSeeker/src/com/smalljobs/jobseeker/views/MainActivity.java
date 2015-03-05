package com.smalljobs.jobseeker.views;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.PosterProfileRequest;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.JobPoster;

public class MainActivity extends BaseActivity {

	private PosterProfileRequest profileRequest;
	
	private TextView mLoremTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLoremTextView = (TextView) findViewById( R.id.name );
		
		profileRequest = new PosterProfileRequest();
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
    protected void onStart() {
        super.onStart();

        setProgressBarIndeterminate( false );
        setProgressBarVisibility( true );

        getSpiceManager().execute( profileRequest, "json", DurationInMillis.ONE_MINUTE, new ProfileRequestListener() );
    }

	// ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class ProfileRequestListener implements RequestListener< JobPoster > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
            Toast.makeText( MainActivity.this, "failure", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final JobPoster result ) {
            Toast.makeText( MainActivity.this, "success", Toast.LENGTH_SHORT ).show();
            String originalText = getString( R.string.hello_world );
            mLoremTextView.setText( originalText + " " + result );
        }
    }
}
