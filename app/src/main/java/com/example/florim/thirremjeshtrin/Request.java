package com.example.florim.thirremjeshtrin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import java.util.HashMap;
import java.util.Map;

public class Request extends AppCompatActivity {
    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        String message=getIntent().getStringExtra("Message");
        ID=getIntent().getStringExtra("ID");
        TextView txtRequest = (TextView) findViewById(R.id.txtRequest);
        txtRequest.setText(message);


    }
    public void onAcceptClick(View v){
        Map<String,String> params=new HashMap<>();
        params.put("ReqID",ID);
        params.put("status","1");
        ConnectToServer connectToServer=new ConnectToServer();
        connectToServer.sendRequest(ConnectToServer.CHECKREQUEST,params,true);
        finish();
    }
    public void onRefuseClick(View v){
        Map<String,String> params=new HashMap<>();
        params.put("ReqID",ID);
        params.put("status","0");
        ConnectToServer connectToServer=new ConnectToServer();
        connectToServer.sendRequest(ConnectToServer.CHECKREQUEST,params,true);
        finish();
    }

}
