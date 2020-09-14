package app.com.worldofwealth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import app.com.worldofwealth.fragments.MyVoiceListFragment;
import app.com.worldofwealth.fragments.LawMakersListFragment;
import app.com.worldofwealth.fragments.ScholarsListFragment;
import com.google.android.material.tabs.TabLayout;

public class DirectoryActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.icon_legislator_blue,
            R.drawable.icon_judiciary_blue,
            R.drawable.icon_ngo_blue,

    };
    public boolean onSupportNavigateUp() {

        startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.directory);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), DirectoryActivity.this));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }



    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LawMakersListFragment();
                case 1:
                    return new MyVoiceListFragment();
                case 2:
                    return new ScholarsListFragment();

                default:

                    return null;
            }
        }
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]).setText("Law Makers");
        tabLayout.getTabAt(1).setIcon(tabIcons[1]).setText("My Voice");
        tabLayout.getTabAt(2).setIcon(tabIcons[2]).setText("Scholars");
    }

    private void Backpressed() {
        super.onBackPressed();
        return;
    }


}
