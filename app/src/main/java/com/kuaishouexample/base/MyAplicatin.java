package com.kuaishouexample.base;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.kuaishouexample.db.DaoMaster;
import com.kuaishouexample.db.DaoSession;
import com.kuaishouexample.db.DbHelper;

import org.greenrobot.greendao.database.Database;

public class MyAplicatin extends Application {

    //greendao
    private DaoSession daoSession;
    private static MyAplicatin sInstance;
    //********


    @Override
    public void onCreate() {
        Log.d("tw", "程序创的时候执行");
        super.onCreate();
        sInstance = this;
        DbHelper helper = new DbHelper(this, "kuaishou", null);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static MyAplicatin getsInstance() {
        return sInstance;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d("tw", "程序在内存清理的时候执行");
        super.onTrimMemory(level);
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d("tw", "程序终止的时候执行");
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("tw", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d("tw", "低内存的时候执行");
        super.onLowMemory();
    }
}
