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

import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import im.delivered.DeliveredSdk;
import im.delivered.InAppNavigation;
import im.delivered.android.showcase.conversation.ConversationActivity;

public class BaseApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);

        /* Method used register the Activity you wish to use for
         * your conversations with the Delivered SDK UI widgets.
         *
         * For more information visit:
         * https://docs.delivered.im/android/initial-setup.html
         */
        DeliveredSdk.setNavigation(new InAppNavigation() {

            @Override
            public Intent getConversationActivity() {
                return new Intent(
                        BaseApp.this.getApplicationContext(), ConversationActivity.class);
            }
        });
    }
}
