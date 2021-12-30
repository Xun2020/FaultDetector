package com.BUAARSE.faultdetector;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;

public class PageFragment_1 extends Fragment {

    public Button quit;
    public boolean isQuitBtVisible = false;
    private ImageButton rename;
    private ImageButton delete;
    private TextView textView;
    private ListView listView;
    private LinearLayout linearLayout;
    ArrayList<String> fileName = new ArrayList<>();
    String dirPath;

    //创建view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        rename = getActivity().findViewById(R.id.id_history_img1);
        delete = getActivity().findViewById(R.id.id_history_img2);
        textView = getActivity().findViewById(R.id.text);
        listView = getActivity().findViewById(R.id.history_list);
        linearLayout = getActivity().findViewById(R.id.bottom_history);
        quit = getActivity().findViewById(R.id.quitBt);
        quit.setEnabled(false);
        init();
        //listView
        final MyAdapter myAdapter = new MyAdapter(getActivity(),fileName,getContext().getFilesDir().getAbsolutePath());
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent act = new Intent(getContext(), ResultActivity1.class);
                act.putExtra("fileName",getContext().getFilesDir().getAbsolutePath()+"/mData/"+adapterView.getItemAtPosition(i).toString());
                startActivity(act);
                getActivity().overridePendingTransition(R.animator.down_in, R.animator.on_out);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                myAdapter.setItemMultiCheckable(true);
                myAdapter.notifyDataSetChanged();
                linearLayout.setVisibility(View.VISIBLE);
                quit.setVisibility(View.VISIBLE);
                quit.setEnabled(true);
                isQuitBtVisible = true;
                return true;
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myAdapter.setItemMultiCheckable(false);
                myAdapter.notifyDataSetChanged();
                linearLayout.setVisibility(View.GONE);
                quit.setVisibility(View.GONE);
                quit.setEnabled(false);
                isQuitBtVisible = false;
            }
        });
        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> Rdata = new ArrayList<>();
                Rdata.addAll(myAdapter.getData());
                if(Rdata.size()!=1){
                    Toast.makeText(getActivity(),"请选择一个文件",Toast.LENGTH_SHORT).show();
                }else{
                    showAdvice(Rdata.get(0),myAdapter);
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myAdapter.getData().size()==0){
                    Toast.makeText(getActivity(),"请先选择文件",Toast.LENGTH_SHORT).show();
                }else{
                    for(int i =0;i<myAdapter.getData().size();i++){
//                        System.out.println(getApplicationContext().getFilesDir().getAbsolutePath()+myAdapter.getData().get(i));
                        File deleteDir = new File(getContext().getFilesDir().getAbsolutePath()+"/mData/"+myAdapter.getData().get(i));
                        File[] deleteFiles = deleteDir.listFiles();
                        if(deleteFiles!=null&&deleteFiles.length!=0){
                            for(int j=0;j<deleteFiles.length;j++){
                                deleteFiles[j].delete();
                            }
                        }
                        deleteDir.delete();
                    }
                    init();
                    myAdapter.setListText(fileName);
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisible){
        super.setUserVisibleHint(isVisible);
        if(!isVisible&&isQuitBtVisible){
            quit.performClick();
        }
    }

    public void init(){
        //检测文件夹里面是否有文件夹
        fileName.clear();
        dirPath = getContext().getFilesDir().getAbsolutePath()+"/mData";
        File file = new File(dirPath);
        if(!file.exists()){
            file.mkdirs();
        }
        File[] files = file.listFiles();
        if(file==null||files.length==0){
            textView.setVisibility(View.VISIBLE);
            textView.setText("没有检测记录");
        }else{
            textView.setVisibility(View.GONE);
            for(int i=0;i<files.length;i++){
                fileName.add(files[i].getName());
            }
        }
    }

    //保存时重命名
    private void showAdvice(final String usedfilename, final MyAdapter myAdapter){
        View dialog_view01 = View.inflate(getActivity(),R.layout.dialog_view01,null);
        final TextView textView = dialog_view01.findViewById(R.id.dialog01_text);
        final EditText editText = dialog_view01.findViewById(R.id.editText);
        final Button Quit = dialog_view01.findViewById(R.id.quit);
        final Button Rsubmit = dialog_view01.findViewById(R.id.Rsubmit);

        textView.setText("重命名");
        editText.setText(usedfilename);
        editText.setSelectAllOnFocus(true);

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
                String RfileName = editText.getText().toString();
                if("".equals(RfileName)||RfileName==null){
                    Toast.makeText(getActivity(), "文件名不能为空！", Toast.LENGTH_SHORT).show();
                }else{
                    String str = getContext().getFilesDir().getAbsolutePath();
                    File dir = new File(str+"/mData/"+usedfilename);
                    File Rdir = new File(str+ "/mData/" +RfileName);
                    if (Rdir==null||Rdir.length()==0) {
                        dir.renameTo(Rdir);
                        dialog.dismiss();
                        init();
                        myAdapter.setListText(fileName);
                    }else{
                        Toast.makeText(getActivity(), "文件名已经存在！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
