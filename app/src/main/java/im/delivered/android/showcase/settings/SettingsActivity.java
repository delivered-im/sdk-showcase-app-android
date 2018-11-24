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

package im.delivered.android.showcase.settings;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.delivered.android.showcase.R;
import im.delivered.config.DeliveredConfig;
import im.delivered.ui.ViewModelFactory;
import im.delivered.ui.user.UserViewModel;
import im.delivered.ui.user.profile.UserProfileActivity;
import im.delivered.ui.utils.ViewExtKt;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.settings));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View devicesItemView = findViewById(R.id.settings_devices_item_view);
        devicesItemView.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this,
                        RegisteredDevicesActivity.class))
        );

        TextView tvUserName = findViewById(R.id.tv_profile_name);
        ImageView ivAvatar = findViewById(R.id.iv_avatar);


        final ViewModelFactory viewModelFactory = new ViewModelFactory(SettingsActivity.this);

        final UserViewModel userViewModel = ViewModelProviders
                .of(SettingsActivity.this, viewModelFactory)
                .get(UserViewModel.class);

        userViewModel.loadUser(DeliveredConfig.getUserId());

        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                String profileName = user.getProfile().getName();
                profileName = profileName == null ? "NA" : profileName;
                tvUserName.setText(profileName);

                ViewExtKt.displayAvatar(ivAvatar, user);

                ivAvatar.setOnClickListener(v -> startActivity(UserProfileActivity.newInstance(this, user.getProfile().getUserId(), null)));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
