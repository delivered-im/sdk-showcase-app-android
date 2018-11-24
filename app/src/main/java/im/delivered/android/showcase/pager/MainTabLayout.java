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

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import im.delivered.android.showcase.R;

public class MainTabLayout extends TabLayout
        implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    public static final int INDEX_TAB_CHATS    = 0;
    public static final int INDEX_TAB_FRIENDS  = 1;
    public static final int INDEX_TAB_REQUESTS = 2;
    public static final int INDEX_TAB_ME       = 3;

    private ViewPager mainViewPager;

    private int mCurrentIndex;

    public MainTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnTabSelectedListener(this);
    }

    public void setMainViewPager(ViewPager mainViewPager) {
        this.mainViewPager = mainViewPager;
        this.mainViewPager.addOnPageChangeListener(this);
    }

    @SuppressWarnings("SameParameterValue")
    public void setUpMainTabs(int tabCount) {
        for (int i = 0; i < tabCount; i++) {
            addTab(newTab().setCustomView(R.layout.tab_main_layout));
            Tab tab = getTabAt(i);
            if (tab != null) {
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    TextView tabTitle = tabView.findViewById(R.id.tab_main_title);
                    switch (i) {
                        case INDEX_TAB_CHATS:
                            tabTitle.setText(getResources()
                                    .getString(R.string.main_tab_title_chats));
                            tabTitle.setTextColor(ContextCompat.getColor(getContext(),
                                    R.color.dlvrd_delivered_purple));
                            break;
                        case INDEX_TAB_REQUESTS:
                            tabTitle.setText(getResources()
                                    .getString(R.string.main_tab_title_requests));
                            tabTitle.setTextColor(ContextCompat.getColor(getContext(),
                                    R.color.dlvrd_delivered_purple));
                            break;
                        case INDEX_TAB_FRIENDS:
                            tabTitle.setText(getResources()
                                    .getString(R.string.main_tab_title_friends));
                            tabTitle.setTextColor(ContextCompat.getColor(getContext(),
                                    R.color.dlvrd_delivered_purple));
                            break;
                        case INDEX_TAB_ME:
                            tabTitle.setText(getResources()
                                    .getString(R.string.main_tab_title_me));
                            tabTitle.setTextColor(ContextCompat.getColor(getContext(),
                                    R.color.dlvrd_delivered_purple));
                            break;

                    }
                }
            }
        }
    }

    /**
     * This method can be use if we want to change the tab appearance
     * when selected/unselected.
     *
     * @param selected whether the tab is selected or not
     * @param tab      the tab to change
     */
    private void setTabState(boolean selected, Tab tab) {
        int tabPosition = tab.getPosition();
        View tabView = tab.getCustomView();
        if (tabView != null) {
            TextView tabTitle = tabView.findViewById(R.id.tab_main_title);
            switch (tabPosition) {
                case INDEX_TAB_CHATS:
                    break;
                case INDEX_TAB_FRIENDS:
                    break;
                case INDEX_TAB_REQUESTS:
                    break;
                case INDEX_TAB_ME:
                    break;
            }
        }
    }

    @Override
    public void onTabSelected(Tab tab) {
        setTabState(true, tab);
        if (mainViewPager != null) {
            mainViewPager.setCurrentItem(tab.getPosition());
        }
    }

    @Override
    public void onPageSelected(int position) {
        TabLayout.Tab currentTab = getTabAt(position);
        if (currentTab != null) {
            currentTab.select();
        }
    }

    @Override
    public void onTabUnselected(Tab tab) {
        setTabState(false, tab);
    }

    @Override
    public void onTabReselected(Tab tab) {
        // no-op
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // no-op
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // no-op
    }
}
