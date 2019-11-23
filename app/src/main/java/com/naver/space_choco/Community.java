package com.naver.space_choco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.os.*;

public class Community extends AppCompatActivity {
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    boolean isDrawerOpend;

    Button write;

    ListView articlelist;
    ArrayList<String> list;
    ArrayList<String> numlist;
    ArrayAdapter<String> adapter;
    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        setTitle("커뮤니티");

        articlelist = (ListView)findViewById(R.id.articlelist);
        articlelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            //첫번째 매개변수는 이벤트가 발생한 리스트 뷰
            //두번째 매개변수는 이벤트가 발생한 항목 뷰
            //세번째 매개변수는 이벤트가 발생한 인덱스
            //네번째 매개변수는 이벤트가 발생한 항목 뷰의 아이디
            public void onItemClick(
                    AdapterView<?> parent, View view, int position, long id) {
               Intent intent = new Intent(Community.this, Detail.class);
               intent.putExtra("num", numlist.get(position));
               startActivity(intent);
            }
        });

        list = new ArrayList<String>();
        numlist = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        articlelist.setAdapter(adapter);

        articlelist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        articlelist.setDividerHeight(2);

        write = (Button)findViewById(R.id.write);

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Community.this, Write.class);
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
                    Intent intent = new Intent(Community.this, MainActivity.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_community){
                    Intent intent = new Intent(Community.this, Community.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_login){
                    Intent intent = new Intent(Community.this, Login.class);
                    startActivity(intent);
                }else if(id==R.id.menu_drawer_logout){
                    Session.email = null;
                    Session.name = null;
                    Intent intent = new Intent(Community.this, MainActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });
    }


    public void onResume(){
        super.onResume();
        if(Session.email == null){
            showToast("로그인을 해야 커뮤니티에 접근이 가능합니다.");
            Intent intent = new Intent(Community.this, Login.class);
            startActivity(intent);
        }


        Thread th = new Thread(){
            String json = "";
            public void run() {
                StringBuffer sBuffer = new StringBuffer();
                try {
                    String urlAddr =
                            Common.server + "communitylist.jsp";
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
                } catch (Exception e) {
                    Log.e("다운로드 중 에러 발생", e.getMessage());
                }
                try {
                    JSONArray obj = new JSONArray(json);

                    list.clear();
                    numlist.clear();
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject item = obj.getJSONObject(i);
                        String msg = String.format("%-50s%-15s%10s",item.getString("title"), item.getString("name"), item.getString("regdate"));
                        list.add(msg);
                        numlist.add(item.getString("num"));
                    }
                    mHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    Log.e("파싱 중 에러 발생", e.getMessage());
                }
            }
        };
        th.start();
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