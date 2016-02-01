package com.ysd.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ysd.mymap.R;

/**
 * Created by Administrator on 2016/1/25.
 */
public class Helps extends Activity {
    private static final String TAG = "Helps";
    //定义菜单需要的常亮
    private static final int MENU_MAIN= Menu.FIRST+1;
    private static final int MENU_NEW= MENU_MAIN+1;
    private static final int MENU_BACK= MENU_NEW+1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helps);
        setTitle(R.string.menu_helps);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_MAIN, 0, "主页").setAlphabeticShortcut('M');
        menu.add(0, MENU_NEW, 0, R.string.menu_new).setAlphabeticShortcut('N');
        menu.add(0, MENU_BACK, 0, R.string.menu_back).setAlphabeticShortcut('E');
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case MENU_NEW:
                intent.setClass(Helps.this, NewTrack.class);
                startActivity(intent);
                return true;
            case MENU_MAIN:
                intent.setClass(Helps.this, ITracks.class);
                startActivity(intent);
                return true;
            case MENU_BACK:
                finish();
                break;
        }
        return true;
    }
}
