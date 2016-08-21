package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class SafeContactActivity extends Activity {

    public static final int CONTACT_OK = 200;
    private List<HashMap<String, String>> contactList;
    private ListView lv_safe_contact;
    private MyAdapter mAdapter;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case CONTACT_OK:
                    mAdapter = new MyAdapter();
                    lv_safe_contact.setAdapter(mAdapter);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_contact);

        lv_safe_contact = (ListView) findViewById(R.id.lv_safe_contact);

        initData();

        lv_safe_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*if (mAdapter != null) {
                    HashMap<String, String> item = mAdapter.getItem(position);
                    String phone = item.get("phone");
                }*/
                TextView tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);
                String phone = tv_contact_phone.getText().toString();

                //删除指定字符“-”（取代成空字符）
                phone = phone.replace("-", "").replace(" ", "");
                Intent intent = new Intent();
                intent.putExtra("phone", phone);
                setResult(0, intent);

                finish();
            }
        });
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                contactList = new ArrayList<HashMap<String, String>>();
                ContentResolver contentResolver = getContentResolver();
                Cursor contact_cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"}, null, null, null);
                while (contact_cursor.moveToNext()) {
                    HashMap<String, String> map = new HashMap<>();

                    String contact_id = contact_cursor.getString(0);

                    Cursor data_cursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"), new String[]{"data1", "mimetype"},
                            "raw_contact_id=?", new String[]{contact_id}, null);
                    while (data_cursor.moveToNext()) {
                        String data = data_cursor.getString(0);
                        String minetype = data_cursor.getString(1);
                        if ("vnd.android.cursor.item/phone_v2".equals(minetype)) {
                            if (!TextUtils.isEmpty(data)) {
                                map.put("phone", data);
                            }
                        } else if ("vnd.android.cursor.item/name".equals(minetype)) {
                            if (!TextUtils.isEmpty(data)) {
                                map.put("name", data);
                            }
                        }
                    }
                    contactList.add(map);
                }
                Message msg = new Message();
                msg.what = 200;
                mHandler.sendMessage(msg);
            }
        }.start();

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return  contactList.get(position);
    }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(getApplicationContext(), R.layout.view_item_safe_contact, null);
            }
            TextView tv_contact_name = (TextView) view.findViewById(R.id.tv_contact_name);
            TextView tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);
            tv_contact_name.setText(contactList.get(position).get("name"));
            tv_contact_phone.setText(contactList.get(position).get("phone"));

            return view;
        }
    }
}
