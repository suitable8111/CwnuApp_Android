package com.yeho.cwnuapp.intro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yeho.cwnuapp.MainLoginActivity;
import com.yeho.cwnuapp.R;

public class IntroActivity extends AppCompatActivity {

    private Button kakaoLoginBtn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        kakaoLoginBtn = (Button)findViewById(R.id.intro_go_kakao_btn);

        kakaoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, MainLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
