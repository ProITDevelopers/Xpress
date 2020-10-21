package ao.co.proitconsulting.xpress.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_screen);

        delaySplashScreen();
    }

    private void delaySplashScreen() {

        Completable.timer(2, TimeUnit.SECONDS,
                AndroidSchedulers.mainThread())
                .subscribe(() -> {

                    if (TextUtils.isEmpty(AppPrefsSettings.getInstance().getAuthToken())) {

                        if (AppPrefsSettings.getInstance().getUser()!=null){
                            Intent intent = new Intent(SplashScreenActivity.this, LoginTemporarioActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            return;
                        }else {
                            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            return;
                        }



                    }
                    if (!TextUtils.isEmpty(AppPrefsSettings.getInstance().getAuthToken())) {

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