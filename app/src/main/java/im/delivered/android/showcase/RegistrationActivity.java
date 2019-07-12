/*
 * Copyright (C) Delivered Oy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.delivered.android.showcase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static im.delivered.android.showcase.AuthenticationService.KEY_AUTH_ERROR;

/**
 * Activity used to handle the input of users who wish to
 * register with the Delivered SDK for the first time.
 *
 * For more information about authentication with the Delivered SDK visit:
 * https://docs.delivered.im/android/secure-registration.html
 */
public class RegistrationActivity extends AppCompatActivity {

    private EditText mUserNameInputView;
    private EditText mUserEmailInputView;
    private EditText mUserPasswordView;

    private Button mRegisterButton;
    private TextView mLoginRequestView;

    private ProgressBar mProgressBar;
    private View mBackgroundMask;

    private BroadcastReceiver mAuthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            completeRegistration(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mUserNameInputView = findViewById(R.id.username_input_view);
        mUserEmailInputView = findViewById(R.id.user_email_input_view);
        mUserPasswordView = findViewById(R.id.user_password_input_view);
        mUserPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    RegistrationActivity.this.registerUser();
                    return true;
                }
                return false;
            }
        });

        mProgressBar = findViewById(R.id.register_progress_bar);
        mBackgroundMask = findViewById(R.id.register_background_mask);

        mRegisterButton = findViewById(R.id.register_button);
        ViewCompat.setBackgroundTintList(mRegisterButton,
                ContextCompat.getColorStateList(this, android.R.color.white));

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationActivity.this.registerUser();
            }
        });

        mLoginRequestView = findViewById(R.id.existing_account_view);
        mLoginRequestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationActivity.this.setResult(RESULT_CANCELED);
                RegistrationActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AuthenticationService.ACTION_REGISTER);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mAuthReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mAuthReceiver);
    }

    private void registerUser() {
        String userName = mUserNameInputView.getText()
                .toString();

        String userEmail = mUserEmailInputView.getText()
                .toString().toLowerCase().trim();

        String userPassword = mUserPasswordView.getText()
                .toString().trim();

        if ((!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword))
                && userPassword.length() > 6) {

            Intent intent = new Intent(this, AuthenticationService.class);
            intent.setAction(AuthenticationService.ACTION_REGISTER);

            intent.putExtra(AuthenticationService.EXTRA_USER_NAME, userName);
            intent.putExtra(AuthenticationService.EXTRA_USER_EMAIL, userEmail);
            intent.putExtra(AuthenticationService.EXTRA_USER_PASSWORD, userPassword);

            startService(intent);

            mUserNameInputView.setEnabled(false);
            mUserEmailInputView.setEnabled(false);
            mUserPasswordView.setEnabled(false);

            mRegisterButton.setEnabled(false);
            mLoginRequestView.setEnabled(false);

            mProgressBar.setVisibility(View.VISIBLE);
            mBackgroundMask.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "Please enter a valid username/password. " +
                    "Password must be at least 6 characters.", Toast.LENGTH_LONG).show();
        }
    }

    private void completeRegistration(Intent intent) {
        final String error = intent.getStringExtra(KEY_AUTH_ERROR);
        if (error != null) {
            mUserNameInputView.setEnabled(true);
            mUserEmailInputView.setEnabled(true);
            mUserPasswordView.setEnabled(true);

            mRegisterButton.setEnabled(true);
            mLoginRequestView.setEnabled(true);

            mProgressBar.setVisibility(View.GONE);
            mBackgroundMask.setVisibility(View.GONE);

            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        } else {
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}