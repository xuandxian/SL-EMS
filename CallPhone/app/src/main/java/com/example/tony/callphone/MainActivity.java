package com.example.tony.callphone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_call = (Button) findViewById(R.id.btn_call);

        btn_call.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                EditText et_phonenumber = (EditText)findViewById(R.id.phonenumber);
                String number = et_phonenumber.getText().toString();
                //用intent启动拨打电话
                Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));
                startActivity(intent);
            }
        });
    }
}
