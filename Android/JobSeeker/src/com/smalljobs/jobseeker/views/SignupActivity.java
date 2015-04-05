package com.smalljobs.jobseeker.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.CreateAccountRequest;
import com.smalljobs.jobseeker.LoginRequest;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.models.Contractor;

/**
 * A login screen that offers login via email/password.
 */
public class SignupActivity extends Activity {

	// UI references.
	private AutoCompleteTextView mEmailView;
	private EditText mNameView;
	private View mProgressView;
	private View mLoginFormView;

	private Context context;
	
	private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceService.class);
	private CreateAccountRequest signUpRequest = null;
	private LoginRequest loginRequest = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		context = getApplicationContext();
		
		// Set up the login form.
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

		mNameView = (EditText) findViewById(R.id.name);
		mNameView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_up_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		
		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
		
	}
	
	@Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }
	
	@Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (signUpRequest != null || loginRequest != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mNameView.setError(null);

		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();
		String name = mNameView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid name, if the user entered one.
		if (TextUtils.isEmpty(name)) {
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
			cancel = true;
		} else if (!isNameValid(name)) {
			mNameView.setError(getString(R.string.error_invalid_name));
			focusView = mNameView;
			cancel = true;
		}

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
			signUpRequest = new CreateAccountRequest(context, email, name);
			spiceManager.execute( signUpRequest, "json", DurationInMillis.ONE_MINUTE, new AuthenticationRequestListener() );
			
			
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

	private boolean isNameValid(String name) {
		return name.length() > 2;
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
	
	public final class AuthenticationRequestListener implements RequestListener< Contractor > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
        	System.out.println("Sign up failed");
        	spiceException.printStackTrace();
            Toast.makeText( SignupActivity.this, "failure", Toast.LENGTH_SHORT ).show();
            showProgress(false);
            mEmailView
			.setError(getString(R.string.error_id_exists));
            mEmailView.requestFocus();
            signUpRequest = null;
            loginRequest = null;
        }

        @Override
        public void onRequestSuccess( final Contractor result ) {
        	showProgress(false);
            //Toast.makeText( SignupActivity.this, "success", Toast.LENGTH_SHORT ).show();
            Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(0, 0);
			finish();
        }
    }

	
}
