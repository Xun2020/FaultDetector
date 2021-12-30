package com.BUAARSE.faultdetector;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.Kwarg;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ResultActivity1 extends AppCompatActivity {
    private Button backBt;
    private Button Bt1;
    private ImageView imageView;
    private TextView textView;
    private TextView textView1;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result1);

        Intent intent = getIntent();
        final String data = intent.getStringExtra("fileName");

        backBt = findViewById(R.id.backBt);
        Bt1 = findViewById(R.id.toResult2);
        textView = findViewById(R.id.file_name);
        editText1 = findViewById(R.id.r1_text1);
        editText2 = findViewById(R.id.r1_text2);
        editText3 = findViewById(R.id.r1_text3);
        editText4 = findViewById(R.id.r1_text4);

        textView.setText(new File(data).getName());
        textView1 = findViewById(R.id.file_hz);
        Bt1.setEnabled(false);
        imageView = findViewById(R.id.image_result1);
        imageView.setImageURI(Uri.fromFile(new File(data+"/"+"dataPic1.png")));

        final File file_2= new File(data+"/"+"dataPic2.png");
        final File file_8= new File(data+"/"+"data8.txt");
        final File file_9= new File(data+"/"+"data9.txt");
        if(file_9.exists()){
            editText1.setEnabled(false);
            editText2.setEnabled(false);
            editText3.setEnabled(false);
            editText4.setEnabled(false);
        }
        if(!file_2.exists()){
            final String mDataPath = intent.getStringExtra("filePath");
            final int mHz = Integer.valueOf(intent.getStringExtra("Hz"));
            textView1.setText(""+mHz);
            try {
                file_8.createNewFile();
                FileWriter fw = new FileWriter(file_8);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.append(""+mHz);
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            init(mDataPath,data,mHz);
        }else{
            Bt1.setEnabled(true);
            try {
                FileReader fd1 = new FileReader(file_8);
                BufferedReader bf1 = new BufferedReader(fd1);
                FileReader fd2 = new FileReader(file_9);
                BufferedReader bf2 = new BufferedReader(fd2);
                String line;
                int i = 1;
                while ((line = bf1.readLine())!=null){
                    textView1.setText(line);
                }
                while ((line = bf2.readLine())!=null){
                    switch (i){
                        case 1: editText1.setText(line);
                        case 2: editText2.setText(line);
                        case 3: editText3.setText(line);
                        case 4: editText4.setText(line);
                        default:break;
                    }
                    i++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(file_2.exists()){
                    finish();
                }else {
                    Toast.makeText(ResultActivity1.this,"计算中不可退出！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str1,str2,str3,str4;
                str1 = editText1.getText().toString();
                str2 = editText2.getText().toString();
                str3 = editText3.getText().toString();
                str4 = editText4.getText().toString();
                if(!file_9.exists()){
                    if(str1.equals("")||str2.equals("")||str3.equals("")||str4.equals("")){
                        Toast.makeText(ResultActivity1.this,"请输入参数",Toast.LENGTH_SHORT).show();
                    }else{
                        try {
                            file_9.createNewFile();
                            FileWriter bw = new FileWriter(file_9,true);
                            bw.append(""+str1+"\n");
                            bw.append(""+str2+"\n");
                            bw.append(""+str3+"\n");
                            bw.append(""+str4+"\n");
                            bw.flush();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        editText1.setEnabled(false);
                        editText2.setEnabled(false);
                        editText3.setEnabled(false);
                        editText4.setEnabled(false);
                        Intent result = new Intent(ResultActivity1.this, ResultActivity2.class);
                        result.putExtra("fileName",data);
                        startActivity(result);
                        ResultActivity1.this.overridePendingTransition(R.animator.down_in, R.animator.on_out);
                    }
                }else{
                    Intent result = new Intent(ResultActivity1.this, ResultActivity2.class);
                    result.putExtra("fileName",data);
                    startActivity(result);
                    ResultActivity1.this.overridePendingTransition(R.animator.down_in, R.animator.on_out);
                }
            }
        });

    }

    private  void init(final String readPath,final String data1,final int HZ){
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Bt1.setText("running");
                        Bt1.setBackgroundColor(Color.parseColor("#ffa001"));
                        initPython();
                        callPython(readPath,data1,HZ);
                    }finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bt1.setText("result");
                                Bt1.setBackgroundColor(Color.parseColor("#21d86d"));
                                Bt1.setEnabled(true);
                            }
                        });
                    }
                }
         }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backBt.performClick();
            return false;
        }else {
            return false;
        }
    }

    protected void initPython(){
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
    }

    protected void callPython(String filePath,String fileName,int fs){
        Python py = Python.getInstance();
        py.getModule("getResult_V3").callAttr("run",new Kwarg("readPath",filePath),
                new Kwarg("savePath",fileName),new Kwarg("fs",fs));
    }
}
