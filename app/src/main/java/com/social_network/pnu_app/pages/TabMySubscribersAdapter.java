package com.social_network.pnu_app.pages;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabMySubscribersAdapter extends FragmentPagerAdapter {
    public TabMySubscribersAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                MySubscribersFragment mySubscribersFragment = new MySubscribersFragment();
                return mySubscribersFragment;

            case 1:
                MySubscribedsFragment mySubscribedsFragment= new MySubscribedsFragment();
                return mySubscribedsFragment;

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
