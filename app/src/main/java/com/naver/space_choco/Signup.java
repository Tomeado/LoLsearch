package com.naver.space_choco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Signup extends AppCompatActivity {
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    boolean isDrawerOpend;

    Button back;
    EditText emailinput;
    EditText passwordinput;
    EditText nicknameinput;
    Button oksignup;

    boolean emailcheck;
    boolean nicknamecheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailinput = findViewById(R.id.emailinput);
        emailinput.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            public void onFocusChange(View view, boolean b) {
                final String email = emailinput.getText().toString().trim();
                if (email.length() < 1) {
                    Toast.makeText(Signup.this, "email 은 필수 입력입니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                final Handler emailHandler = new Handler(){
                    public void handleMessage(Message msg){
                        String result = (String)msg.obj;

                        if(result.trim().equals("success")){
                            Toast.makeText(Signup.this, "사용 가능한 이메일입니다..", Toast.LENGTH_LONG).show();
                            emailcheck = true;
                        }else{
                            Toast.makeText(Signup.this, "이미 사용 중 인 이메일입니다..", Toast.LENGTH_LONG).show();
                            emailcheck = false;
                        }
                    }
                };

                Thread th = new Thread() {
                    public void run(){
                        String mAddr;
                        String mResult = "";

                        mAddr = Common.server + "emailcheck.jsp?";

                        mAddr = mAddr + "email=" + email;
                        StringBuilder html = new StringBuilder();
                        try {
                            URL url = new URL(mAddr);
                            HttpURLConnection conn =
                                    (HttpURLConnection) url.openConnection();
                            if (conn != null) {
                                conn.setConnectTimeout(10000);
                                conn.setUseCaches(false);
                                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    BufferedReader br = new BufferedReader(
                                            new
                                                    InputStreamReader(conn.getInputStream()));
                                    while (true) {
                                        String line =
                                                br.readLine();
                                        if (line == null) break;
                                        html.append(line + '\n');
                                    }
                                    br.close();
                                    mResult = html.toString();
                                }
                                conn.disconnect();

                            }
                        }
                        catch (Exception ex) {}
                        Message msg = new Message();
                        msg.obj = mResult;
                        emailHandler.sendMessage(msg);
                    }
                };
                th.start();
            }
        });


        passwordinput = findViewById(R.id.passwordinput);

        nicknameinput = findViewById(R.id.nicknameinput);

       nicknameinput.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            public void onFocusChange(View view, boolean b) {
                final String nickname = nicknameinput.getText().toString().trim();
                if (nickname.length() < 1) {
                    Toast.makeText(Signup.this, "nickname 은 필수 입력입니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                final Handler nicknameHandler = new Handler(){
                    public void handleMessage(Message msg){
                        String result = (String)msg.obj;

                        if(result.trim().equals("success")){
                            Toast.makeText(Signup.this, "사용 가능한 닉네임 입니다..", Toast.LENGTH_LONG).show();
                            nicknamecheck = true;
                        }else{
                            Toast.makeText(Signup.this, "이미 사용 중 인 닉네임 입니다..", Toast.LENGTH_LONG).show();
                            nicknamecheck = false;
                        }
                    }
                };

                Thread th = new Thread() {
                    public void run(){
                        String mAddr;
                        String mResult = "";

                        mAddr = Common.server + "nicknamecheck.jsp?";

                        mAddr = mAddr + "nickname=" + nickname;
                        StringBuilder html = new StringBuilder();
                        try {
                            URL url = new URL(mAddr);
                            HttpURLConnection conn =
                                    (HttpURLConnection) url.openConnection();
                            if (conn != null) {
                                conn.setConnectTimeout(10000);
                                conn.setUseCaches(false);
                                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    BufferedReader br = new BufferedReader(
                                            new
                                                    InputStreamReader(conn.getInputStream()));
                                    while (true) {
                                        String line =
                                                br.readLine();
                                        if (line == null) break;
                                        html.append(line + '\n');
                                    }
                                    br.close();
                                    mResult = html.toString();
                                }
                                conn.disconnect();

                            }
                        }
                        catch (Exception ex) {}
                        Message msg = new Message();
                        msg.obj = mResult;
                        nicknameHandler.sendMessage(msg);
                    }
                };
                th.start();
            }
        });

        oksignup = findViewById(R.id.oksignup);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        oksignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String email = emailinput.getText().toString().trim();
               if(email.length() < 1){
                   Toast.makeText(Signup.this, "email 은 필수 입력입니다.", Toast.LENGTH_LONG).show();
                   return;
               }

               final String password = passwordinput.getText().toString().trim();
               if(password.length() < 1){
                   Toast.makeText(Signup.this, "password 은 필수 입력입니다.", Toast.LENGTH_LONG).show();
                   return;
               }

               final String nickname = nicknameinput.getText().toString().trim();
               if(nickname.length() < 1){
                   Toast.makeText(Signup.this, "nickname 은 필수 입력입니다.", Toast.LENGTH_LONG).show();
                   return;
               }

                if(emailcheck == false){
                    Toast.makeText(Signup.this, "email 체크를 하세요", Toast.LENGTH_LONG).show();
                    return;
                }

                if(nicknamecheck == false){
                    Toast.makeText(Signup.this, "nickname 체크를 하세요", Toast.LENGTH_LONG).show();
                    return;
                }

                final Handler registerHandler = new Handler(){
                    public void handleMessage(Message msg){
                        String result = (String)msg.obj;

                        if(result.trim().equals("success")){
                            Toast.makeText(Signup.this, "회원가입 성공", Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(Signup.this, "회원가입 실패", Toast.LENGTH_LONG).show();

                        }
                    }
                };

                Thread th = new Thread() {
                    public void run(){
                        String mAddr;
                        String mResult = "";

                        mAddr = Common.server + "register.jsp?";
                        mAddr = mAddr + "email=" + email;
                        mAddr = mAddr + "&name=" + nickname;
                        mAddr = mAddr + "&pw=" + password;
                        StringBuilder html = new StringBuilder();
                        try {
                            URL url = new URL(mAddr);
                            HttpURLConnection conn =
                                    (HttpURLConnection) url.openConnection();
                            if (conn != null) {
                                conn.setConnectTimeout(10000);
                                conn.setUseCaches(false);
                                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    BufferedReader br = new BufferedReader(
                                            new
                                                    InputStreamReader(conn.getInputStream()));
                                    while (true) {
                                        String line =
                                                br.readLine();
                                        if (line == null) break;
                                        html.append(line + '\n');
                                    }
                                    br.close();
                                    mResult = html.toString();
                                }
                                conn.disconnect();

                            }
                        }
                        catch (Exception ex) {}
                        Message msg = new Message();
                        msg.obj = mResult;
                        registerHandler.sendMessage(msg);
                    }
                };
                th.start();
            }
        });


        setTitle("회원가입");



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
                    Intent intent = new Intent(Signup.this, MainActivity.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_community){
                    Intent intent = new Intent(Signup.this, Community.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_login){
                    finish();
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