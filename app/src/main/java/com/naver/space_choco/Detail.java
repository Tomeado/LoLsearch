package com.naver.space_choco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Detail extends AppCompatActivity {

    TextView vtitle;
    TextView vcontents;

    String title;
    String contents;

    Button backbtn;

    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            vtitle.setText(title);
            vcontents.setText(contents);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        backbtn = (Button)findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                finish();
            }
        });
        vtitle = (TextView)findViewById(R.id.vtitle);
        vcontents = (TextView)findViewById(R.id.vcontents);
    }



    public void onResume(){
        super.onResume();

        Intent intent = getIntent();
        final String num = intent.getStringExtra("num");

        Thread th = new Thread(){
            String json = "";
            public void run() {
                StringBuffer sBuffer = new StringBuffer();
                try {
                    String urlAddr =
                            Common.server  + "communitydetail.jsp?num=" + num;
                    Log.e("urlAddr", urlAddr);
                    URL url = new URL(urlAddr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    if (conn != null) {
                        conn.setConnectTimeout(20000);
                        conn.setUseCaches(false);
                        if (conn.getResponseCode() ==
                                HttpURLConnection.HTTP_OK) {
                            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                            BufferedReader br = new BufferedReader(isr);
                            while (true) {
                                String line =
                                        br.readLine();
                                if (line == null) {
                                    break;
                                }
                                sBuffer.append(line);

                            }
                            br.close();
                            conn.disconnect();
                        }
                    }
                    json = sBuffer.toString();
                    Log.e("sBuffer", sBuffer.toString());
                } catch (Exception e) {
                    Log.e("다운로드 중 에러 발생", e.getMessage());
                }
                try {
                    JSONObject obj = new JSONObject(json);
                        title = obj.getString("title");
                        contents = obj.getString("contents");
                        mHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    Log.e("json", json);

                    Log.e("파싱 중 에러 발생", e.getMessage());
                }
            }
        };
        th.start();
    }
}
