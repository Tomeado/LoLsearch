package com.naver.space_choco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Login extends AppCompatActivity {
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    boolean isDrawerOpend;

    Button signup;
    Button login;
    EditText emailinput;
    EditText passwordinput;

    CheckBox remember;

    ProgressDialog mProgress;
    DownThread mThread;

    Handler mAfterDown = new Handler() {
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            Toast.makeText(Login.this, "로그인 성공", Toast.LENGTH_LONG).show();

            finish();
        }
    };




    public void onResume(){
        super.onResume();
        if(Session.email != null){
            finish();
        }
    }

    class DownThread extends Thread {
        String mAddr;
        String mResult;
        DownThread(){
            mAddr = Common.server  + "login.jsp?";
            String email = emailinput.getText().toString();
            String password = passwordinput.getText().toString();

            mAddr = mAddr + "email=" + email + "&pw=" + password;
            mResult = "";
        }

        public void run() {
            StringBuilder html = new StringBuilder();
            try {
                URL url = new URL(mAddr);
                Log.e("url", mAddr);
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
                        mResult = html.toString();
                    }
                    conn.disconnect();
                }
            }
            catch (Exception ex) {;}
            if(mResult.trim().equals("fail")){
                Toast.makeText(Login.this, "로그인 실패", Toast.LENGTH_LONG).show();
            }else {
                Session.email =  emailinput.getText().toString().trim();
                Session.name = mResult.trim();
                Log.e("email", Session.email);
                mAfterDown.sendEmptyMessage(0);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("로그인");



        emailinput = (EditText)findViewById(R.id.emailinput);

        passwordinput = (EditText)findViewById(R.id.passwordinput);

        login = (Button)findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               String email = emailinput.getText().toString();
               if(email.trim().length() < 1){
                   Toast.makeText(Login.this, "이메일은 필수 입력입니다", Toast.LENGTH_LONG).show();
                   return;
               }

                String password = passwordinput.getText().toString();
                if(password.trim().length() < 1){
                    Toast.makeText(Login.this, "비밀번호는 필수 입력입니다", Toast.LENGTH_LONG).show();
                    return;
                }
                mProgress = ProgressDialog.show(Login.this,
                        "로그인", "로그인 확인 중");
                mThread = new DownThread();
                mThread.start();

            }
        });


        signup = (Button)findViewById(R.id.signupButton);

        signup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });

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
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_community){
                    Intent intent = new Intent(Login.this, Community.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_login){
                    Intent intent = new Intent(Login.this, Login.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_logout){
                    Session.email = null;
                    Session.name = null;
                    Intent intent = new Intent(Login.this, MainActivity.class);
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

