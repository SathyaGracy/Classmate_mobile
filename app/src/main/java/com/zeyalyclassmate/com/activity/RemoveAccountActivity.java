package com.zeyalyclassmate.com.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zeyalyclassmate.com.Database.DatabaseHelper;
import com.zeyalyclassmate.com.Database.UserInfoDB;
import com.zeyalyclassmate.com.R;
import com.zeyalyclassmate.com.databinding.RemoveAccountBinding;
import com.zeyalyclassmate.com.session.Session;
import com.zeyalyclassmate.com.utils.TransistionAnimation;
import com.zeyalyclassmate.com.utils.URLHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoveAccountActivity extends AppCompatActivity implements View.OnClickListener {
    RemoveAccountBinding binding;
    String id;

    Session session;
    DatabaseHelper databaseHelper;
    UserInfoDB userInfoDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.remove_account);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        TransistionAnimation transistionAnimation = new TransistionAnimation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(transistionAnimation.enterTransition());
            getWindow().setSharedElementReturnTransition(transistionAnimation.returnTransition());
        }
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        intView();

    }

    @SuppressLint("RestrictedApi")
    private void intView() {
        session = new Session(RemoveAccountActivity.this);
        binding.backArraow.setOnClickListener(this::onClick);
        binding.removeAccount.setOnClickListener(this::onClick);
        databaseHelper = new DatabaseHelper(this);
         userInfoDB = databaseHelper.getUser(id);
        binding.nameTxt.setText(userInfoDB.getName());
        binding.title.setText("Hey " + userInfoDB.getName());
      //  Picasso.with(RemoveAccountActivity.this).load(response.getString("profile_image_url")).into(binding.profile);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArraow:
                finish();
                break;
            case R.id.remove_account:
                databaseHelper.delete(id);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
        }

    }


}
