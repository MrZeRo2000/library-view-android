package com.romanpulov.libraryandroidtest;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import androidx.legacy.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class PagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TestPagerAdapter(getFragmentManager()));
    }

    public static class TestPagerAdapter extends FragmentPagerAdapter {

        public TestPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PagerFragment1.newInstance("p1", "p2");
                case 1:
                    return PagerFragment2.newInstance("p1", "p2");
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public String getPageTitle(int position) {
            return "Item " + position;
        }
    }

}
