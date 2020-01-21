package com.magicbio.truename.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.magicbio.truename.fragments.IntroFirstPage;
import com.magicbio.truename.fragments.IntroSecondPage;

/**
 * Created by Ahmed Bilal on 12/5/2018.
 */


public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new IntroFirstPage(); //ChildFragment1 at position 0
            case 1:
                return new IntroSecondPage(); //ChildFragment2 at position 1
            //ChildFragment3 at position 2
        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 2; //three fragments
    }
}
