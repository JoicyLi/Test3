package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //SharedPreferences文件名
    private final static String SharedPreferencesFileName = "PersonalInfo";
    private final static String Key_Name = "Name";
    private final static String Key_StuNumber = "StuNumber";

    private ContentResolver resolver;
    private static String TAG = "ContactName";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(SharedPreferencesFileName, MODE_PRIVATE);
        editor = preferences.edit();

        Button btnWrite = (Button) findViewById(R.id.ButtonWrite);
        Button btnRead = (Button) findViewById(R.id.ButtonRead);
        Button btnWrite2 = (Button) findViewById(R.id.ButtonWrite2);
        Button btnRead2 = (Button) findViewById(R.id.ButtonRead2);
        Button buttonAll = (Button) findViewById(R.id.ButtonAll);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(Key_Name, "李武霞");
                editor.putString(Key_StuNumber, "2017011446");
                editor.apply();
            }
        });

        //输出xml
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strName = preferences.getString(Key_Name, null);
                String strStuNumber = preferences.getString(Key_StuNumber, null);

                if (strName != null && strStuNumber != null)
                    Toast.makeText(MainActivity.this, "姓名：" + strName + "  学号：" + strStuNumber, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "无数据", Toast.LENGTH_LONG).show();
            }
        });

        //写文件
        btnWrite2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                OutputStream out = null;
                try {
                    FileOutputStream fileOutputStream = openFileOutput("PersonalInfo", MODE_PRIVATE);
                    out = new BufferedOutputStream(fileOutputStream);
                    String content = "姓名：李武霞  学号：2017011446";
                    try {
                        out.write(content.getBytes(StandardCharsets.UTF_8));
                    } finally {
                        if (out != null)
                            out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //读文件
        btnRead2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputStream in = null;
                try {
                    File file = new File("/data/data/com.example.helloworld/files/PersonalInfo");
                    StringBuilder sb = new StringBuilder("");
                    String s = "";
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    try {
                        while ((s = br.readLine()) != null) {
                            sb.append(s + "\n");
                        }
                        Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                    } finally {
                        if (br != null)
                            br.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //读取联系人并打印姓名
        resolver  = this.getContentResolver();
        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    String msg;

                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    msg = "id:" + id;

                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    msg = msg + " name:" + name;

                    /*
                    Cursor phoneNumbers = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
                    String phoneNumber = null;
                    while (phoneNumbers.moveToNext()) {
                        phoneNumber = phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        msg = msg + " phone:" + phoneNumber;
                    }
                    */

                    Log.v(TAG, msg);
                }
            }
        });
    }
}