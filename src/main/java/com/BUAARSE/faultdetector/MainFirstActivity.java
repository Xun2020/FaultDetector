package com.BUAARSE.faultdetector;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.Kwarg;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainFirstActivity extends AppCompatActivity {
    private Button backBt;
    private Button startBt;
    private Button stopBt;
    private Button deleteBt;
    private Button submitBt;
    private ShowWaveView imageWave;
    private Chronometer timer;
    private int userHz = 0;
    MyRecorder myRecorder;
    File dir,file,Rdir;
    String str,filePath;
    Boolean isRun = false;
    Boolean isReName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        startBt = findViewById(R.id.startBt);
        stopBt = findViewById(R.id.stopBt);
        deleteBt = findViewById(R.id.deleteBt);
        submitBt = findViewById(R.id.submitBt);
        imageWave = findViewById(R.id.image_wave);
        timer = findViewById(R.id.timer);
        backBt = findViewById(R.id.backBt);
        init();
        try {
            FileReader fd = new FileReader(new File(MainFirstActivity.this.getFilesDir().getAbsolutePath()+"/userData/mData.txt"));
            BufferedReader bf = new BufferedReader(fd);
            String line = bf.readLine();
            userHz = Integer.valueOf(line);
            bf.close();
        } catch (FileNotFoundException e) {
            System.out.println("W");
        } catch (IOException e) {
            System.out.println("W");
        }
        //??????????????????
        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deleteBt.isEnabled()){
                    deleteBt.performClick();
                }
                Intent back = new Intent(MainFirstActivity.this,MainActivity.class);
                startActivity(back);
                overridePendingTransition(R.animator.slide_left_in,R.animator.slide_right_out);
            }
        });
        //????????????
        startBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRun = true;
                isReName = false;
                backBt.setEnabled(false);
                //???????????????
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
                //????????????
                stopBt.setVisibility(View.VISIBLE);
                imageWave.setVisibility(View.VISIBLE);
                startBt.setVisibility(View.GONE);
                //????????????????????????????????????????????????
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MainFirstActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopBt.setEnabled(true);
                                deleteBt.setEnabled(true);
                                backBt.setEnabled(true);
                            }
                        });
                    }
                }).start();
                //????????????
                str = getApplicationContext().getFilesDir().getAbsolutePath()+ "/mData/" +  System.currentTimeMillis();
                //????????????????????????????????????????????????
                dir = new File(str);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                filePath = str + "/Record.pcm";
                //??????????????????
                file = new File(filePath);
                //??????????????????
                myRecorder = new MyRecorder(userHz,str);
                //????????????
                myRecorder.startMyRecord();
                //???????????????
                myRecorder.showWaveData(imageWave);
            }
        });
        //????????????
        stopBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRun = false;
                //????????????
                timer.stop();
                //????????????
                stopBt.setEnabled(false);
                stopBt.setVisibility(View.GONE);
                startBt.setVisibility(View.VISIBLE);
                startBt.setEnabled(false);
                submitBt.setEnabled(true);
                //????????????
                myRecorder.stopRecord();
                myRecorder.stopWave(imageWave);
                //????????????
                imageWave.setVisibility(View.GONE);
                showAdvice(new File(str).getName());
            }
        });
        //????????????
        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBt.setEnabled(false);
                startBt.setEnabled(true);
                if(!isRun){
                    submitBt.setEnabled(false);
                    if(isReName){
                        File f = new File(Rdir.getAbsolutePath()+"/Record.pcm");
                        f.delete();
                        Rdir.delete();
                    }else{
                        file.delete();
                        dir.delete();
                    }
                    Toast.makeText(MainFirstActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                }else{
                    timer.stop();
                    timer.setBase(SystemClock.elapsedRealtime());
                    submitBt.setEnabled(false);
                    stopBt.setEnabled(false);
                    stopBt.setVisibility(View.GONE);
                    startBt.setVisibility(View.VISIBLE);
                    startBt.setEnabled(true);
                    myRecorder.stopRecord();
                    myRecorder.stopWave(imageWave);
                    //????????????
                    imageWave.setVisibility(View.GONE);
                    file.delete();
                    dir.delete();
                    Toast.makeText(MainFirstActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                isRun = false;
            }
        });
        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBt.setEnabled(true);
                try {
                    initPython();
                    callPython(str+"/Record.pcm",str);
                }finally {
                    timer.setBase(SystemClock.elapsedRealtime());
                    deleteBt.setEnabled(false);
                    imageWave.setVisibility(View.GONE);
                    submitBt.setEnabled(false);
                    File file1 = new File(str);
                    if (file1.exists()) {
                        Intent result = new Intent(MainFirstActivity.this, ResultActivity1.class);
                        result.putExtra("filePath",str+"/Record.pcm");
                        result.putExtra("fileName",str);
                        result.putExtra("Hz",""+userHz);
                        startActivity(result);
                        MainFirstActivity.this.overridePendingTransition(R.animator.down_in, R.animator.on_out);
                    }
                }
            }
        });
    }
    private void init(){
        //        ?????????
        startBt.setVisibility(View.VISIBLE);
        startBt.setEnabled(true);
        stopBt.setVisibility(View.GONE);
        stopBt.setEnabled(false);
        deleteBt.setVisibility(View.VISIBLE);
        deleteBt.setEnabled(false);
        submitBt.setEnabled(false);
        imageWave.setVisibility(View.GONE);
    }

    //??????????????????
    private void showAdvice(String string){
        View dialog_view01 = View.inflate(MainFirstActivity.this,R.layout.dialog_view01,null);
        final TextView textView = dialog_view01.findViewById(R.id.dialog01_text);
        final EditText editText = dialog_view01.findViewById(R.id.editText);
        final Button Quit = dialog_view01.findViewById(R.id.quit);
        final Button Rsubmit = dialog_view01.findViewById(R.id.Rsubmit);

        textView.setText("?????????");
        editText.setText(string);
        editText.setSelectAllOnFocus(true);

        final AlertDialog dialog = new AlertDialog.Builder(MainFirstActivity.this,R.style.mydialog).create();
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(dialog_view01);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        lp.width = 5*width/6;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        //????????????
        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainFirstActivity.this, "??????????????????????????????",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        //????????????
        Rsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String RfileName = editText.getText().toString();
                if("".equals(RfileName)||RfileName==null){
                    Toast.makeText(MainFirstActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                }else{
                    str = MainFirstActivity.this.getFilesDir().getAbsolutePath()+ "/mData/" +RfileName;
                    Rdir = new File(str);
                    if (Rdir==null||Rdir.length()==0) {
                        dir.renameTo(Rdir);
                        isReName = true;
                        dialog.dismiss();
                        Toast.makeText(MainFirstActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainFirstActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&&deleteBt.isEnabled()) {
            deleteBt.performClick();
            Intent back = new Intent(MainFirstActivity.this,MainActivity.class);
            startActivity(back);
            overridePendingTransition(R.animator.slide_left_in,R.animator.slide_right_out);
            return false;
        }else {
            Intent back = new Intent(MainFirstActivity.this,MainActivity.class);
            startActivity(back);
            overridePendingTransition(R.animator.slide_left_in,R.animator.slide_right_out);
            return false;
        }
    }
    protected void initPython(){
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
    }

    protected void callPython(String filePath1,String fileName){
        Python py = Python.getInstance();
        py.getModule("demo1").callAttr("pic1",new Kwarg("readPath",filePath1),new Kwarg("savePath",fileName));
    }
}
