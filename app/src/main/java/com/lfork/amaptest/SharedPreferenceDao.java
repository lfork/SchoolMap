package com.lfork.amaptest;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author 98620
 */
public class SharedPreferenceDao {
    private SharedPreferences sp;
    
    public SharedPreferenceDao(SharedPreferences sp) {
        this.sp = sp;
    }

    public SharedPreferenceDao(Context context) {
        this(context.getSharedPreferences("config", Context.MODE_PRIVATE));
    }

    public void put(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String get(String key) {
        return sp.getString(key, null);
    }
}
