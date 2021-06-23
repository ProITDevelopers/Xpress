package ao.co.proitconsulting.xpress.localDB;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import ao.co.proitconsulting.xpress.XpressEntregasApplication;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;

public class AppPrefsSettings {

    private static final String APP_SHARED_PREF_NAME = "XPRESS_SHAREDPREF";
    private static final String KEY_USER = "USUARIO_KEY";
    private static final String KEY_SESSION = "USER_LOGGEDIN";
    private static final String KEY_AUTH_TOKEN = "USER_AUTH_TOKEN";
    private static final String KEY_AUTH_TOKEN_TIME = "AUTH_TOKEN_TIME";
    private static final String KEY_CHANGE_VIEW = "CHANGE_VIEW";
    private static final String KEY_FILTER_VIEW = "ESTAB_FILTER_VIEW";

    private static AppPrefsSettings mInstance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Gson gson;


    public static synchronized AppPrefsSettings getInstance() {
        if (mInstance == null) {
            mInstance = new AppPrefsSettings(XpressEntregasApplication.getInstance().getApplicationContext());
        }
        return mInstance;
    }

    private AppPrefsSettings(Context context) {
        super();
        sharedPreferences = context.getSharedPreferences(APP_SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        gson = new Gson();
    }


    //SAVE USER SESSION
    public void setLoggedIn(boolean loggedIn){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SESSION, loggedIn);
        editor.apply();
    }

    //GET USER SESSION
    public boolean getLoggedIn() {
        return sharedPreferences.getBoolean(KEY_SESSION, false);
    }


    //SAVE USER DATA
    public void saveUser(UsuarioPerfil usuario){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userInfo = gson.toJson(usuario);
        editor.putString(KEY_USER, userInfo);
        editor.apply();

    }

    //GET USER DATA
    public UsuarioPerfil getUser(){

        String userInfo = sharedPreferences.getString(KEY_USER, null);
        return  gson.fromJson(userInfo, UsuarioPerfil.class);
    }

    //SAVE TOKEN
    public void saveAuthToken(String authToken) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.apply();
    }

    public void saveTokenTime(String tokenTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN_TIME, tokenTime);
        editor.apply();
    }



    //GET TOKEN
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public String getTokenTime() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN_TIME, null);
    }





    //SAVE CHANGE_VIEW
    public void saveChangeView(int viewValue) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CHANGE_VIEW, viewValue);
        editor.apply();
    }

    //GET CHANGE_VIEW
    public int getListGridViewMode() {
        return sharedPreferences.getInt(KEY_CHANGE_VIEW, 1);
    }

    //SAVE ESTAB_FILTER_VIEW
    public void saveEstabFilterView(int viewValue) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_FILTER_VIEW, viewValue);
        editor.apply();
    }

    //GET ESTAB_FILTER_VIEW
    public int getEstabFilterView() {
        return sharedPreferences.getInt(KEY_FILTER_VIEW, 0);
    }

    //DELETE KEY_AUTH_TOKEN
    public void deleteToken(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_AUTH_TOKEN_TIME);
        editor.apply();


    }


    //DELETE ALL DATA
    public void clearAppPrefs(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
