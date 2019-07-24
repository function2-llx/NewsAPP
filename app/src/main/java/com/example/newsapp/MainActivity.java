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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.newsapp.Event.NightModeChangeEvent;
import com.ortiz.touchview.TouchImageView;
import com.wildma.pictureselector.PictureSelector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends DeFaultActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private View navigationHeader;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        configureNavigationView();

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
//            recreate();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
