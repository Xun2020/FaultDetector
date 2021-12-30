package com.BUAARSE.faultdetector;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PageFragment_3 extends Fragment {
    private TextView editText;
    private LinearLayout layout1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        editText = getActivity().findViewById(R.id.f3_text1);
        layout1 = getActivity().findViewById(R.id.f3_layout1);
        String path = getContext().getFilesDir().getAbsolutePath()+"/userData";
        String filePath = path+"/mData.txt";
        File dir = new File(path);
        File file = new File(filePath);
        if(!dir.exists()){
            dir.mkdirs();
            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("44100");
                editText.setText("44100");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                FileReader fd = new FileReader(file);
                BufferedReader bf = new BufferedReader(fd);
                String line;
                while ((line = bf.readLine())!=null){
                    editText.setText(line);
                }
                bf.close();
            } catch (FileNotFoundException e) {
                System.out.println("W");
            } catch (IOException e) {
                System.out.println("W");
            }
        }

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAdvice1(editText);
            }
        });
    }
//
    private void showAdvice1(final TextView text){
        View dialog_view01 = View.inflate(getActivity(),R.layout.dialog_view01,null);
        final TextView textView = dialog_view01.findViewById(R.id.dialog01_text);
        final EditText editText = dialog_view01.findViewById(R.id.editText);
        final Button Quit = dialog_view01.findViewById(R.id.quit);
        final Button Rsubmit = dialog_view01.findViewById(R.id.Rsubmit);

        textView.setText("录音频率");
        editText.setText(text.getText());
        editText.setSelectAllOnFocus(true);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity(),R.style.mydialog).create();
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
                    Toast.makeText(getActivity(),"请输入大于5000小于44100的整数",Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        int data = Integer.parseInt(string);
                        if(data<5000||data>44100){
                            Toast.makeText(getActivity(),"请输入大于5000小于44100的整数",Toast.LENGTH_SHORT).show();
                            editText.setText("");
                        }else {
                            text.setText(string);
                            try {
                                File file = new File(getContext().getFilesDir().getAbsolutePath()+"/userData/mData.txt");
                                file.createNewFile();
                                FileWriter fw = new FileWriter(file);
                                BufferedWriter bw = new BufferedWriter(fw);
                                bw.write(string);
                                bw.flush();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    }catch (NumberFormatException e){
                        Toast.makeText(getActivity(),"请输入大于5000小于44100的整数",Toast.LENGTH_SHORT).show();
                        editText.setText("");
                    }
                }
            }
        });
    }
}