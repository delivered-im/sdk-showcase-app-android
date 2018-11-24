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
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import im.delivered.android.showcase.chats.PublicChatListActivity;
import im.delivered.android.showcase.pager.MainPagerAdapter;
import im.delivered.android.showcase.pager.MainTabLayout;
import im.delivered.android.showcase.settings.SettingsActivity;
import im.delivered.config.DeliveredConfig;
import im.delivered.ui.chat.create.CreateChatActivity;

import static im.delivered.android.showcase.pager.MainTabLayout.INDEX_TAB_CHATS;

public class MainActivity extends AppCompatActivity {

    public static final int TAB_COUNT = 4;

    private FloatingActionsMenu fabActionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.mipmap.ic_launcher);
        }

        // We first check if we have a valid Delivered auth token.
        final String authToken = DeliveredConfig.getAuthToken();

        // If no token is found we prompt the user to enter their credentials.
        if (authToken == null || authToken.isEmpty()) {
            startActivity(new Intent(MainActivity.this,
                    LoginActivity.class));
            finish();
            return;
        }

        ViewPager viewPager = findViewById(R.id.main_view_pager);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));

        MainTabLayout tabLayout = findViewById(R.id.main_tab_layout);
        tabLayout.setUpMainTabs(TAB_COUNT);
        tabLayout.setMainViewPager(viewPager);

        fabActionMenu =
                findViewById(R.id.fab_action_menu);
        final FloatingActionButton searchPublicChatsFab =
                findViewById(R.id.search_public_chats_fab);
        final FloatingActionButton createNewChatFab =
                findViewById(R.id.create_new_chat_fab);

        searchPublicChatsFab.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PublicChatListActivity.class)));
        createNewChatFab.setOnClickListener(v ->
                startActivity(CreateChatActivity.newInstance(this)));

        if (viewPager.getCurrentItem() == INDEX_TAB_CHATS) {
            fabActionMenu.setVisibility(View.VISIBLE);
        } else {
            fabActionMenu.setVisibility(View.GONE);
            if (fabActionMenu.isExpanded()) {
                fabActionMenu.collapse();
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == INDEX_TAB_CHATS) {
                    fabActionMenu.setVisibility(View.VISIBLE);
                } else {
                    fabActionMenu.setVisibility(View.GONE);
                    if (fabActionMenu.isExpanded()){
                        fabActionMenu.collapse();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // no-op
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // no-op
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fabActionMenu.isExpanded()){
            fabActionMenu.collapse();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
