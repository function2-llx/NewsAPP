package com.java.luolingxiao;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.java.luolingxiao.adapters.SectionsPagerAdapter;
import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.bean.ChannelBean;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.bean.NewsDateTime;
import com.java.luolingxiao.events.NightModeChangeEvent;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.ortiz.touchview.TouchImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.trs.channellib.channel.channel.helper.ChannelDataHelper;
import com.wildma.pictureselector.PictureSelector;
import com.yanzhenjie.nohttp.error.NetworkError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;

public class MainActivity extends DeFaultActivity
    implements NavigationView.OnNavigationItemSelectedListener,
        ChannelDataHelper.ChannelDataRefreshListenter {

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    private View navigationHeader;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ChannelDataHelper<ChannelBean> channelDataHelper;
    private int selectedChannelPosition = -1;
    private SectionsPagerAdapter pagerAdapter;

    private MaterialSearchBar searchBar;
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

    private void refreshTabs() {
        new Thread(() -> {
            runOnUiThread(() -> {
                List<ChannelBean> channels = channelDataHelper.getShowChannels(getAllChannels());
                pagerAdapter.updateChannels(channels);
                if (selectedChannelPosition != -1) {
                    viewPager.setCurrentItem(selectedChannelPosition);
                    selectedChannelPosition = -1;
                }
            });
        }).start();
    }

    private void initTabs() {
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        channelDataHelper = new ChannelDataHelper<ChannelBean>(this, this, findViewById(R.id.container_main));
        channelDataHelper.setSwitchView(findViewById(R.id.subscribe));
        viewPager.setOffscreenPageLimit(100);

        refreshTabs();
    }

    void initSearchBar() {
        searchBar = findViewById(R.id.search_view);
        searchBar.inflateMenu(R.menu.menu_main);

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled) {
                    searchBar.setHint(searchBar.getPlaceHolderText());
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if (text != null) {
                    if (text.length() > 0) getDataRepository().insertSearchRecords(text.toString());
                    pagerAdapter.updateWords(text.toString());
                    searchBar.setPlaceHolder(text);
                    searchBar.disableSearch();
                } else {
                    // back to default mode
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                searchBar.onItemClick(position, v);
            }

            @Override
            public void onItemDelete(int position, View v) {
                searchBar.onItemDelete(position, v);
                if (v.getTag() instanceof String) {
                    getDataRepository().deleteSearchRecords(v.getTag().toString());
                }
            }
        });
        getDataRepository().getAllSearchRecords(records -> searchBar.setLastSuggestions(records));
//        searchBar.setLastSuggestions(records);

//        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
//            @Override
//            public void onItemClick(int position, View v) {
//            }
//
//            @Override
//            public void onItemDelete(int position, View v) {
////                System.err.print(position);
//
//            }
//        });



//        searchBar.addTextChangeListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        searchBar.getMenu().setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    startSettings();
                break;

                case R.id.share_test:
                    OnekeyShare oks = new OnekeyShare();
                    oks.disableSSOWhenAuthorize();
                    oks.setTitle("分享测试");
                    oks.setText("咕鸽快来写代码");
                    oks.show(MainActivity.this);
                break;

                case R.id.news_test:
                    NewsApi.requestNews(new NewsApi.SearchParams()
                                    .setSize(20)
                                    .setWords("咕咕")
                                    .setCategory("科技")
                                    .setStartDate(new NewsDateTime(2019, 7, 1))
                                    .setEndDate(new NewsDateTime(2019, 7, 3)),
                            new NewsApi.NewsCallback() {
                                @Override
                                public void onReceived(List<NewsBean> newsBeanList) {
                                    if (newsBeanList.isEmpty()) {
                                        Toast.makeText(MainActivity.this, "莫得新闻了，等哈再来哈", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, newsBeanList.get(0).getTitle(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onException(Exception e) {
                                    if (e instanceof NetworkError) {
                                        Toast.makeText(MainActivity.this, "莫得网络啦，等下再来吧", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                    );
                break;

                case R.id.login:
                    Platform platform = ShareSDK.getPlatform(QQ.NAME);
                    if (!platform.isAuthValid()) {
                        Toast.makeText(this, "登陆过了", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(this, "userid: " + platform.getDb().getUserId(), Toast.LENGTH_SHORT).show();
//                    System.err.print();
//                    platform.SSOSetting(false);
                    ShareSDK.setActivity(this);
                    platform.setPlatformActionListener(new PlatformActionListener() {
                        @Override
                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "登录成功啦" + hashMap.toString(), Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onError(Platform platform, int i, Throwable throwable) {
                            Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel(Platform platform, int i) {
                            Toast.makeText(MainActivity.this, "取消登录", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    platform.authorize();
                    platform.showUser(null);
                break;
            }
            return true;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSearchBar();

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        configureNavigationView();
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
        if (searchBar.getPlaceHolderText().equals("")) {
            super.onBackPressed();
        } else {
            searchBar.setPlaceHolder("");
        }
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                startSettings();
//            break;
//
////            case R.id.action_search:
////                Toast.makeText(this, "search view open", Toast.LENGTH_SHORT).show();
////                viewPager.setCurrentItem(SectionsPagerAdapter.SEARCH_PAGE_POS);
////            break;
//
//            case R.id.share_test:
//                OnekeyShare oks = new OnekeyShare();
//                oks.disableSSOWhenAuthorize();
//                oks.setTitle("分享测试");
//                oks.setText("咕鸽快来写代码");
//                oks.show(this);
//            break;
//
//            case R.id.news_test:
//                NewsApi.requestNews(new NewsApi.SearchParams()
//                        .setSize(20)
//                        .setWords("咕咕")
//                        .setCategory("科技")
//                        .setStartDate(new NewsDateTime(2019, 7, 1))
//                        .setEndDate(new NewsDateTime(2019, 7, 3)),
//                    newsBeanList -> {
//                        if (newsBeanList.isEmpty()) {
//                            Toast.makeText(this, "莫得新闻了，等哈再来哈", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(this, newsBeanList.get(0).getTitle(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                );
//            break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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