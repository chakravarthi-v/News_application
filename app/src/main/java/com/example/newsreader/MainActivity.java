package com.example.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView outlook;
    ArrayAdapter buck;
    SQLiteDatabase databse;
    ArrayList<String> found=new ArrayList<>();
    ArrayList<String> lost=new ArrayList<>();
   public void updateList(){
        Cursor c=databse.rawQuery("SELECT * FROM datae",null);
        int a=c.getColumnIndex("title");
        Log.i("no","found u");
        int b=c.getColumnIndex("name");
        if(c.moveToFirst()){
            found.clear();
            lost.clear();
        }
        while(c.moveToNext()){
            //Log.i("text",c.getString(a));
            found.add(c.getString(a));
            lost.add(c.getString(b));
        }
        buck.notifyDataSetChanged();
    }
    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
           String x="";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection data = (HttpURLConnection) url.openConnection();
                InputStream in=data.getInputStream();
                InputStreamReader file=new InputStreamReader(in);
                int flex=file.read();
                while(flex!=-1){
                    char set=(char) flex;
                    x+=set;
                    flex=file.read();
                }
                JSONArray fix=new JSONArray(x);
                int total=20;
                if(fix.length()<20){
                    total=fix.length();
                }
                databse.execSQL("DELETE FROM datae");
                for(int i=0;i<total;i++){
                    String s=fix.getString(i);
                    url=new URL("https://hacker-news.firebaseio.com/v0/item/"+s+".json?print=pretty");
                    data=(HttpURLConnection) url.openConnection();
                    in=data.getInputStream();
                    file=new InputStreamReader(in);
                    int flip=file.read();
                    String high="";
                    while(flip!=-1){
                        char set=(char) flip;
                        high+=set;
                        flip=file.read();
                    }
                    JSONObject curl=new JSONObject(high);
                    if(!curl.isNull("title")&&!curl.isNull("url")) {
                        String tile = curl.getString("title");
                        String urls = curl.getString("url");
                        String mouse="INSERT INTO datae(title,name) VALUES(?,?)";
                        SQLiteStatement space=databse.compileStatement(mouse);
                        space.bindString(1,tile);
                        space.bindString(2,urls);
                        space.execute();
                       // Log.i("text",title);
                    }
                    }
                return null;
            }
            catch (Exception e){
                e.printStackTrace();
                Log.i("text","helo");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //buck.notifyDataSetChanged();
            updateList();
            //updateList();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outlook=findViewById(R.id.lost);
        buck=new ArrayAdapter(this,android.R.layout.simple_list_item_1,found);
        try {
            databse = this.openOrCreateDatabase("Datae", MODE_PRIVATE, null);
            databse.execSQL("CREATE TABLE IF NOT EXISTS datae (title VARCHAR,name VARCHAR)");
        }
        catch(Exception e){
            e.printStackTrace();
        }

        outlook.setAdapter(buck);
        DownloadTask nexa= new DownloadTask();
        try {
            nexa.execute(" https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        }
        catch (Exception e){
            e.printStackTrace();
            Log.i("text","hi");
        }
        outlook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent get=new Intent(getApplicationContext(),view.class);
                get.putExtra("content",lost.get(position));
                startActivity(get);
            }
        });
        updateList();
    }
}