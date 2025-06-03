package min.mjc.foodie;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText etId, etPassword, etPasswordConfirm, etEmail;
    Spinner spinnerGender;
    Button btnCheckId, btnSendCode, btnSignup, btnCancel;
    DatePicker etBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        etBirth = findViewById(R.id.etBirth);
        etEmail = findViewById(R.id.etEmail);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnCheckId = findViewById(R.id.btnCheckId);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnSignup = findViewById(R.id.btnSignup);
        btnCancel = findViewById(R.id.btnCancel);

        //생년월일 받아오기
        int year = etBirth.getYear();
        int month = etBirth.getMonth() + 1;
        int day = etBirth.getDayOfMonth();

        // 성별 선택 초기화
        String[] genders = {"남성", "여성"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // 취소 버튼 클릭 시 뒤로 가기
        btnCancel.setOnClickListener(v -> finish());

        // 중복확인
        btnCheckId.setOnClickListener(v -> {
            String userId = etId.getText().toString();
            // DB 연결
        });

        // 인증번호 받기
        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            Toast.makeText(this, "인증번호 전송 기능은 추후 구현 예정", Toast.LENGTH_SHORT).show();
        });

        // 회원가입 버튼 클릭
        btnSignup.setOnClickListener(v -> {
            //가입 성공 시
            Intent intent = new Intent(SignupActivity.this, SignupCompleteActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
