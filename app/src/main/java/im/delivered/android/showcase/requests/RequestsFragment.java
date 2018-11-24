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

package im.delivered.android.showcase.requests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import im.delivered.android.showcase.conversation.ConversationActivity;
import im.delivered.ui.chat.invites.InvitesListFragment;
import im.delivered.ui.chat.invites.callbacks.OnInviteAcceptListener;
import im.delivered.ui.chat.invites.callbacks.OnInviteRejectListener;

import static im.delivered.ui.conversation.DeliveredConversationFragment.EXTRA_CHAT_ID;
import static im.delivered.ui.conversation.DeliveredConversationFragment.EXTRA_CHAT_NAME;

public class RequestsFragment extends InvitesListFragment
        implements OnInviteAcceptListener, OnInviteRejectListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnInviteAcceptListener(this);
        setOnInviteRejectListener(this);
    }

    @Override
    public void onInviteAccept(@NonNull String chatId, @NonNull String chatName) {
        acceptInvite(chatId);

        final Activity activity = getActivity();
        if (activity != null) {
            final Intent intent = new Intent(activity, ConversationActivity.class);
            intent.putExtra(EXTRA_CHAT_ID, chatId);
            intent.putExtra(EXTRA_CHAT_NAME, chatName);
            startActivity(intent);
        }

        Toast.makeText(getContext(), "Invite accepted",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInviteReject(@NonNull String chatId) {
        rejectInvite(chatId);
        Toast.makeText(getContext(), "Invite Rejected",
                Toast.LENGTH_SHORT).show();
    }
}
