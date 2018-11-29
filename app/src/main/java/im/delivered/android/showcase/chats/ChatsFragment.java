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

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import im.delivered.android.showcase.R;
import im.delivered.ui.chat.ChatListFragment;
import im.delivered.ui.chat.callbacks.OnChatClickListener;
import im.delivered.ui.chat.callbacks.OnProfileAvatarClickListener;
import im.delivered.ui.chat.create.CreateChatActivity;

public class ChatsFragment extends ChatListFragment
        implements OnChatClickListener, OnProfileAvatarClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = "ChatsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setOnChatClickListener(this);
        setOnProfileAvatarClickListener(this);
    }

    @Override
    public boolean onChatClick(@NonNull String chatId, @Nullable String chatName) {
        //put your custom code
        return true;
    }

    @Override
    public boolean onProfileAvatarClick(@NonNull String chatId) {
        //put your custom code here
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chats, menu);

        final Activity activity = getActivity();
        if (activity != null) {
            final SearchManager searchManager = (SearchManager) getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);

            final SearchView searchView = (SearchView) menu
                    .findItem(R.id.chat_search).getActionView();

            searchView.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            searchView.setQueryHint("Search chats...");
            searchView.setSubmitButtonEnabled(true);

            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager
                        .getSearchableInfo(getActivity().getComponentName()));

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        handleChatSearch(query);
                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        handleChatSearch(newText);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_new_chat:
                if (getActivity() != null) {
                    startActivity(CreateChatActivity.newInstance(getActivity()));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
