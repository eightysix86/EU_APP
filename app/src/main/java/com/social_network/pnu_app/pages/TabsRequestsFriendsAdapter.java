package com.social_network.pnu_app.pages;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsRequestsFriendsAdapter extends FragmentPagerAdapter {
    public TabsRequestsFriendsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                RequestsFriendsFragment requestsFriendsFragment = new RequestsFriendsFragment();
                return requestsFriendsFragment;

            case 1:
                SenderedRequesrsFriendsFragment senderedRequesrsFriendsFragment= new SenderedRequesrsFriendsFragment();
                return senderedRequesrsFriendsFragment;

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
                return "Отримані";

            case 1:
                return "Надіслані";

            default:
                return null;
        }
    }
}