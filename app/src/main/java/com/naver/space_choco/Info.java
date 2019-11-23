package com.naver.space_choco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Info extends AppCompatActivity {
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

    String gamerId;
    int level;
    int profileIconId;

    EditText gginput;
    Button search;

    String json;


    ImageView picon;
    ImageView rank;
    ImageView master;

    TextView nickname;
    TextView LP;
    TextView mspoint;


    public void onResume(){
        super.onResume();
        Intent intent = getIntent();
        nickname.setText(intent.getStringExtra("nickname") + " 레벨은 " + intent.getIntExtra("level", 0) + "입니다.");
        int profileIconId = intent.getIntExtra("profileIconId", 0);

        String imageUrl = "http://ddragon.leagueoflegends.com/cdn/9.22.1/img/profileicon/" + profileIconId + ".png";
        Log.e("imageurl", imageUrl);
        Glide.with(this).load(imageUrl).into(picon);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        picon = (ImageView)findViewById(R.id.picon);
        nickname = (TextView)findViewById(R.id.nickname);

        rank = (ImageView)findViewById(R.id.rank);
        LP = (TextView)findViewById(R.id.LP);

        master = (ImageView)findViewById(R.id.master);
        mspoint = (TextView)findViewById(R.id.mspoint);

        setTitle("검색결과");

        gginput = (EditText) findViewById(R.id.gginput);

        search = (Button) findViewById(R.id.search);
       final Handler successHandler = new Handler() {
          @Override
            public void handleMessage(Message msg) {
                Intent intent = new Intent(Info.this, Info.class);
                intent.putExtra("gamerId", gamerId);
                intent.putExtra("level", level);
                intent.putExtra("profileIconId", profileIconId);
                intent.putExtra("nickname", gginput.getText().toString().trim());
                startActivity(intent);
            }
        };

        final Handler failHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(Info.this, "없는 아이디이거나 잘못된 아이디 입니다.", Toast.LENGTH_LONG).show();
            }
        };

        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String imsi = gginput.getText().toString().trim();
                final String nickname = imsi.replace(" ", "");
                if (nickname.length() < 1) {
                    Toast.makeText(Info.this, "닉네임은 필수 입력입니다", Toast.LENGTH_LONG).show();
                    return;
                }
                Thread th = new Thread() {
                    public void run() {
                        StringBuffer sBuffer = new StringBuffer();
                        try {
                            String urlAddr =
                                    "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + java.net.URLEncoder.encode(nickname, "utf-8") + "?api_key=RGAPI-3de1bcce-e713-4e87-9eeb-526001d58983";

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
                            JSONObject obj = new JSONObject(json);
                            level = obj.getInt("summonerLevel");
                            profileIconId = obj.getInt("profileIconId");
                            gamerId = obj.getString("id");
                            successHandler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            failHandler.sendEmptyMessage(0);
                        }
                    }
                };
                th.start();

            }
        });


        //위의 상태바 색상
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFF0000));

        //타이틀 설정
        //setTitle("");

        drawer=findViewById(R.id.main_drawer);
        toggle=new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.main_drawer_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.menu_drawer_search){
                    finish();
                }else if(id==R.id.menu_drawer_community){
                    Intent intent = new Intent(Info.this, Community.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_login){
                    Intent intent = new Intent(Info.this, Login.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_logout){
                    Session.email = null;
                    Session.name = null;
                    Intent intent = new Intent(Info.this, MainActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }



    private void showToast(String message){
        Toast toast=Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

}
