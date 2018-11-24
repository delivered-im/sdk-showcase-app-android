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

package im.delivered.android.showcase.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import im.delivered.android.showcase.MainActivity;
import im.delivered.android.showcase.chats.ChatsFragment;
import im.delivered.android.showcase.requests.RequestsFragment;
import im.delivered.ui.user.contact.ContactFragment;
import im.delivered.ui.user.profile.MyProfileFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return MainActivity.TAB_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case MainTabLayout.INDEX_TAB_CHATS:
                return new ChatsFragment();
            case MainTabLayout.INDEX_TAB_REQUESTS:
                return new RequestsFragment();
            case MainTabLayout.INDEX_TAB_FRIENDS:
                return ContactFragment.newInstance();
            case MainTabLayout.INDEX_TAB_ME:
                return MyProfileFragment.newInstance();
            default:
                return new Fragment();
        }

    }
}