package com.ysd.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ysd.mymap.R;
import com.ysd.mymap.db.TrackDbAdapter;

import java.sql.SQLException;

/**
 * Created by Administrator on 2016/1/24.
 */
public class NewTrack extends Activity {
    private static final String TAG="NewTrack";
    private Button button_new;
    private EditText field_new_name;
    private EditText field_new_desc;
    private TrackDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_track);
        setTitle(R.string.menu_new);
        findView();
        setListensers();
        mDbHelper= new TrackDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onstop");
        //mDbHelper.close();
    }
    private void findView() {
        Log.d(TAG, "find views");
        field_new_name = (EditText) findViewById(R.id.new_name);
        field_new_desc = (EditText) findViewById(R.id.new_desc);
        button_new = (Button) findViewById(R.id.new_submit);
        //listen for button clicks

    }
    private void setListensers() {
        Log.d(TAG, "onclick new_track");
        button_new.setOnClickListener(new_track);
    }

    private Button.OnClickListener new_track = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.d(TAG, "ONclick new_track");
            try {
                String name = (field_new_name.getText().toString());
                String desc = (field_new_desc.getText().toString());
                if (name.equals("")) {
                    Toast.makeText(NewTrack.this, getString(R.string.new_name_null), Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: 2016/1/25调用存储借口保存到数据库并启动service
                    Long row_id = mDbHelper.createTrack(name,desc);
                    Log.d(TAG, "row_id=" + row_id);
                    Intent intent = new Intent();
                    intent.setClass(NewTrack.this, Helps.class);
                    intent.putExtra(TrackDbAdapter.KEY_ROWID, row_id);
                    intent.putExtra(TrackDbAdapter.NAME, name);
                    intent.putExtra(TrackDbAdapter.DESC, desc);
                    startActivity(intent);

                }
            } catch (Exception err) {
                Log.e(TAG, "error" + err.toString());
                Toast.makeText(NewTrack.this,getString(R.string.new_fail),Toast.LENGTH_SHORT).show();
            }
        }
    };
}