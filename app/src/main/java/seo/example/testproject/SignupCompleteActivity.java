package seo.example.testproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import seo.example.testproject.MainActivity;

public class SignupCompleteActivity extends AppCompatActivity {

    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_complete);

        btnStart = findViewById(R.id.btnStart);

        // 시작하기 클릭 시 메인화면(MainActivity)으로 이동
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(SignupCompleteActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

    }
}
