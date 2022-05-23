 package com.example.messangertest.view;

 import android.content.Intent;
 import android.os.Bundle;
 import android.os.Handler;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.databinding.DataBindingUtil;
 import androidx.fragment.app.Fragment;
 import androidx.fragment.app.FragmentManager;
 import androidx.fragment.app.FragmentPagerAdapter;
 import androidx.viewpager.widget.ViewPager;

 import com.example.messangertest.R;
 import com.example.messangertest.databinding.ActivityMainBinding;
 import com.example.messangertest.menu.CallsFragment;
 import com.example.messangertest.menu.ChatsFragment;
 import com.example.messangertest.menu.StatusFragment;
 import com.example.messangertest.view.settings.SettingsActivity;

 import java.util.ArrayList;
 import java.util.List;

 public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        setSupportActionBar(binding.toolbar);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                changeFabIcon(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //TODO ViewTabs... und Fragment Anlengen(BLANK)
    private void setUpWithViewPager(ViewPager viewPager){
        MainActivity.SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(),"Chats");
        adapter.addFragment(new CallsFragment(),"Calls");
        adapter.addFragment(new StatusFragment(),"Status");

        viewPager.setAdapter(adapter);
    }
    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }

    }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
     }

     //Menu On Klick --> SearchBtn und MenuBtn
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.menu_search: Toast.makeText(this, "Action Search", Toast.LENGTH_LONG).show();break;
            case R.id.action_new_group: Toast.makeText(this, "Action New Groups", Toast.LENGTH_LONG).show();break;
            case R.id.action_new_broadcast: Toast.makeText(this, "Action More", Toast.LENGTH_LONG).show();break;
            case R.id.action_wa_web: Toast.makeText(this, "Action Web", Toast.LENGTH_LONG).show();break;
            case R.id.action_starred_message: Toast.makeText(this, "Action Starred Message", Toast.LENGTH_LONG).show();break;
            case R.id.action_google_maps: Toast.makeText(this, "Action Google Maps", Toast.LENGTH_LONG).show();break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
     }

     private void changeFabIcon(final int index){
        binding.fabActionBtn.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (index){
                    case 0 : binding.fabActionBtn.setImageDrawable(getDrawable(R.drawable.ic_chat_24dp));break;
                    case 1 : binding.fabActionBtn.setImageDrawable(getDrawable(R.drawable.ic_camera_alt_24dp));break;
                    case 2 : binding.fabActionBtn.setImageDrawable(getDrawable(R.drawable.ic_call_back_24dp));break;

                }
                binding.fabActionBtn.show();



            }
        }, 200);

     }
 }
