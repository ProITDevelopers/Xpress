package ao.co.proitconsulting.xpress.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashScreenActivity extends AppCompatActivity {

    private Animation topAnim, bottomAnim;

    private ImageView imgAppLogo;
    private RelativeLayout relative_Start;
    private FloatingActionButton fabStart;
    private TextView txt_SignUp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initViews();

    }

    private void initViews() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation_splash);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation_splash);

        imgAppLogo = findViewById(R.id.imgAppLogo);
        relative_Start = findViewById(R.id.relative_Start);
        fabStart = findViewById(R.id.fabStart);
        txt_SignUp = findViewById(R.id.txt_SignUp);

        SpannableString spannableString = new SpannableString(getString(R.string.splash_sign_up_hint));
        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.login_register_text_color));
        spannableString.setSpan(new UnderlineSpan(),22,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(fcsGreen,22,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_SignUp.setText(spannableString);

        imgAppLogo.setAnimation(topAnim);
        delaySplashScreen();



    }

    private void delaySplashScreen() {

        Completable.timer(3, TimeUnit.SECONDS,
                AndroidSchedulers.mainThread())
                .subscribe(() -> {

                    if (AppPrefsSettings.getInstance().getAuthToken()==null) {

                        if (AppPrefsSettings.getInstance().getUser()!=null){
                            Intent intent = new Intent(SplashScreenActivity.this, LoginTemporarioActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            return;
                        }else {
                            relative_Start.setVisibility(View.VISIBLE);
                            relative_Start.setAnimation(bottomAnim);
                            fabStart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                }
                            });


                            txt_SignUp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                }
                            });



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