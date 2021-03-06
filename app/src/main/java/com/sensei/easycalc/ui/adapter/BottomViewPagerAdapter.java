package com.sensei.easycalc.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.sensei.easycalc.MainActivity;
import com.sensei.easycalc.ui.fragment.HistoryFragment;
import com.sensei.easycalc.ui.fragment.NumpadFragment;
import com.sensei.easycalc.ui.fragment.TrigonometryFragment;

public class BottomViewPagerAdapter extends FragmentStatePagerAdapter
                                    implements ViewPager.OnPageChangeListener{

    private static final String TAG = "BottomViewPagerAdapter";

    private static final int NUM_VIEWS = 3;
    private MainActivity activity = null;

    public BottomViewPagerAdapter( FragmentManager fm, MainActivity a ) {
        super( fm );
        this.activity = a;
    }

    @Override
    public Fragment getItem( int position ) {
        switch( position ) {
            case 0:
                return new HistoryFragment().withActivity( activity );

            case 1:
                return new NumpadFragment();

            case 2:
                return new TrigonometryFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_VIEWS;
    }

    @Override
    public int getItemPosition( Object object ) {
        if( object instanceof HistoryFragment ) {
            Log.d( TAG, "View changed, refreshing" );
            ((HistoryFragment)object).refreshData();
        }

        return super.getItemPosition( object );
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch( position ) {
            case 0:
                return "HISTORY";

            case 1:
                return "NUMPAD";

            case 2:
                return "TRIG";
        }
        return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        if( position == 0 ) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) { }
}
