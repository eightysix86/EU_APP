package com.social_network.pnu_app.pages;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class TabsFriendsAdapter extends FragmentPagerAdapter {

    public TabsFriendsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

          /*  case 1:
                OnlineFriendsFragment onlineFriendsFragment= new OnlineFriendsFragment();
                return onlineFriendsFragment;
*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Друзі";

   /*         case 1:
                return "Друзі онлайн";*/

             default:
                 return null;
        }
    }
}
