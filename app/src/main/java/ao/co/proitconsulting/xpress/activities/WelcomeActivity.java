package ao.co.proitconsulting.xpress.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ao.co.proitconsulting.xpress.R;

public class WelcomeActivity extends AppCompatActivity {

    private FloatingActionButton fabStart;
    private TextView txt_SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initViews();
    }

    private void initViews() {

        fabStart = findViewById(R.id.fabStart);
        txt_SignUp = findViewById(R.id.txt_SignUp);

        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHomeScreen();

            }
        });


        SpannableString spannableString = new SpannableString(getString(R.string.splash_sign_up_hint));
        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.login_register_text_color));
        spannableString.setSpan(new UnderlineSpan(),22,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(fcsGreen,22,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_SignUp.setText(spannableString);
        txt_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });


    }


    private void launchHomeScreen() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

    }
}