package com.example.newsapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.newsapp.api.NewsApi;
import com.example.newsapp.bean.NewsBean;
import com.example.newsapp.bean.NewsDateTime;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.example.newsapp.events.NightModeChangeEvent;
import com.example.newsapp.bean.ChannelBean;
import com.example.newsapp.adapters.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.ortiz.touchview.TouchImageView;
import com.trs.channellib.channel.channel.helper.ChannelDataHelper;
import com.wildma.pictureselector.PictureSelector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class MainActivity extends DeFaultActivity
    implements NavigationView.OnNavigationItemSelectedListener,
        ChannelDataHelper.ChannelDataRefreshListenter {
    private View navigationHeader;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ChannelDataHelper<ChannelBean> channelDataHelper;
    private int selectedChannelPosition = -1;
    private SectionsPagerAdapter pagerAdapter;
    private SearchView searchView;
//    private NewsMainFragment newsMainFragment;

    @Override
    public void updateData() {
        refreshTabs();
    }

    @Override
    public void onChannelSelected(boolean update, int position) {
        if (!update) {
            viewPager.setCurrentItem(position);
        } else {
            selectedChannelPosition = position;
        }
    }

    void refreshTabs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ChannelBean> channels = channelDataHelper.getShowChannels(getAllChannels());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pagerAdapter.updateChannels(channels);
                        if (selectedChannelPosition != -1) {
                            viewPager.setCurrentItem(selectedChannelPosition);
                            selectedChannelPosition = -1;
                        }
                    }
                });
            }
        }).start();
    }

    private void initTabs() {
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        channelDataHelper = new ChannelDataHelper<ChannelBean>(this, this, findViewById(R.id.toolbar));
        channelDataHelper.setSwitchView(findViewById(R.id.subscribe));
        refreshTabs();
    }

    void initSearchView(Menu optionsMenu) {
        searchView = (SearchView)optionsMenu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("家事国事天下事");
        searchView.setIconifiedByDefault(true);
//        searchView.setIconified(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        configureNavigationView();
        initTabs();

        EventBus.getDefault().register(this);
    }

    private void configureNavigationView() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationHeader = navigationView.getHeaderView(0);
        navigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items= {"更换封面","查看封面"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("封面");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                PictureSelector.create(MainActivity.this, PictureSelector.SELECT_REQUEST_CODE)
                                        .selectPicture(true, navigationHeader.getWidth(), navigationHeader.getHeight(), navigationHeader.getWidth(), navigationHeader.getHeight());
                            break;
                            case 1:
                                TouchImageView image = new TouchImageView(MainActivity.this);
                                image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                image.setImageDrawable(getCoverDrawable());

                                final Dialog imageDialog = new Dialog(MainActivity.this);
                                WindowManager.LayoutParams attributes = getWindow().getAttributes();
                                attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
                                attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
                                Objects.requireNonNull(imageDialog.getWindow()).setAttributes(attributes);

                                imageDialog.setContentView(image);
                                imageDialog.show();
                            break;
                        }
                    }
                });
                builder.show();
            }
        });
        reFreshCover();
    }

    private String getCoverPath() {
        String dir = getExternalFilesDir(null).getAbsolutePath();
        if (dir.charAt(dir.length() - 1) != '/') {
            dir = dir + "/";
        }
        return dir + "path.jpg";
    }

    private Drawable getCoverDrawable() {
        String coverPath = getCoverPath();
        Bitmap bitmap = BitmapFactory.decodeFile(coverPath);
        return new BitmapDrawable(getResources(), bitmap);
    }

    private void reFreshCover() {
        navigationHeader.setBackground(getCoverDrawable());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PictureSelector.SELECT_REQUEST_CODE:
                if (data != null) {
                    String path = data.getStringExtra(PictureSelector.PICTURE_PATH);
                    try {
                        assert path != null;
                        FileInputStream is = new FileInputStream(path);
                        FileOutputStream os = new FileOutputStream(getCoverPath());
                        for (;;) {
                            byte[] buf = new byte[1024];
                            int byteRead = is.read(buf);
                            if (byteRead == -1) break;
                            os.write(buf, 0, byteRead);
                        }
                        is.close();
                        os.flush();
                        os.close();
                        reFreshCover();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        initSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startSettings();
            break;

            case R.id.action_search:
                searchView.onActionViewExpanded();

            break;

            case R.id.share_test:
                OnekeyShare oks = new OnekeyShare();
                oks.disableSSOWhenAuthorize();
                oks.setTitle("分享测试");
                oks.setText("咕鸽快来写代码");
                oks.show(this);
            break;

            case R.id.news_test:
                NewsApi.requestNews(new NewsApi.SearchParams()
                        .setSize(20)
                        .setWords("咕咕")
                        .setCategory("科技")
                        .setStartDate(new NewsDateTime(2019, 7, 1))
                        .setEndDate(new NewsDateTime(2019, 7, 3)), newsBeanList -> {
                    if (newsBeanList.isEmpty()) {
                        Toast.makeText(this, "莫得新闻了，等哈再来哈", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, newsBeanList.get(0).getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
                startSettings();
            break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NightModeChangeEvent event) {
        recreate();
    }
}