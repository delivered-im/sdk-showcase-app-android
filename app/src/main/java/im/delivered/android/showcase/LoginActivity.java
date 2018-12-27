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

import android.accounts.AccountAuthenticatorActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static im.delivered.android.showcase.AuthenticationService.KEY_AUTH_ERROR;
import static im.delivered.android.showcase.Constants.REQUEST_CODE_REGISTER;


/**
 * Activity used to handle the input of users who wish to
 * login with their existing credentials.
 *
 * For more information about authentication with the Delivered SDK visit:
 * https://docs.delivered.im/android/secure-registration.html
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    private EditText mUserEmailInputView;
    private EditText mUserPasswordInputView;

    private Button mLoginButton;
    private TextView mRegisterRequestView;

    private ProgressBar mProgressBar;
    private View mBackgroundMask;

    // TODO: Feature not available at the moment.
    // private GoogleSignInClient mGoogleSignInClient;

    private BroadcastReceiver mAuthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            completeLogin(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserEmailInputView = findViewById(R.id.user_email_input_view);
        mUserPasswordInputView = findViewById(R.id.user_password_input_view);
        mUserPasswordInputView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });

        mProgressBar = findViewById(R.id.login_progress_bar);
        mBackgroundMask = findViewById(R.id.login_background_mask);

        mLoginButton = findViewById(R.id.login_button);
        ViewCompat.setBackgroundTintList(mLoginButton,
                ContextCompat.getColorStateList(this, android.R.color.white));

        mLoginButton.setOnClickListener(view -> login());

        mRegisterRequestView = findViewById(R.id.register_request_view);
        mRegisterRequestView.setOnClickListener(view -> {
            // Here we launch the Registration activity where
            // the user can create an account and subsequently log in.
            // Once that is done a result will be returned to this activity.
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);

            if (getIntent().getExtras() != null) {
                intent.putExtras(getIntent().getExtras());
            }

            startActivityForResult(intent, REQUEST_CODE_REGISTER);
        });

        // TODO: Feature not available at the moment.
        /*SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_LOGIN);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AuthenticationService.ACTION_LOGIN);
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

    public void login() {
        final String userEmail = mUserEmailInputView.getText()
                .toString().toLowerCase().trim();

        final String userPassword = mUserPasswordInputView.getText()
                .toString().trim();

        if ((!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword))
                && userPassword.length() > 6) {

            Intent intent = new Intent(this, AuthenticationService.class);
            intent.setAction(AuthenticationService.ACTION_LOGIN);

            intent.putExtra(AuthenticationService.EXTRA_USER_EMAIL, userEmail);
            intent.putExtra(AuthenticationService.EXTRA_USER_PASSWORD, userPassword);

            startService(intent);

            mUserEmailInputView.setEnabled(false);
            mUserPasswordInputView.setEnabled(false);

            mLoginButton.setEnabled(false);
            mRegisterRequestView.setEnabled(false);

            mProgressBar.setVisibility(View.VISIBLE);
            mBackgroundMask.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "Please enter a valid username/password. " +
                    "Password must be at least 6 characters.", Toast.LENGTH_LONG).show();
        }
    }

    private void completeLogin(Intent intent) {
        final String error = intent.getStringExtra(KEY_AUTH_ERROR);
        if (error != null) {
            mUserEmailInputView.setEnabled(true);
            mUserPasswordInputView.setEnabled(true);

            mLoginButton.setEnabled(true);
            mRegisterRequestView.setEnabled(true);

            mProgressBar.setVisibility(View.GONE);
            mBackgroundMask.setVisibility(View.GONE);

            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            completeLogin(data);
        }

        // TODO: Feature not available at the moment.
        /*else if (requestCode == REQUEST_CODE_GOOGLE_LOGIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase.
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential =
                        GoogleAuthProvider.getCredential(account.getIdToken(), null);

                Intent intent = new Intent(this, AuthenticationService.class);
                intent.setAction(AuthenticationService.ACTION_GOOGLE_LOGIN);
                intent.putExtra(AuthenticationService.EXTRA_GOOGLE_CREDENTIAL, credential);
                startService(intent);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately.
                Log.w(TAG, "Google sign in failed", e);
            }
        }*/
    }
}

