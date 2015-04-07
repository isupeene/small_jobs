package com.smalljobs.jobseeker.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.smalljobs.jobseeker.R;
import com.smalljobs.jobseeker.UserProfilePostRequest;
import com.smalljobs.jobseeker.models.Contractor;
import com.smalljobs.jobseeker.models.User;

/**
 * A screen that displays user's profile and allows to update it
 * 
 * Requirements Specifications Reference:
 * 3.2.2.1.4 Permit the user to update their profile with relevant
 * information including Skills and contact information (name, email, phone number).
 */

public class MyProfileActivity extends BaseActivity implements TextWatcher {

	EditText nameField;
	EditText emailField;
	EditText phoneNumberField;
	EditText descriptionField;
	
	private boolean textChanged = false;
	
	private UserProfilePostRequest profilePostRequest = null;
	
	private Contractor user;
	private Contractor changedUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_PROGRESS );
		setContentView(R.layout.activity_my_profile);
		
		nameField = (EditText) findViewById(R.id.profile_name);
		emailField = (EditText) findViewById(R.id.profile_email_address);
		phoneNumberField = (EditText) findViewById(R.id.profile_phone_number);
		descriptionField = (EditText) findViewById(R.id.profile_description);
		
		user = User.getInstance().getContractor();
		
		nameField.setText(user.getName());
		emailField.setText(user.getEmail());
		if (user.getPhoneNumber() != null) {
			phoneNumberField.setText(user.getPhoneNumber());
		}
		if (user.getDescription() != null) {
			descriptionField.setText(user.getDescription());
		}
		
		nameField.addTextChangedListener(this);
		emailField.addTextChangedListener(this);
		phoneNumberField.addTextChangedListener(this);
		descriptionField.addTextChangedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_profile, menu);
		
		if (!textChanged) {
			menu.findItem(R.id.action_save).setEnabled(false);
		} else {
			menu.findItem(R.id.action_save).setEnabled(true);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_save) {
			boolean safe = true;
			String name = nameField.getText().toString();
			String email = emailField.getText().toString();
			if (name.isEmpty()) {
				safe = false;
				nameField.setError(getString(R.string.error_field_required));
			}
			if (email.isEmpty()) {
				safe = false;
				emailField.setError(getString(R.string.error_field_required));
			}
			if (!isEmailValid(email)) {
				safe = false;
				emailField.setError(getString(R.string.error_invalid_email));
			}
			changedUser = user;
			changedUser.setName(name.trim());
			changedUser.setEmail(email.trim());
			changedUser.setDescription(descriptionField.getText().toString().trim());
			changedUser.setPhoneNumber(phoneNumberField.getText().toString().trim());
			if (safe) {
				setProgressBarIndeterminate( true );
		        setProgressBarVisibility( true );
		        
				profilePostRequest = new UserProfilePostRequest(changedUser);
				getSpiceManager().execute(profilePostRequest, new PostProfileRequestListener());
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean isEmailValid(String email) {
		CharSequence inputStr = email;
	    return android.util.Patterns.EMAIL_ADDRESS.matcher(inputStr).matches();
	}
	
    public final class PostProfileRequestListener implements RequestListener< String > {

        @Override
        public void onRequestFailure( SpiceException spiceException ) {
        	setProgressBarVisibility( false );
            Toast.makeText( MyProfileActivity.this, "Could not save profile.", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onRequestSuccess( final String result ) {
        	setProgressBarVisibility( false );
            //Toast.makeText( ViewPostingActivity.this, "success", Toast.LENGTH_SHORT ).show();
        	textChanged = false;
        	User.getInstance().setContractor(changedUser);
			invalidateOptionsMenu();
            System.out.println(result);
        }
    }

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		textChanged = true;
		invalidateOptionsMenu();
		
	}
}
