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

package im.delivered.android.showcase.conversation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import im.delivered.android.showcase.R;
import im.delivered.messaging.model.Message;
import im.delivered.ui.conversation.DeliveredConversationFragment;
import im.delivered.ui.conversation.callbacks.OnMessageClickListener;
import im.delivered.ui.conversation.callbacks.OnMessageLongClickListener;
import im.delivered.ui.conversation.callbacks.OnMessageSelectListener;
import im.delivered.ui.conversation.callbacks.OnUserAvatarClickListener;

import static im.delivered.ui.conversation.DeliveredConversationFragment.EXTRA_CHAT_ID;
import static im.delivered.ui.conversation.DeliveredConversationFragment.EXTRA_CHAT_NAME;

public class ConversationActivity extends AppCompatActivity
        implements OnMessageSelectListener, OnMessageClickListener,
        OnMessageLongClickListener, OnUserAvatarClickListener, ActionMode.Callback {

    @SuppressWarnings("unused")
    private static final String TAG = "ConversationActivity";

    private ActionMode actionMode;
    private DeliveredConversationFragment mConversationFragment;

    private String mChatId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String chatName = "";
        if (getIntent().getExtras() != null) {
            mChatId = getIntent().getExtras().getString(EXTRA_CHAT_ID);
            chatName = getIntent().getExtras().getString(EXTRA_CHAT_NAME);
        }

        if (mChatId == null || mChatId.isEmpty()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_conversation);

        Toolbar toolbar = findViewById(R.id.conversation_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(chatName);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mConversationFragment = DeliveredConversationFragment.newInstance(mChatId);
        fragmentTransaction.add(R.id.conversation_fragment_container, mConversationFragment);
        fragmentTransaction.commit();

        mConversationFragment.setOnMessageSelectListener(this);
        mConversationFragment.setOnMessageClickListener(this);
        mConversationFragment.setOnMessageLongClickListener(this);
        mConversationFragment.setOnUserAvatarClickListener(this);

        mConversationFragment.setMessageListBackgroundColor(R.color.dlvrd_delivered_purple_light_50);
        mConversationFragment.setInputViewBackgroundColor(android.R.color.white);
        mConversationFragment.setInputViewIconTintColor(
                ContextCompat.getColor(this, R.color.dlvrd_delivered_purple));

        mConversationFragment.setIncomingMessageScheme(
                ContextCompat.getColor(this, android.R.color.white),
                ContextCompat.getColor(this, R.color.dlvrd_delivered_purple));

        mConversationFragment.setOutgoingMessageScheme(
                ContextCompat.getColor(this, R.color.dlvrd_delivered_purple),
                ContextCompat.getColor(this, android.R.color.white));

        mConversationFragment.setMessageTextSize(18);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_conversation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_placeholder:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_action_message, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.copy_message).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.delete_message).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy_message:
                mConversationFragment.copySelectedMessages();
                actionMode.finish();
                break;
            case R.id.delete_message:
                mConversationFragment.deleteSelectedMessages();
                actionMode.finish();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (actionMode != null) {
            mConversationFragment.clearMessageListSelections();
            actionMode = null;
        }
    }

    @Override
    public boolean onMessageClick(@NonNull Message message) {
        return true;
    }

    @Override
    public boolean onMessageLongClick(@NonNull Message message) {
        return false;
    }

    @Override
    public boolean onUserAvatarClick(@NonNull String userId, @NonNull String chatId) {
        Toast.makeText(this, "Clicked avatar of user: " + userId, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onMessageSelect(int selectionCount) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(this);
        }

        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(selectionCount));
        }

        if (actionMode != null && selectionCount == 0) {
            actionMode.finish();
        }
    }
}
