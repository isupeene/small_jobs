package com.smalljobs.jobseeker.views;

import java.net.ConnectException;

import org.apache.http.HttpStatus;

import roboguice.util.temp.Ln;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.api.client.http.HttpResponseException;
import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.LoginRequest;
import com.smalljobs.jobseeker.R;

/**
 * A login screen that offers login via email.
 * 
 * Requirements Specifications Reference:
 * 3.2.2.1.2 Allow the user to login with any account they have created
 * 3.2.2.1.3 Permit the user to remain logged in indefinitely (i.e. across multiple sessions).
 *
 */

public class LoginActivity extends Activity {
	
	Context context = this;
	
	public static final String PREFS_NAME = "Credentials";
	
	private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceService.class);

	// UI references.
	private AutoCompleteTextView mEmailView;
	private View mProgressView;
	private View mLoginFormView;

	private LoginRequest loginRequest = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);

		SharedPreferences credentials = context.getSharedPreferences(PREFS_NAME, 0);
		String email = credentials.getString("email", null);
		
		
        spiceManager.start(this);
        
		// Set up the login form.
		mEmailView = (AutoCompleteTextView) findViewById(R.id.loginEmail);


		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
		
		
		if (email != null) {
			
			showProgress(true);
			loginRequest = new LoginRequest(context, email);
			spiceManager.execute( loginRequest,	new AuthenticationRequestListener() );
		}
		
	}

	
	
	@Override
	protected void onStart() {
		if (!spiceManager.isStarted()) {
			spiceManager.start(this);
		}
		super.onStart();
	}

	@Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();																																																																																																																																																	 
    }
	
	public void showSignupDialog(View v) {
		Intent intent = new Intent(context, SignupActivity.class);
		startActivityForResult(intent, 0);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (loginRequest != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		
		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			loginRequest = new LoginRequest(context, email);
    		spiceManager.execute( loginRequest, "json", DurationInMillis.ALWAYS_EXPIRED, new AuthenticationRequestListener() );
		}
	}

	private boolean isEmailValid(String email) {
		CharSequence inputStr = email;

	    if (inputStr == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(inputStr).matches();
	    }
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Make sure the request was successful
		if (resultCode == RESULT_OK) {
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(0, 0);
			finish();
		}
	}


	public final class AuthenticationRequestListener implements RequestListener< String > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
        	
        	showProgress(false);
        	
        	if(spiceException.getCause() instanceof ConnectException)
            {
        		Toast.makeText( LoginActivity.this, "Sorry, could not connect to the server.", Toast.LENGTH_SHORT ).show();
            }
            else if(spiceException.getCause() instanceof HttpResponseException)
            {
            	HttpResponseException exception = (HttpResponseException)spiceException.getCause();
            	if (exception.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            		mEmailView
        			.setError(getString(R.string.error_invalid_id));
                    mEmailView.requestFocus();
            	}
            }
            else
            {
                Ln.d("Other exception");
            }
        	
            loginRequest = null;
        }

        @Override
        public void onRequestSuccess( final String result ) {
        	showProgress(false);
            //Toast.makeText( LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT ).show();
            Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(0, 0);
			finish();
        }
    }
}
