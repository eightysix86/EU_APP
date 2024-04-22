package com.social_network.pnu_app.pages;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabSubscribersProfileAdapter extends FragmentPagerAdapter {
    public TabSubscribersProfileAdapter(FragmentManager fm) { super(fm); }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                SubscribersProfileFragment subscribersProfileActivity = new SubscribersProfileFragment();
                return subscribersProfileActivity;

            case 1:
                SubscribedsProfileFragment subscribedsProfileFragment = new SubscribedsProfileFragment();
                return subscribedsProfileFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Підписники";

            case 1:
                return "Читачі";

            default:
                return null;
        }
    }
}
