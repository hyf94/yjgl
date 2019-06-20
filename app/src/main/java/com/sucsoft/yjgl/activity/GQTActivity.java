package com.sucsoft.yjgl.activity;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.gqt.bean.PttGroup;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.GroupEngine;
import com.sucsoft.yjgl.R;
import com.sucsoft.yjgl.gqt.CallFragment;
import com.sucsoft.yjgl.gqt.TalkBackFragment;
import com.sucsoft.yjgl.util.gqt.GQTRegister;

import java.util.ArrayList;
import java.util.List;

public class GQTActivity extends AppCompatActivity {

    public GQTRegister gqtRegister;
    String IP = "39.106.217.160";
    int PORT = 7080;
    public static final String  CUSTOM_GROUP_ACTION_RESULT_STATE = "action_CustomGroupResultState";

    TabLayout mytab;
    ViewPager viewPager;
    private static GroupEngine groupEngine = GQTHelper.getInstance().getGroupEngine();    //获取对讲模块实例
    private static final int GroupStatusChanged = 1;
    private static final int GroupChanged = 2;
    private static final int GroupListChanged = 3;
    private static final int GroupMemChanged = 4;
    private static final int GroupIncoming = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gqt);
//        PttGroup curGrp = groupEngine.getCurGrp();
//        Toast.makeText(this,"GrpName1: -----"+curGrp.getGrpName(),Toast.LENGTH_SHORT).show();

//        gqtRegister = new GQTRegister(this);
//        gqtRegister.init("800011", "800011", IP, PORT);
        initView ();
    }

    private void initView () {
        mytab = findViewById(R.id.gqt_tl);
        viewPager = findViewById(R.id.gqt_vp);
        final List<String> mTitle;
        final List<Fragment> mFragment;
        mTitle = new ArrayList<>();
        mTitle.add("对讲");
        mTitle.add("通话");
        mFragment = new ArrayList<>();
        mFragment.add(new TalkBackFragment());
        mFragment.add(new CallFragment());

        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragment.get(i);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        };

        mytab.addTab(mytab.newTab().setText("对讲").setIcon(R.mipmap.ic_launcher));
        mytab.addTab(mytab.newTab().setText("通话").setIcon(R.mipmap.ic_launcher));

        viewPager.setAdapter(mAdapter);
        mytab.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        mytab.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }

}
