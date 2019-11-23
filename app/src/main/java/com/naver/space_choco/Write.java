package com.naver.space_choco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Write extends AppCompatActivity {

    Button back;
    Button upload;
    EditText title;
    EditText contents;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = (EditText)findViewById(R.id.title);

        contents = (EditText)findViewById(R.id.contents);
        upload = (Button)findViewById(R.id.upload);

        final Handler uploadHander = new Handler(){
            @Override
          public void handleMessage(Message msg){
              if(result.trim().equals("success")){
                  Toast.makeText(Write.this, "글 작성 성공", Toast.LENGTH_LONG).show();
                  finish();
              }else{
                  Toast.makeText(Write.this, "글 작성 실패", Toast.LENGTH_LONG).show();
              }
          }
        };

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread th = new Thread(){
                    public void run() {
                        StringBuilder html = new StringBuilder();
                        try {
                            String addr = Common.server  + "communitywrite.jsp?";
                            addr = addr + "email=" + Session.email;
                            addr = addr + "&name=" + Session.name;
                            addr = addr + "&title=" + title.getText().toString().trim();
                            addr = addr + "&contents=" + contents.getText().toString().trim();

                            URL url = new URL(addr);

                            HttpURLConnection conn =
                                    (HttpURLConnection)url.openConnection();
                            if (conn != null) {
                                conn.setConnectTimeout(10000);
                                conn.setUseCaches(false);

                                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    BufferedReader br = new BufferedReader(
                                            new
                                                    InputStreamReader(conn.getInputStream()));
                                    while(true){
                                        String line =
                                                br.readLine();
                                        if (line == null) break;
                                        html.append(line + '\n');
                                    }
                                    br.close();
                                    result = html.toString();
                                }
                                conn.disconnect();
                            }
                        }
                        catch (Exception ex) {;}
                        uploadHander.sendEmptyMessage(0);
                    }
                };
                th.start();

            }
        });


    }
}
