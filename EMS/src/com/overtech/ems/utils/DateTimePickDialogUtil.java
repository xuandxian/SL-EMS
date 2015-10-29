package com.overtech.ems.utils;

import java.text.SimpleDateFormat;  
import java.util.Calendar;  
import com.overtech.ems.R;
import android.app.Activity;  
import android.app.AlertDialog;  
import android.content.DialogInterface;  
import android.widget.DatePicker;  
import android.widget.DatePicker.OnDateChangedListener;  
import android.widget.EditText;  
import android.widget.LinearLayout;  
  
/** 
 * 实现一个日期选择器
 * @author Will
 */  
public class DateTimePickDialogUtil implements OnDateChangedListener{  
    private DatePicker datePicker;  
    private AlertDialog ad;  
    private String dateTime;  
    private Activity activity;  
  
    /** 
     * 日期时间弹出选择框构造函数 
     *  
     * @param activity 
     *            ：调用的父activity 
     * @param initDateTime 
     *            初始日期时间值，作为弹出窗口的标题和日期时间初始值 
     */  
    public DateTimePickDialogUtil(Activity activity) {  
        this.activity = activity;  
  
    }  
  
    public void init(DatePicker datePicker) {  
        Calendar calendar = Calendar.getInstance();  
        datePicker.init(calendar.get(Calendar.YEAR),  
                calendar.get(Calendar.MONTH),  
                calendar.get(Calendar.DAY_OF_MONTH), this);  
        
          
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");  
        
        dateTime = sdf.format(calendar.getTime());  
    }  
  
    /** 
     * 弹出日期时间选择框方法 
     *  
     * @param inputDate 
     *            :为需要设置的日期时间文本编辑框 
     * @return 
     */  
    public AlertDialog dateTimePicKDialog(final EditText inputDate) {  
        LinearLayout dateTimeLayout = (LinearLayout) activity  
                .getLayoutInflater().inflate(R.layout.common_datetime, null);  
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);  
        init(datePicker);  
        
        ad = new AlertDialog.Builder(activity)  
                .setTitle("请选择您进入本行业的时间")
                .setView(dateTimeLayout)  
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        inputDate.setText(dateTime);  
                    }  
                })  
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        inputDate.setText("");  
                    }  
                }).show();  
  
//        onDateChanged(null, 0, 0, 0);  
        return ad;  
    }  
  
  
    public void onDateChanged(DatePicker view, int year, int monthOfYear,  
            int dayOfMonth) {  
        // 获得日历实例  
        Calendar calendar = Calendar.getInstance();  
  
        calendar.set(datePicker.getYear(), datePicker.getMonth(),  
                datePicker.getDayOfMonth());  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");  
  
        dateTime = sdf.format(calendar.getTime());  
        ad.setTitle(dateTime);  
    }  
  
   
    
}  