package com.kuaishouexample.util;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by suntianwei on 2017/4/14.
 */

public class SharedPreferencesUitl {
    private android.content.SharedPreferences sharedPreferences;
    private android.content.SharedPreferences.Editor editor;
    private static SharedPreferencesUitl preferencesUitl = null;


    @SuppressLint("WrongConstant")
    public SharedPreferencesUitl(Context context, String key) {
        sharedPreferences = context.getSharedPreferences(key, Context.MODE_APPEND);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferencesUitl getInstance(Context context, String filename) {
        if (preferencesUitl == null) {
            preferencesUitl = new SharedPreferencesUitl(context, filename);
        }
        return preferencesUitl;
    }

    public void write(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void write(String key, String value) {
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    public void write(String key, boolean[] value) {
        for (int i = 0; i < value.length; i++) {
            editor.putBoolean(key, value[i]);
        }
        editor.commit();
        editor.apply();
    }

//    /**
//     * 保存条码队列
//     *
//     * @param key
//     * @param value
//     */
//    public void writeQueue(String key, Queue value) {
//        Object[] object = value.toArray();
//        for (int i = 0; i < object.length; i++) {
//            editor.putString(key, String.valueOf(object[i]));
//        }
//        editor.commit();
//        editor.apply();
//    }

    public boolean read(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public String read(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public boolean[] read(String key, boolean[] defValue) {
        boolean[] booleens = new boolean[defValue.length];
//        boolean[] booleens = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};
        if (defValue != null) {
            for (int i = 0; i < defValue.length; i++) {
                booleens[i] = sharedPreferences.getBoolean(key, defValue[i]);
            }
        }
        return booleens;
//        return false;
    }



    /**
     * 存储Queue<String>
     *
     * @param key    Queue<String>对应的key
     * @param strList 对应需要存储的Queue<String>
     */
    public void writeQueue(String key, Queue<String> strList) {
        if (null == strList) {
            return;
        }
        // 保存之前先清理已经存在的数据，保证数据的唯一性
//        strList.clear();
        int size = strList.size();
        putIntValue(key + "size", size);
        for (int i = 0; i < size; i++) {
            putStringValue(key + i, String.valueOf(strList.toArray()[i]));
        }
    }

    /**
     * 存储数据(Int)
     *
     * @param key
     * @param value
     */
    private void putIntValue(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 存储数据(String)
     *
     * @param key
     * @param value
     */
    private  void putStringValue(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 取出Queue<String>
     *
     * @param key Queue<String> 对应的key
     * @return Queue<String>
     */
    public Queue<String> readQueue(String key) {
        Queue<String> strList = new ArrayDeque<>();//条码队列
        int size = getIntValue(key + "size", 0);
        //Log.d("sp", "" + size);
        for (int i = 0; i < size; i++) {
            strList.offer(getStringValue(key + i, null));
        }
        return strList;
    }

    /**
     * 取出数据（String)
     *
     * @param key
     * @param defValue 默认值
     * @return
     */
    private String getStringValue(String key, String defValue) {

        String value = sharedPreferences.getString(key, defValue);
        return value;
    }

    /**
     * 取出数据（int)
     *
     * @param key
     * @param defValue 默认值
     * @return
     */
    private int getIntValue(String key, int defValue) {
        int value = sharedPreferences.getInt(key, defValue);
        return value;
    }
    /**
     * 清空所有数据
     * @param key
     */
    public  void removeQueueList(String key) {
        int size = getIntValue( key + "size", 0);
        if (0 == size) {
            return;
        }
        remove(key + "size");
        for (int i = 0; i < size; i++) {
            remove( key + i);
        }
    }
    /**
     * 清空对应key数据
     * @param key
     */
    public  void remove( String key) {
        editor.remove(key);
        editor.commit();
    }
    public void delete(String key) {
        editor.remove(key);
        editor.commit();
    }
}
