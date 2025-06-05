package seo.example.testproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.ContextCompat.startActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class SignupActivity extends AppCompatActivity {

    EditText etId, etPassword, etPasswordConfirm, etEmail, etUserName, etPhoneNumber;
    Spinner spinnerGender;
    Button btnCheckId, btnSendCode, btnSignup, btnCancel;
    DatePicker etBirth;

    private RequestQueue requestQueue;
    private static final String BASE_URL = "http://10.0.2.2:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etId = findViewById(R.id.etId);
        Log.d(TAG, "etId: " + (etId != null ? "found" : "NULL"));

        etPassword = findViewById(R.id.etPassword);
        Log.d(TAG, "etPassword: " + (etPassword != null ? "found" : "NULL"));

        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        Log.d(TAG, "etPasswordConfirm: " + (etPasswordConfirm != null ? "found" : "NULL"));

        etBirth = findViewById(R.id.etBirth);
        Log.d(TAG, "etBirth: " + (etBirth != null ? "found" : "NULL"));

        etEmail = findViewById(R.id.etEmail);
        Log.d(TAG, "etEmail: " + (etEmail != null ? "found" : "NULL"));

        spinnerGender = findViewById(R.id.spinnerGender);
        Log.d(TAG, "spinnerGender: " + (spinnerGender != null ? "found" : "NULL"));

        btnCheckId = findViewById(R.id.btnCheckId);
        Log.d(TAG, "btnCheckId: " + (btnCheckId != null ? "found" : "NULL"));

        btnSendCode = findViewById(R.id.btnSendCode);
        Log.d(TAG, "btnSendCode: " + (btnSendCode != null ? "found" : "NULL"));

        btnSignup = findViewById(R.id.btnSignup);
        Log.d(TAG, "btnSignup: " + (btnSignup != null ? "found" : "NULL"));

        btnCancel = findViewById(R.id.btnCancel);
        Log.d(TAG, "btnCancel: " + (btnCancel != null ? "found" : "NULL"));

        etUserName = findViewById(R.id.etUserName);
        Log.d(TAG, "etUserName: " + (etUserName != null ? "found" : "NULL"));

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        Log.d(TAG, "etPhoneNumber: " + (etPhoneNumber != null ? "found" : "NULL"));


        requestQueue = Volley.newRequestQueue(this); // 초기화


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
            String userId = etId.getText().toString().trim();
            if (userId.isEmpty()) {
                Toast.makeText(SignupActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            checkIdAvailability(userId); // ID 중복 확인 메서드 호출
        });

        // 인증번호 받기
        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(SignupActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(SignupActivity.this, "인증번호 전송 기능은 추후 구현 예정", Toast.LENGTH_SHORT).show();
            // 실제 이메일 인증 API 호출 로직은 여기에 추가됩니다.
        });

        // 회원가입 버튼 클릭 리스너 (회원가입 API 연동)
        btnSignup.setOnClickListener(v -> {
            // 모든 입력 값 가져오기
            String id = etId.getText().toString().trim();
            String password = etPassword.getText().toString();
            String passwordConfirm = etPasswordConfirm.getText().toString();
            String userName = etUserName.getText().toString().trim(); // 추가된 필드
            String email = etEmail.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim(); // 추가된 필드

            // DatePicker에서 생년월일 가져오기 및 포맷팅
            int year = etBirth.getYear();
            int month = etBirth.getMonth() + 1; // DatePicker 월은 0부터 시작
            int day = etBirth.getDayOfMonth();
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String dateOfBirth = sdf.format(calendar.getTime());

            // Spinner에서 성별 가져오기 (서버 DTO에 맞게 "M" 또는 "F"로 변환)
            String selectedGenderText = spinnerGender.getSelectedItem().toString();
            String gender = "";
            if (selectedGenderText.equals("남성")) {
                gender = "M";
            } else if (selectedGenderText.equals("여성")) {
                gender = "F";
            }

            // 클라이언트 측 유효성 검사
            if (id.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || userName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || gender.isEmpty()) {
                Toast.makeText(SignupActivity.this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(passwordConfirm)) {
                Toast.makeText(SignupActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 회원가입 API 호출 메서드 호출
            registerUser(id, password, userName, dateOfBirth, gender, email, phoneNumber);
        });
    }


    private void checkIdAvailability(String userId) {
        String url = BASE_URL + "/api/users/id/" + userId;


        // JsonObjectRequest 대신 StringRequest를 사용하여 404 Not Found 응답
        // 404는 아이디가 없다
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // 200 OK 응답이 오면 아이디가 이미 존재한다는 의미
                    Toast.makeText(SignupActivity.this, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // 404 Not Found (아이디 없음) 또는 다른 오류
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        Toast.makeText(SignupActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 다른 네트워크 오류 또는 서버 오류
                        Log.e("main", "checkIdAvailability: 아이디 중복 확인 중 오류 발생: " + error.getMessage(), error); // Logcat 태그 사용
                        Toast.makeText(SignupActivity.this, "아이디 중복 확인 중 오류 발생: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void registerUser(String id, String password, String userName, String dateOfBirth, String gender, String email, String phoneNumber) {
        String url = BASE_URL + "/api/users/register";

        // 요청 본문 (JSON) 생성
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
            requestBody.put("password", password);
            requestBody.put("userName", userName);
            requestBody.put("dateOfBirth", dateOfBirth);
            requestBody.put("gender", gender);
            requestBody.put("email", email);
            requestBody.put("phoneNumber", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(SignupActivity.this, "회원가입 데이터 생성 오류", Toast.LENGTH_SHORT).show();
            return;
        }

        // JsonObjectRequest를 사용하여 POST 요청 전송
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 회원가입 성공 응답 처리 (HTTP 201 Created)
                        Toast.makeText(SignupActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, SignupCompleteActivity.class);
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 회원가입 실패 응답 처리
                        String errorMessage = "회원가입 실패";
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            try {
                                JSONObject errorJson = new JSONObject(responseBody);
                                // Spring Boot에서 보낸 에러 메시지를 파싱
                                if (errorJson.has("message")) {
                                    errorMessage = errorJson.getString("message");
                                } else if (errorJson.has("error")) {
                                    errorMessage = errorJson.getString("error");
                                }
                            } catch (JSONException e) {
                                Log.e("TAG", "onErrorResponse: ", e);
                                errorMessage += ": 응답 파싱 오류";
                            }
                        } else if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }
                        Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        // 요청 큐에 추가
        requestQueue.add(jsonObjectRequest);
    }
}


