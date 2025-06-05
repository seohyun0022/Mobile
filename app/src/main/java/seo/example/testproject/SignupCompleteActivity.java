package seo.example.testproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;



public class SignupCompleteActivity extends AppCompatActivity {

    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_complete);

        btnStart = findViewById(R.id.btnStart);

        // 시작하기 클릭 시 네비게이션이
        btnStart.setOnClickListener(v -> {

            finish();
        });

    }
}
