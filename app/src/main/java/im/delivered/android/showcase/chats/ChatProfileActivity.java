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

package im.delivered.android.showcase.chats;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import im.delivered.android.showcase.R;
import im.delivered.ui.chat.profile.ChatProfileFragment;

import static im.delivered.ui.conversation.DeliveredConversationFragment.EXTRA_CHAT_ID;

public class ChatProfileActivity extends AppCompatActivity {

    private ChatProfileFragment mChatProfileFragment;
    private String mChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_profile);

        if (getIntent().getExtras() != null) {
            mChatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        }

        if (mChatId == null || mChatId.isEmpty()) {
            finish();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mChatProfileFragment = ChatProfileFragment.newInstance(mChatId);
        fragmentTransaction.add(R.id.chat_profile_fragment_container, mChatProfileFragment);
        fragmentTransaction.commit();
    }

}
