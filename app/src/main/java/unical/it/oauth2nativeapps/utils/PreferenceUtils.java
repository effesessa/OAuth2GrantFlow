package unical.it.oauth2nativeapps.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

import unical.it.oauth2nativeapps.model.DriveFile;
import unical.it.oauth2nativeapps.model.OAuthToken;

public class PreferenceUtils {

    private static final String PREFS_NAME = "boxCodeVerifierPrefs";

    public static final String CODE_VERIFIER = "codeVerifier";

    private static final String OAUTH_TOKEN = "oauthToken";

    private static final String DRIVE_FILES = "driveFiles";

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.edit();
    }

    public static void storeWithString(Context context, String KEY, String s) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(KEY, s);
        editor.commit();
    }

    public static void storeWithBoolean(Context context, boolean b, String KEY) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(KEY,b);
        editor.commit();
    }

    public static boolean isStored(Context context, String KEY) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(KEY))
            return false;
        return sharedPreferences.getBoolean(KEY,false);
    }

    public static String getCodeVerifier(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(CODE_VERIFIER))
            return null;
        return sharedPreferences.getString(CODE_VERIFIER, "DEFAULT");
    }

    public static void storeOAuthToken(Context context,String json) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(OAUTH_TOKEN,json);
        editor.commit();
    }

    public static void storeDriveFile(Context context,String json) {
        SharedPreferences.Editor editor = getEditor(context);
        Set<String> set = getStrDriveFiles(context);
        set.add(json);
        editor.putStringSet(DRIVE_FILES,set);
        editor.commit();
    }

    private static Set<String> getStrDriveFiles(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(DRIVE_FILES))
            return new HashSet<>();
        return sharedPreferences.getStringSet(DRIVE_FILES,new HashSet<String>());
    }

    public static Set<DriveFile> getDriveFiles(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet(DRIVE_FILES, new HashSet<String>());
        Set<DriveFile> driveFiles = new HashSet<>();
        if (set == null)
            return driveFiles;
        if(set.isEmpty())
            return driveFiles;
        Gson gson = new Gson();
        for(String json: set) {
            driveFiles.add(gson.fromJson(json,DriveFile.class));
        }
        return driveFiles;
    }

    public static void removeAllDriveFile(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(DRIVE_FILES);
        editor.apply();
    }

    public static OAuthToken getOAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(OAUTH_TOKEN))
            return null;
        String json = sharedPreferences.getString(OAUTH_TOKEN, null);
        if (json == null)
            return new OAuthToken();
        Gson gson = new Gson();
        return gson.fromJson(json,OAuthToken.class);
    }

    public static void storeWithIntValue(Context context, int n, String KEY) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(KEY,n);
        editor.commit();
    }

    public static int getIntValue(Context context, String KEY) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(KEY))
            return -1;
        return sharedPreferences.getInt(KEY,0);
    }

    public static boolean isBooleanValue(Context context, String KEY) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(KEY))
            return false;
        return sharedPreferences.getBoolean(KEY,false);
    }
}
