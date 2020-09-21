package ao.co.proitconsulting.xpress.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import ao.co.proitconsulting.xpress.MainActivity;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_screen);
        delaySplashScreen();
    }

    private void delaySplashScreen() {
//        mAuth = FirebaseAuth.getInstance();

        Completable.timer(2, TimeUnit.SECONDS,
                AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

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