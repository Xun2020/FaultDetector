package com.BUAARSE.faultdetector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ResultActivity2 extends AppCompatActivity {
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
//    private ImageView imageView4;
//    private ImageView imageView5;
//    private ImageView imageView6;
    private ImageView imageR;
    private TextView textView1;
    private TextView textView2;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private LinearLayout view1;
    private LinearLayout view2;
    private LinearLayout view3;
    private Button backBt;
    private boolean isChecked1 = false;
    private boolean isChecked2 = false;
    private boolean isChecked3 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);
        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        imageView3 = findViewById(R.id.image3);
//        imageView4 = findViewById(R.id.image4);
//        imageView5 = findViewById(R.id.image5);
//        imageView6 = findViewById(R.id.image6);
        imageR = findViewById(R.id.imageR);
        textView1 = findViewById(R.id.hz_text);
        textView2 = findViewById(R.id.textR);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        backBt = findViewById(R.id.backBt);

        Intent intent = getIntent();
        final String data = intent.getStringExtra("fileName");
        imageView1.setImageURI(Uri.fromFile(new File(data+"/"+"dataPic2.png")));
        imageView2.setImageURI(Uri.fromFile(new File(data+"/"+"dataPic3.png")));
        imageView3.setImageURI(Uri.fromFile(new File(data+"/"+"dataPic4.png")));
//        imageView4.setImageURI(Uri.fromFile(new File(data+"/"+"dataPic5.png")));
//        imageView5.setImageURI(Uri.fromFile(new File(data+"/"+"dataPic6.png")));
//        imageView6.setImageURI(Uri.fromFile(new File(data+"/"+"dataPic7.png")));

        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        view3.setVisibility(View.GONE);

        init(data);

        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finish();
            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isChecked1){
                    view1.setVisibility(View.VISIBLE);
                    isChecked1 = true;
                    bt1.setBackgroundResource(R.drawable.shouhui);
                }else{
                    view1.setVisibility(View.GONE);
                    isChecked1 = false;
                    bt1.setBackgroundResource(R.drawable.zhankai);
                }
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isChecked2){
                    view2.setVisibility(View.VISIBLE);
                    isChecked2 = true;
                    bt2.setBackgroundResource(R.drawable.shouhui);
                }else{
                    view2.setVisibility(View.GONE);
                    isChecked2 = false;
                    bt2.setBackgroundResource(R.drawable.zhankai);
                }
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isChecked3){
                    view3.setVisibility(View.VISIBLE);
                    isChecked3 = true;
                    bt3.setBackgroundResource(R.drawable.shouhui);
                }else{
                    view3.setVisibility(View.GONE);
                    isChecked3 = false;
                    bt3.setBackgroundResource(R.drawable.zhankai);
                }
            }
        });
    }
    public void init(String filePath){
        String[] strs1 = new String[4];
        int n = 0;
        String[] strs = new String[5];
        File file_10 = new File(filePath+"/"+"data10.txt");
        File file_9 = new File(filePath+"/"+"data9.txt");
        try {
            FileReader fd1 = new FileReader(file_10);
            BufferedReader bf1 = new BufferedReader(fd1);
            FileReader fd2 = new FileReader(file_9);
            BufferedReader bf2 = new BufferedReader(fd2);
            String line;
            int i = 0;
            while ((line = bf2.readLine())!=null){
                strs1[i] = line;
                i++;
            }
            n = Integer.parseInt(strs1[1]);
            int j = 0;
            while ((line = bf1.readLine())!=null){
                strs[j] = line;
                j++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Double a = Double.parseDouble(strs[0]);
        if(a==-1){
            textView1.setText("None");
            imageR.setImageResource(R.drawable.image2);
            textView2.setText("Normal");
        }else{
            textView1.setText(strs[0]+"Hz");
            imageR.setImageResource(R.drawable.image1);
            if(strs1[0].equals("6205SKF")){
                double a1 = 0.0902*n;
                double a2 = 0.0598*n;
                double a3 = 0.0785*n;
                double a4 = 0.0067*n;
                if(a>0.97*a1&&a<1.03*a1){
                    textView2.setText("Failure, bearing inner ring failure.");
                }else if(a>0.97*a2&&a<1.03*a2){
                    textView2.setText("Failure, bearing outer ring failure.");
                }else if(a>0.97*a3&&a<1.03*a3){
                    textView2.setText("Failure, bearing rolling body failure.");
                }else if(a>0.97*a4&&a<1.03*a4){
                    textView2.setText("Failure, bearing cage failure.");
                }else{
                    textView2.setText("Failure, but not in the bearing.");
                }
            }else if(strs1[0].equals("6202SKF")){
                double a1 = 0.0825*n;
                double a2 = 0.0508*n;
                double a3 = 0.0662*n;
                double a4 = 0.0063*n;
                if(a>0.97*a1&&a<1.03*a1){
                    textView2.setText("Failure, bearing inner ring failure.");
                }else if(a>0.97*a2&&a<1.03*a2){
                    textView2.setText("Failure, bearing outer ring failure.");
                }else if(a>0.97*a3&&a<1.03*a3){
                    textView2.setText("Failure, bearing rolling body failure.");
                }else if(a>0.97*a4&&a<1.03*a4){
                    textView2.setText("Failure, bearing cage failure.");
                }else{
                    textView2.setText("Failure, but not in the bearing.");
                }
            }else{
                textView2.setText("Failure, the parts do not exist");
            }

        }
        text1.setText(strs[1]);
        text2.setText(strs[2]);
        text3.setText(strs[3]);
        text4.setText(strs[4]);
    }
}
