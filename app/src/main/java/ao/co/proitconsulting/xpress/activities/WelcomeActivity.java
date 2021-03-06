package ao.co.proitconsulting.xpress.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.mySignalR.MySignalRService;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class WelcomeActivity extends AppCompatActivity {

    private Animation topAnim, bottomAnim, leftAnim;

    private ImageView imgAppLogoHands, imgAppLogo;
    private RelativeLayout relative_Start;
    private FloatingActionButton fabStart;
    private TextView txt_SignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initViews();
    }

    private void initViews() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation_splash);
        leftAnim = AnimationUtils.loadAnimation(this, R.anim.left_animation_splash);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation_splash);

        imgAppLogoHands = findViewById(R.id.imgAppLogoHands);
        imgAppLogo = findViewById(R.id.imgAppLogo);
        relative_Start = findViewById(R.id.relative_Start);
        fabStart = findViewById(R.id.fabStart);
        txt_SignUp = findViewById(R.id.txt_SignUp);

        SpannableString spannableString = new SpannableString(getString(R.string.splash_sign_up_hint));
        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.xpress_green));
        spannableString.setSpan(new UnderlineSpan(),22,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(fcsGreen,22,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_SignUp.setText(spannableString);

        imgAppLogo.setAnimation(topAnim);

        Completable.timer(3, TimeUnit.SECONDS,
                AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    imgAppLogoHands.setAnimation(leftAnim);
                    imgAppLogoHands.setVisibility(View.VISIBLE);
                    delaySplashScreen();
                });

//        delaySplashScreen();



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

                        relative_Start.setVisibility(View.VISIBLE);
                        relative_Start.setAnimation(bottomAnim);
                        fabStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        });


                        txt_SignUp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        });
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