package com.java.luolingxiao.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.java.luolingxiao.FavoriteActivity;
import com.java.luolingxiao.R;
import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.api.UserApi;

import java.util.HashMap;
import java.util.Objects;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyFragment extends DefaultFragment {
    private TextView username;
    private CircleImageView userPortrait;

    private LinearLayout authorizedContainer, unauthorizedContainer, localContainer;

    private void switchLayout() {
        if (UserApi.isAuthorized()) {
            authorizedContainer.setVisibility(View.VISIBLE);
            unauthorizedContainer.setVisibility(View.GONE);
            username.setText(UserApi.getUsername());
            NewsApi.requestImage(UserApi.getPortraitUrl(), new NewsApi.ImageCallback() {
                @Override
                public void onReceived(Bitmap bitmap) {
                    userPortrait.setImageBitmap(bitmap);
                }

                @Override
                public void onException(Exception e) {

                }
            });

        } else {
            authorizedContainer.setVisibility(View.GONE);
            unauthorizedContainer.setVisibility(View.VISIBLE);
            username.setText(getString(R.string.unauthorized));
            userPortrait.setImageDrawable(getResources().getDrawable(R.drawable.unknown, null));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        username = view.findViewById(R.id.username);
        userPortrait = view.findViewById(R.id.user_portrait);
        authorizedContainer = view.findViewById(R.id.container_authorized);
        unauthorizedContainer = view.findViewById(R.id.container_unauthorized);
        localContainer = view.findViewById(R.id.local_container);

        switchLayout();

        authorizedContainer.findViewById(R.id.logout).setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("确定要退出当前账号吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    UserApi.logout();
                    switchLayout();
                }).setNegativeButton("取消", null)
                .show());

        unauthorizedContainer.findViewById(R.id.login).setOnClickListener(v -> {
            UserApi.authorize(getActivity(), new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> switchLayout());
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onCancel(Platform platform, int i) {

                }
            });
        });

        localContainer.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FavoriteActivity.class);
            intent.putExtra("title", "本地收藏");
            intent.putExtra("local", true);
            startActivity(intent);
        });

        return view;
    }
    public MyFragment() {}

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }
}
