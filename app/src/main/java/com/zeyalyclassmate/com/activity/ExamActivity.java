package com.zeyalyclassmate.com.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zeyalyclassmate.com.R;
import com.zeyalyclassmate.com.adpter.DialogRadioBeanAdapter;
import com.zeyalyclassmate.com.adpter.ExamAdapter;
import com.zeyalyclassmate.com.bean.ExamBean;
import com.zeyalyclassmate.com.bean.SectionBean;
import com.zeyalyclassmate.com.databinding.ExamLayoutBinding;
import com.zeyalyclassmate.com.databinding.SyllabusLayoutBinding;
import com.zeyalyclassmate.com.fragment.HomeFragment;
import com.zeyalyclassmate.com.onItemClickListner.RecyclerTouchListener;
import com.zeyalyclassmate.com.session.Session;
import com.zeyalyclassmate.com.utils.TransistionAnimation;
import com.zeyalyclassmate.com.utils.URLHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ExamActivity extends AppCompatActivity implements View.OnClickListener {
    ExamLayoutBinding binding;
    ArrayList<ExamBean> examBeanArrayList;
    ExamAdapter examAdapter;
    Session session;
    int SectionId;
    ArrayList<SectionBean> SectionBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.exam_layout);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        TransistionAnimation transistionAnimation = new TransistionAnimation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(transistionAnimation.enterTransition());
            getWindow().setSharedElementReturnTransition(transistionAnimation.returnTransition());
        }

        intView();

    }

    @SuppressLint("RestrictedApi")
    private void intView() {
        session = new Session(ExamActivity.this);
        binding.syllabusrecyclerview.setLayoutManager(new LinearLayoutManager(ExamActivity.this, LinearLayoutManager.VERTICAL, false));
        //   binding.syllabusrecyclerview.setLayoutManager(new GridLayoutManager(SyllabusDetailActivity.this, 2));
        binding.title.setText(getResources().getText(R.string.exam));
        onItemClickListener();
        Section();
        if (MainActivity.userInfoArrayList.get(0).getRole_name().equalsIgnoreCase("Students")) {
            binding.fablayout.setVisibility(GONE);
            binding.filter.setVisibility(GONE);
        } else {
            binding.fablayout.setVisibility(VISIBLE);
            binding.filter.setVisibility(VISIBLE);
        }
    }

    private void onItemClickListener() {
        binding.syllabusrecyclerview.addOnItemTouchListener(new RecyclerTouchListener(ExamActivity.this, binding.syllabusrecyclerview,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent mainIntent = new Intent(ExamActivity.this, ExamDetailViewActivity.class);
                        mainIntent.putExtra("id", examBeanArrayList.get(position).getId());
                        mainIntent.putExtra("SectionId", SectionId);
                        startActivity(mainIntent);

                    }


                    @Override
                    public void onLongClick(View view, int position) {
                        //dialogAlert(position);

                    }
                }));
        binding.backArraow.setOnClickListener(this);
        binding.filter.setOnClickListener(this);
        binding.fab.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArraow:
                finish();
                break;
            case R.id.filter:
                DialogSection();
                break;
            case R.id.fab:
                Intent intent = new Intent(ExamActivity.this, ExamCreate.class);
                startActivity(intent);
                break;
        }


    }

    private void GetExamActivity(int sectionId) {
        examBeanArrayList = new ArrayList<>();
       /* String url = "";
        if (MainActivity.userInfoArrayList.get(0).getRole_name().equalsIgnoreCase("Students")) {
            url = URLHelper.exam + "?section_id=" + MainActivity.userInfoArrayList.get(0).getSection_id();
        } else {
            url = URLHelper.exam + "?staff_id=" + MainActivity.userInfoArrayList.get(0).getId();
        }*/
        AndroidNetworking.get(URLHelper.exam + "?section_id=" + sectionId)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", session.getKEYAuth())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("json---exam-------->" + response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ExamBean examBean = new ExamBean();
                                examBean.setId(jsonObject.getString("id"));
                                examBean.setSection_id(jsonObject.getString("section"));
                               // examBean.setStudent_id(jsonObject.getString("student_id"));
                             //   examBean.setTotal_marks(jsonObject.getString("total_marks"));
                              //  JSONObject exam = jsonObject.getJSONObject("exam");
                                examBean.setExam_date(jsonObject.getString("exam_date"));
                               // examBean.setExamid(exam.getString("id"));
                                examBean.setMax_mark(jsonObject.getString("max_mark"));
                                examBean.setMin_mark(jsonObject.getString("min_mark"));
                                examBean.setName(jsonObject.getString("name"));
                                JSONObject type = jsonObject.getJSONObject("type");
                                JSONObject subject = jsonObject.getJSONObject("subject");
                                examBean.setTypeid(type.getString("id"));
                                examBean.setIs_sys(type.getString("is_sys"));
                                examBean.setType_name(type.getString("name"));
                                examBean.setSubject_name(subject.getString("name"));


                                examBeanArrayList.add(examBean);
                            }

                            examAdapter = new ExamAdapter(ExamActivity.this, examBeanArrayList);
                            binding.syllabusrecyclerview.setAdapter(examAdapter);

                            if(jsonArray.length()>0){
                                binding.noDataLayout.setVisibility(View.GONE);
                            }else {
                                binding.noDataLayout.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        // do anything with response
                    }


                    @Override
                    public void onError(ANError error) {
                        // handle error
                        System.out.println(error.getErrorBody());
                        System.out.println(error.getErrorCode());
                        if (error.getErrorCode() == 403) {
                            session.onDestroy();


                        }
                    }
                });
    }

    private void DialogSection() {

        Dialog dialog = new Dialog(ExamActivity.this);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_list);

        TextView Heading = dialog.findViewById(R.id.Heading);
        Heading.setText(getResources().getText(R.string.SelectSection));

        RelativeLayout close_layout = dialog.findViewById(R.id.close_layout);
        RelativeLayout searchRL = dialog.findViewById(R.id.searchRL);
        searchRL.setVisibility(View.GONE);
        RecyclerView mainList = dialog.findViewById(R.id.mainList);
        mainList.setLayoutManager(new LinearLayoutManager(ExamActivity.this, LinearLayoutManager.VERTICAL, false));


        DialogRadioBeanAdapter sectionAdapter = new DialogRadioBeanAdapter(ExamActivity.this, SectionBeanArrayList);
        mainList.setAdapter(sectionAdapter);


        mainList.addOnItemTouchListener(new RecyclerTouchListener(ExamActivity.this, mainList,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        SectionId = SectionBeanArrayList.get(position).getSection_id();
                        GetExamActivity(SectionId);

                        for (int i = 0; i < SectionBeanArrayList.size(); i++) {
                            if (i == position) {
                                if (SectionBeanArrayList.get(i).getSelect()) {
                                    SectionBeanArrayList.get(i).setSelect(false);
                                } else {
                                    SectionBeanArrayList.get(i).setSelect(true);
                                }
                            } else {
                                SectionBeanArrayList.get(i).setSelect(false);
                            }
                        }
                        DialogRadioBeanAdapter sectionAdapter = new DialogRadioBeanAdapter(ExamActivity.this, SectionBeanArrayList);
                        mainList.setAdapter(sectionAdapter);
                        dialog.dismiss();


                    }


                    @Override
                    public void onLongClick(View view, int position) {
                        //dialogAlert(position);

                    }
                }));


        close_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();


    }

    private void Section() {
        SectionBeanArrayList = new ArrayList<>();

        AndroidNetworking.get(URLHelper.section)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", session.getKEYAuth())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                SectionBean sectionBean = new SectionBean();
                                sectionBean.setGrade_id(jsonObject.getInt("grade_id"));
                                sectionBean.setGrade_name(jsonObject.getString("grade_name"));
                                sectionBean.setSection_id(jsonObject.getInt("section_id"));
                                sectionBean.setSection_name(jsonObject.getString("section_name"));
                                sectionBean.setSection_visibility(jsonObject.getBoolean("section_visibility"));
                                if (i == 0)
                                    sectionBean.setSelect(true);
                                else
                                    sectionBean.setSelect(false);


                                SectionBeanArrayList.add(sectionBean);
                            }
                            SectionId = SectionBeanArrayList.get(0).getSection_id();
                            GetExamActivity(SectionId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        // do anything with response
                    }


                    @Override
                    public void onError(ANError error) {
                        // handle error
                        System.out.println(error.getErrorBody());
                        System.out.println(error.getErrorCode());
                        if (error.getErrorCode() == 403) {
                            session.onDestroy();


                        }
                    }
                });
    }
}

