package com.BUAARSE.faultdetector;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    //声明ViewPager
    private ViewPager mViewPager;
    //适配器
    private FragmentPagerAdapter mAdapter;
    //装载Fragment的集合
    private List<Fragment> mFragments;

    //3个Tab对应的布局
    private LinearLayout mTab1;
    private LinearLayout mTab2;
    private LinearLayout mTab3;

    //3个Tab对应的ImageButton
    private ImageButton mImg1;
    private ImageButton mImg2;
    private ImageButton mImg3;
    //3个Tab对应的Text
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    //TopText
    private TextView mTopText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initViews();//初始化控件
        initEvents();//初始化事件
        initDatas();//初始化数据
        mViewPager.setCurrentItem(1,false);
        init();
        File mDataFile = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/mData");
        if(!mDataFile.exists()){
            mDataFile.mkdirs();
        }
    }

    private void init(){
        if(!allResult()){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("应用需要您的录音和存储权限，请到设置-权限管理中授权。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "您没有获得权限，此功能不能正常使用", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
    }

    private void initDatas() {
        mFragments = new ArrayList<>();
        //将三个Fragment加入集合中
        mFragments.add(new PageFragment_1());
        mFragments.add(new PageFragment_2());
        mFragments.add(new PageFragment_3());

        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {//从集合中获取对应位置的Fragment
                return mFragments.get(position);
            }

            @Override
            public int getCount() {//获取集合中Fragment的总数
                return mFragments.size();
            }
        };
        //不要忘记设置ViewPager的适配器
        mViewPager.setAdapter(mAdapter);
        //设置ViewPager的切换监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                //设置position对应的集合中的Fragment
                mViewPager.setCurrentItem(position);
                resetImgs();
                selectTab(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initEvents() {
        //设置3个Tab的点击事件
        mTab1.setOnClickListener(this);
        mTab2.setOnClickListener(this);
        mTab3.setOnClickListener(this);

    }

    //初始化控件
    private void initViews() {
        mViewPager =  findViewById(R.id.id_content);

        mTab1 =  findViewById(R.id.id_tab1);
        mTab2 =  findViewById(R.id.id_tab2);
        mTab3 =  findViewById(R.id.id_tab3);

        mImg1 =  findViewById(R.id.id_tab_img1);
        mImg2 =  findViewById(R.id.id_tab_img2);
        mImg3 =  findViewById(R.id.id_tab_img3);

        mText1 = findViewById(R.id.tab_text1);
        mText2 = findViewById(R.id.tab_text2);
        mText3 = findViewById(R.id.tab_text3);
        mTopText = findViewById(R.id.top_text);

    }

    @Override
    public void onClick(View v) {
        //先将3个ImageButton置为灰色
        resetImgs();

        //根据点击的Tab切换不同的页面及设置对应的ImageButton
        switch (v.getId()) {
            case R.id.id_tab1:
                selectTab(0);
                break;
            case R.id.id_tab2:
                selectTab(1);
                break;
            case R.id.id_tab3:
                selectTab(2);
                break;
        }
    }

    private void selectTab(int i) {
        //根据点击的Tab设置对应的ImageButton颜色
        switch (i) {
            case 0:
                mImg1.setImageResource(R.mipmap.history_2);
                mText1.setTextColor(0xff288df8);
                mTopText.setText("History");
                break;
            case 1:
                mImg2.setImageResource(R.mipmap.home_2);
                mText2.setTextColor(0xff288df8);
                mTopText.setText("Home");
                break;
            case 2:
                mImg3.setImageResource(R.mipmap.shezhi_2);
                mText3.setTextColor(0xff288df8);
                mTopText.setText("Setting");
                break;
        }
        //设置当前点击的Tab所对应的页面
        mViewPager.setCurrentItem(i);
    }

    //将3个ImageButton设置为灰色
    private void resetImgs() {
        mImg1.setImageResource(R.mipmap.history_1);
        mImg2.setImageResource(R.mipmap.home_1);
        mImg3.setImageResource(R.mipmap.shezhi_1);
        mText1.setTextColor(0xff2c2c2c);
        mText2.setTextColor(0xff2c2c2c);
        mText3.setTextColor(0xff2c2c2c);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();        //调用双击退出函数
        }
        return false;
    }
    static Boolean isExit = false;
    private void exitBy2Click() {
        Timer tExit;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            this.finish();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            System.exit(0);
        }
    }

    //权限查询
    protected boolean allResult() {
        int[] p = new int[3];
        p[0] = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);
        p[1] = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
        p[2] = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        for (int i = 0; i < 3; i++) {
            if(p[i] != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
