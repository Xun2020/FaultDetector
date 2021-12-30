package com.BUAARSE.faultdetector;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private List<String> listText;
    private ArrayList<String> selected = new ArrayList<>();
    private Context context;
    private Map<Integer,Boolean> map= new HashMap<>();
    private boolean checkable = false;
    private String filePath;
    public MyAdapter(Context context,List<String> listText,String filePath){
        this.listText=listText;
        this.context=context;
        this.filePath = filePath;
    }
    public void setListText(List<String> listText){
        this.listText=listText;
        map.clear();
        selected.clear();
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        //return返回的是int类型，也就是页面要显示的数量。
        return listText.size();
    }

    @Override
    public String getItem(int position) {
        return listText.get(position);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public void setItemMultiCheckable(boolean a){
        checkable = a;
    }

    public ArrayList<String> getData(){
        return selected;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView==null){
            //通过一个inflate 可以把一个布局转换成一个view对象
            view=View.inflate(context,R.layout.file_items,null);
        }else {
            view=convertView;//复用历史缓存对象
        }
        //文字
        TextView radioText = (TextView)view.findViewById(R.id.file_name_text);
        radioText.setText(listText.get(position));
        TextView timeText = view.findViewById(R.id.file_time_text);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File(filePath+"/mData/"+listText.get(position));
        timeText.setText(format.format(file.lastModified()));
        //单选按钮
        final CheckBox checkBox=(CheckBox)view.findViewById(R.id.contact_selected_checkbox);
        if(checkable){
            checkBox.setVisibility(View.VISIBLE);
        }else {
            checkBox.setVisibility(View.GONE);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    map.put(position,true);
                    selected.add(listText.get(position));
//                    System.out.println(selected);
                }else {
                    map.remove(position);
                    selected.remove(listText.get(position));
//                    System.out.println(selected);
                }
            }
        });
        if(map!=null&&map.containsKey(position)){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
        return view;
    }
}