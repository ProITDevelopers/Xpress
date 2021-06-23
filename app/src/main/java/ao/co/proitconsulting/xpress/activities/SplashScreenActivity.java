package ao.co.proitconsulting.xpress.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;

import java.util.concurrent.TimeUnit;

import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.mySignalR.MySignalRService;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashScreenActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        delaySplashScreen();

    }


    private void delaySplashScreen() {

        Completable.timer(2, TimeUnit.SECONDS,
                AndroidSchedulers.mainThread())
                .subscribe(() -> {


                    if (!AppPrefsSettings.getInstance().getLoggedIn()){
                        Intent serviceIntent = new Intent(this, MySignalRService.class);
                        stopService(serviceIntent);
                        AppDatabase.clearData();
                        AppPrefsSettings.getInstance().clearAppPrefs();
                        LoginManager.getInstance().logOut();

                        Intent intent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();


                    } else {
                        launchHomeScreen();
                    }


                });
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }




}