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

import android.app.Application;
import android.content.Intent;

import im.delivered.android.showcase.conversation.ConversationActivity;
import im.delivered.sdk.DeliveredSdk;

import static im.delivered.ui.conversation.DeliveredConversationFragment.EXTRA_CHAT_ID;
import static im.delivered.ui.conversation.DeliveredConversationFragment.EXTRA_CHAT_NAME;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DeliveredSdk.setNavigation((chatId, chatName) -> {
                    final Intent intent = new Intent(this, ConversationActivity.class);
                    intent.putExtra(EXTRA_CHAT_ID, chatId);
                    intent.putExtra(EXTRA_CHAT_NAME, chatName);
                    startActivity(intent);
                }
        );
    }
}
