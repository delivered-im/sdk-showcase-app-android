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

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import im.delivered.auth.DeliveredAuthCallback;
import im.delivered.auth.TokenRefreshCallback;
import im.delivered.config.DeliveredConfig;
import im.delivered.sdk.DeliveredSdk;

/**
 * Sample Service used to handle authentication with the Delivered SDK.
 *
 * For more information about the code in this class visit:
 * https://docs.delivered.im/android/secure-registration.html
 */
public class AuthenticationService extends IntentService {

    private static final String TAG = "AuthenticationService";

    public static final String ACTION_LOGIN        = "auth_action_login";
    public static final String ACTION_REGISTER     = "auth_action_register";
    public static final String ACTION_GOOGLE_LOGIN = "auth_google_login";

    public static final String EXTRA_USER_NAME         = "extra_user_name";
    public static final String EXTRA_USER_EMAIL        = "extra_user_email";
    public static final String EXTRA_USER_PASSWORD     = "extra_user_password";
    public static final String EXTRA_GOOGLE_CREDENTIAL = "extra_auth_token";

    public static final String KEY_USER_NAME  = "key_user_name";
    public static final String KEY_AUTH_TOKEN = "key_auth_token";
    public static final String KEY_AUTH_ERROR = "key_auth_error";

    private FirebaseAuth mAuth;

    public AuthenticationService() {
        super("AuthService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (action == null) {
            return;
        }

        mAuth = FirebaseAuth.getInstance();

        switch (action) {
            case ACTION_REGISTER:
                register(intent);
                break;
            case ACTION_LOGIN:
                login(intent);
                break;
            case ACTION_GOOGLE_LOGIN:
                // TODO: Feature not available at the moment.
                // loginWithGoogle(intent);
                break;
        }
    }

    private void register(final Intent registrationIntent) {
        final String userEmail = registrationIntent.getStringExtra(EXTRA_USER_EMAIL);
        final String userPassword = registrationIntent.getStringExtra(EXTRA_USER_PASSWORD);

        Log.d(TAG, "Registering new user: " + userEmail);

        final Intent result = new Intent(ACTION_REGISTER);
        final Bundle registerData = new Bundle();

        // 1. Create new user in Firebase.
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    // 2. Retrieve Firebase token from response.
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Firebase registration successfully completed");
                            final FirebaseUser user = task.getResult().getUser();
                            final Task<GetTokenResult> tokenTask = user.getIdToken(true);
                            tokenTask.addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {

                                // 3. Authenticate with Delivered using the received Firebase token.
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    String userName = registrationIntent.getStringExtra(EXTRA_USER_NAME);

                                    if (TextUtils.isEmpty(userName)) {
                                        userName = userEmail;
                                    }

                                    String token = task.getResult().getToken();
                                    if (!TextUtils.isEmpty(token)) {
                                        authenticateWithDelivered(token, userName,
                                                userEmail, ACTION_REGISTER);
                                    }
                                }
                            });
                        } else {
                            if (task.getException() != null) {
                                Log.e(TAG, "Registration failed: " + task.getException().getMessage());
                                registerData.putString(KEY_AUTH_ERROR,
                                        task.getException().getMessage());

                                result.putExtras(registerData);
                                broadcastResult(result);
                            }
                        }
                    }
                });
    }

    private void login(Intent loginIntent) {
        final String userEmail = loginIntent.getStringExtra(EXTRA_USER_EMAIL);
        final String userPassword = loginIntent.getStringExtra(EXTRA_USER_PASSWORD);
        final String authToken = DeliveredConfig.getAuthToken();

        Log.d(TAG, "Login - user: " + userEmail + " authToken: " + authToken);

        final Intent result = new Intent(ACTION_LOGIN);
        final Bundle loginData = new Bundle();

        // 1. Attempt to refresh an already stored Delivered auth token.
        Log.d(TAG, "Login in. Attempting Delivered token refresh");
        DeliveredSdk.refreshDeliveredToken(new TokenRefreshCallback() {

            // 2. If a token is found and successfully refreshed we are all good.
            @Override
            public void onAuthTokenRefreshed(String deliveredToken) {
                if (!TextUtils.isEmpty(deliveredToken)) {
                    Log.d(TAG, "Delivered token refresh successfully completed");
                    loginData.putString(KEY_USER_NAME, userEmail);
                    loginData.putString(KEY_AUTH_TOKEN, deliveredToken);

                    result.putExtras(loginData);
                    broadcastResult(result);
                } else {
                    // If no token is found, we sign in with Firebase once more
                    // to receive a new token and use it to authenticate with Delivered.
                    Log.d(TAG, "Delivered token failed. Re-authenticating...");
                    mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        task.getResult().getUser().getIdToken(true).addOnCompleteListener(
                                                new OnCompleteListener<GetTokenResult>() {

                                                    @Override
                                                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                        String token = task.getResult().getToken();
                                                        if (!TextUtils.isEmpty(token)) {
                                                            authenticateWithDelivered(token, userEmail,
                                                                    userEmail, ACTION_LOGIN);
                                                        }
                                                    }
                                                });
                                    } else {
                                        if (task.getException() != null) {
                                            Log.e(TAG, "Login failed: " + task.getException().getMessage());
                                            loginData.putString(KEY_AUTH_ERROR,
                                                    task.getException().getMessage());

                                            result.putExtras(loginData);
                                            broadcastResult(result);
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    private void authenticateWithDelivered(final String firebaseToken,
                                           final String userName,
                                           final String userEmail,
                                           final String action) {

        final Intent result = new Intent(action);
        final Bundle registerData = new Bundle();

        DeliveredSdk.registerWithFirebaseToken(userName, firebaseToken, new DeliveredAuthCallback() {

            @Override
            public void onAuthComplete(String deliveredToken) {
                if (!TextUtils.isEmpty(deliveredToken)) {
                    Log.d(TAG, "Delivered authentication successful - email: " + userEmail
                            + " token: " + deliveredToken);

                    registerData.putString(KEY_USER_NAME, userName);
                    registerData.putString(KEY_AUTH_TOKEN, deliveredToken);
                } else {
                    registerData.putString(KEY_AUTH_ERROR, "Delivered authentication failed");
                }

                result.putExtras(registerData);
                broadcastResult(result);
            }
        });
    }

    private void broadcastResult(Intent result) {
        LocalBroadcastManager.getInstance(AuthenticationService.this)
                .sendBroadcast(result);
    }

    // TODO: Feature not available at the moment.
    /*private void loginWithGoogle(Intent loginIntent) {
        AuthCredential credential = loginIntent.getParcelableExtra(EXTRA_GOOGLE_CREDENTIAL);
        mAuth.signInWithCredential(credential).addOnCompleteListener(
        new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                }
            }
        });
    }*/
}
