package com.java.luolingxiao.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    private LinearLayout authorizedContainer, unauthorizedContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        username = view.findViewById(R.id.username);
        userPortrait = view.findViewById(R.id.user_portrait);
        authorizedContainer = view.findViewById(R.id.container_authorized);
        unauthorizedContainer = view.findViewById(R.id.container_unauthorized);

        if (UserApi.isAuthorized()) {
            authorizedContainer.setVisibility(View.VISIBLE);
            unauthorizedContainer.setVisibility(View.GONE);
        } else {
            authorizedContainer.setVisibility(View.GONE);
            unauthorizedContainer.setVisibility(View.VISIBLE);
        }

        authorizedContainer.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApi.logout();
                username.setText(getString(R.string.unauthorized));
                userPortrait.setImageDrawable(getResources().getDrawable(R.drawable.unknown, null));
                authorizedContainer.setVisibility(View.GONE);
                unauthorizedContainer.setVisibility(View.VISIBLE);
            }
        });

        unauthorizedContainer.findViewById(R.id.login).setOnClickListener(v -> {
            UserApi.authorize(getActivity(), new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
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
                    });
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {

                }

                @Override
                public void onCancel(Platform platform, int i) {

                }
            });
        });



        return view;
    }
    public MyFragment() {}

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }
}
