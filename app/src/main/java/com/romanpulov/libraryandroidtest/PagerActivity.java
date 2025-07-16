package com.romanpulov.libraryandroidtest;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class PagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        ViewPager2 pager = findViewById(R.id.pager);
        pager.setAdapter(new TestPagerAdapter(getSupportFragmentManager(), getLifecycle()));
    }

    public static class TestPagerAdapter extends FragmentStateAdapter {

        public TestPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return switch (position) {
                case 0 -> PagerFragment1.newInstance();
                case 1 -> PagerFragment2.newInstance();
                default -> throw new RuntimeException("Invalid position");
            };
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}
