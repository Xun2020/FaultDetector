package com.BUAARSE.faultdetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.Kwarg;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;

public class MainSecondActivity extends AppCompatActivity {
    private Button backBt;
    private TextView textView1;
    private TextView textView2;
    private Button button1;
    private Button button2;
    private Button button3;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        textView1 = findViewById(R.id.txt1);
        textView2 = findViewById(R.id.txt2);
        button1 = findViewById(R.id.import_Bt1);
        button2 = findViewById(R.id.import_Bt2);
        button3 = findViewById(R.id.import_Bt3);
        backBt = findViewById(R.id.backBt);
    //设置退出函数
        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(MainSecondActivity.this,MainActivity.class);
                startActivity(back);
                overridePendingTransition(R.animator.slide_left_in,R.animator.slide_right_out);
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cachePath = MainSecondActivity.this.getCacheDir().getPath();
                File dir = new File(cachePath+"/documents");
                if(dir.exists()){
                    File[] files = dir.listFiles();
                    if(files.length!=0){
                        for(int i=0;i<files.length;i++){
                            files[i].delete();
                        }
                    }
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdvice1(textView2);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = textView1.getText().toString();
                String str2 = textView2.getText().toString();
                if(str==null||str.equals("")||str2==null||str2.equals("")){
                    Toast.makeText(MainSecondActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
                }else{
                    showAdvice(path, str.substring(0,str.lastIndexOf(".")),str2);
                }
            }
        });
    }

    private void showAdvice1(final TextView text){
        View dialog_view01 = View.inflate(MainSecondActivity.this,R.layout.dialog_view01,null);
        final TextView textView = dialog_view01.findViewById(R.id.dialog01_text);
        final EditText editText = dialog_view01.findViewById(R.id.editText);
        final Button Quit = dialog_view01.findViewById(R.id.quit);
        final Button Rsubmit = dialog_view01.findViewById(R.id.Rsubmit);

        textView.setText("采样频率");
        editText.setText("44100");
        editText.setSelectAllOnFocus(true);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        final AlertDialog dialog = new AlertDialog.Builder(MainSecondActivity.this,R.style.mydialog).create();
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

        //取消退出
        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //确定保存
        Rsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = editText.getText().toString();
                if("".equals(string)||string==null){
                    Toast.makeText(MainSecondActivity.this,"请输入大于5000小于44100的整数",Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        int data = Integer.parseInt(string);
                        if(data<5000||data>44100){
                            Toast.makeText(MainSecondActivity.this,"请输入大于5000小于44100的整数",Toast.LENGTH_SHORT).show();
                            editText.setText("");
                        }else {
                            text.setText(string);
                            dialog.dismiss();
                        }
                    }catch (NumberFormatException e){
                        Toast.makeText(MainSecondActivity.this,"请输入大于5000小于44100的整数",Toast.LENGTH_SHORT).show();
                        editText.setText("");
                    }
                }
            }
        });
    }

    //保存时重命名
    private void showAdvice(final String filepath, String string,final String Hz){
        View dialog_view01 = View.inflate(this,R.layout.dialog_view01,null);
        final TextView textView = dialog_view01.findViewById(R.id.dialog01_text);
        final EditText editText = dialog_view01.findViewById(R.id.editText);
        final Button Quit = dialog_view01.findViewById(R.id.quit);
        final Button Rsubmit = dialog_view01.findViewById(R.id.Rsubmit);

        textView.setText("文件命名");
        editText.setText(string);
        editText.setSelectAllOnFocus(true);

        final AlertDialog dialog = new AlertDialog.Builder(this,R.style.mydialog).create();
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

        //取消退出
        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //确定保存
        Rsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = editText.getText().toString();
                if("".equals(fileName)||fileName==null){
                    Toast.makeText(MainSecondActivity.this, "文件名不能为空！", Toast.LENGTH_SHORT).show();
                }else{
                    String str = getApplicationContext().getFilesDir().getAbsolutePath()+ "/mData/" +fileName;
                    File dir = new File(str);
                    if (dir==null||dir.length()==0) {
                        dir.mkdirs();
                        dialog.dismiss();
                        Intent result = new Intent(MainSecondActivity.this, ResultActivity1.class);
                        result.putExtra("filePath",filepath);
                        result.putExtra("fileName",str);
                        result.putExtra("Hz",Hz);
                        try {
                            initPython();
                            callPython(filepath,str);
                        }finally {
                            startActivity(result);
                        }
                    }else{
                        Toast.makeText(MainSecondActivity.this, "文件名已经存在！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==1){
                Uri uri = data.getData();
                String filePath = PickUtils.getPath(this,uri);
                System.out.println("文件地址"+filePath);
                this.path = filePath;
                if(filePath.endsWith(".mat")||filePath.endsWith(".pcm")){
                    textView1.setText(filePath.substring(filePath.lastIndexOf("/")+1,filePath.length()));
                }else{
                    textView1.setText("");
                    Toast.makeText(MainSecondActivity.this,"目前只支持pcm、mat格式文件",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected void initPython(){
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
    }

    protected void callPython(String filePath,String fileName){
        Python py = Python.getInstance();
        py.getModule("demo1").callAttr("pic1",new Kwarg("readPath",filePath),new Kwarg("savePath",fileName));
    }

}
