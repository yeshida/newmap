package com.ysd.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ysd.mymap.R;

/**
 * Created by Administrator on 2016/1/24.
 */
public class ITracks extends Activity {
    private static final String TAG = "ITracks";
    private static final int MENU_NEW=Menu.FIRST+1;
    private static final int MENU_CON=MENU_NEW+1;
    private static final int MENU_SETTING=MENU_CON+1;
    private static final int MENU_HELPS=MENU_SETTING+1;
    private static final int MENU_EXIT=MENU_HELPS+1;
    //初始化菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        menu.add(0, MENU_NEW, 0, R.string.menu_new).setAlphabeticShortcut('N');
        menu.add(0, MENU_CON, 0, R.string.menu_con).setAlphabeticShortcut('C');
        menu.add(0, MENU_SETTING, 0, R.string.menu_setting).setAlphabeticShortcut('S');
        menu.add(0, MENU_HELPS, 0, R.string.menu_helps).setAlphabeticShortcut('H');
        menu.add(0, MENU_EXIT, 0, R.string.menu_exit).setAlphabeticShortcut('E');
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case MENU_NEW:
                intent.setClass(ITracks.this, NewTrack.class);
                startActivity(intent);
                return true;
            case MENU_CON:
                //TODO:继续跟踪选择的记录
              //  conTrackService();
                return true;
            case MENU_SETTING:
                intent.setClass(ITracks.this, Setting.class);
                startActivity(intent);
                return true;
            case MENU_HELPS:
                intent.setClass(ITracks.this, Helps.class);
                startActivity(intent);
                return true;
            case MENU_EXIT:
                finish();
                break;
        }
        return true;
    }
}
