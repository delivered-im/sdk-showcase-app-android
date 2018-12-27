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

package im.delivered.android.showcase.fcm;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import im.delivered.fcm.DeliveredFcmService;

/**
 * For detailed instructions on how to enable push with the Delivered SDK, visit:
 * https://docs.delivered.im/android/push-notifications.html
 */
public class SampleFcmService extends DeliveredFcmService {

    private static final String TAG = "SampleFcmService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.toString());

        // Your custom push handling here if needed
    }
}
