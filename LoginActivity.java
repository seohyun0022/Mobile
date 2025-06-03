package min.mjc.foodie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etLoginId, etLoginPassword;
    Button btnLogin, btnCancelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginId = findViewById(R.id.etLoginId);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancelLogin = findViewById(R.id.btnCancelLogin);

        // 취소 버튼 클릭 시 종료
        btnCancelLogin.setOnClickListener(v -> finish());

        // 로그인 버튼 클릭
        btnLogin.setOnClickListener(v -> {
            // 로그인 성공 시
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }
}