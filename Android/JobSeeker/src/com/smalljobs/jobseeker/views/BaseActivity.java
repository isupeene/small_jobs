package com.smalljobs.jobseeker.views;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.*;

import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.LogoutRequest;
import com.smalljobs.jobseeker.R;

public class BaseActivity extends Activity {
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] navOptions = new String[] {"Home", 
        "Browse", "My Jobs", "My Profile", "Settings", "Logout"};

    LogoutRequest logoutRequest;
    
    private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceService.class);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    
    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    public void setContentView(final int layoutResID) {
        DrawerLayout fullLayout= (DrawerLayout) getLayoutInflater()
                .inflate(R.layout.activity_base, null);
        RelativeLayout actContent= (RelativeLayout) fullLayout.findViewById(R.id.content);

        mDrawerLayout = (DrawerLayout) fullLayout.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) fullLayout.findViewById(R.id.nav_drawer);

        mDrawerList
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectItem(position);
			}
		});
        
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navOptions));

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.hello_world,
                R.string.app_name
        ) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void selectItem(int position) {
    	Intent intent;
    	switch (position) {
		case 0:
			//Home
			intent = new Intent(this, MainActivity.class);
			mDrawerLayout.closeDrawers();
			startActivity(intent);
			overridePendingTransition(0, 0);
			if (this.getClass() != MainActivity.class) {
				finish();
			}
			break;
		case 1:
			//Browse
			intent = new Intent(this, BrowseActivity.class);
			mDrawerLayout.closeDrawers();
			startActivity(intent);
			overridePendingTransition(0, 0);
			if (this.getClass() != BrowseActivity.class) {
				finish();
			}
			break;			
		case 2:
			//My Jobs
			intent = new Intent(this, MyJobsActivity.class);
			mDrawerLayout.closeDrawers();
			startActivity(intent);
			overridePendingTransition(0, 0);
			if (this.getClass() != MyJobsActivity.class) {
				finish();
			}
			break;	
		case 3:
			//My Profile
			intent = new Intent(this, MyProfileActivity.class);
			mDrawerLayout.closeDrawers();
			startActivity(intent);
			overridePendingTransition(0, 0);
			if (this.getClass() != MyProfileActivity.class) {
				finish();
			}
			break;	
		case 4:
			//Settings
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case 5:
			//Logout
			logoutRequest = new LogoutRequest();
			spiceManager.execute(logoutRequest, new LogoutRequestListener());
			break;
		}
	}
    
    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }
    
	public final class LogoutRequestListener implements RequestListener< String > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
        }

        @Override
        public void onRequestSuccess( final String result ) {
            Toast.makeText( BaseActivity.this, result, Toast.LENGTH_SHORT ).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
			startActivity(intent);
			overridePendingTransition(0, 0);
			finish();
        }
    }

}
