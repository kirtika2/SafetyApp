package com.example.vaibhavgupta.feature403;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
public class FetchData
{
    //TO pick Data From Settings menu
    SharedPreferences sharedPrefs;
    private final Context mContext;
     public FetchData(Context mContext) {
        this.mContext = mContext;
    }
    public String getmobno()
    {
        sharedPrefs= PreferenceManager.getDefaultSharedPreferences(mContext);
        String phone_no=sharedPrefs.getString(
                mContext.getString(R.string.pref_phone_key),
                mContext.getString(R.string.pref_phone_default));
         return phone_no;
    }

}
