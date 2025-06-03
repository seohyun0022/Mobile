package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.demo.utils.VolleySingleton; // VolleySingleton 임포트

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

import seo.example.testproject.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    // Spring Boot 서버의 로그인 API URL (실제 IP 주소와 포트로 변경하세요)
    private static final String LOGIN_API_URL = "http://YOUR_SPRINGBOOT_SERVER_IP:8080/api/auth/login";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    // 회원가입 텍스트뷰 (클릭 리스너 추가 가능)
    // private TextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI 요소 초기화
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        // registerTextView = findViewById(R.id.register_text);

        // 로그인 버튼 클릭 리스너 설정
        loginButton.setOnClickListener(v -> performLogin());

        // (선택 사항) 회원가입 텍스트 클릭 리스너
        // registerTextView.setOnClickListener(v -> {
        //     Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        //     startActivity(registerIntent);
        // });

        // 앱 시작 시 이미 로그인되어 있는지 확인
        checkLoginStatus();
    }

    /**
     * 앱 시작 시 저장된 로그인 정보가 있는지 확인하고, 있다면 바로 메인 화면으로 이동합니다.
     */
    private void checkLoginStatus() {
        try {
            SharedPreferences sharedPreferences = getEncryptedSharedPreferences();
            String authToken = sharedPreferences.getString("auth_token", null);

            if (authToken != null && !authToken.isEmpty()) {
                Log.d(TAG, "자동 로그인: 토큰 존재");
                navigateToMainActivity();
            } else {
                Log.d(TAG, "자동 로그인: 토큰 없음");
            }
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "EncryptedSharedPreferences 초기화 오류", e);
            Toast.makeText(this, "보안 저장소 초기화 오류", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 로그인 버튼 클릭 시 호출되는 메서드.
     * 사용자 입력을 검증하고 서버에 로그인 요청을 보냅니다.
     */
    private void performLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 로그인 요청을 위한 JSON 객체 생성
        JSONObject loginRequestJson = new JSONObject();
        try {
            loginRequestJson.put("id", username); // 서버의 DTO 필드명과 일치해야 함
            loginRequestJson.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "JSON 생성 오류: " + e.getMessage());
            Toast.makeText(this, "요청 데이터 생성 오류", Toast.LENGTH_SHORT).show();
            return;
        }

        // Volley JsonObjectRequest를 사용하여 서버에 로그인 요청
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_API_URL, loginRequestJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // 서버 응답에서 토큰 및 사용자 정보 파싱
                            // 예시: {"token": "jwt_token_string", "userId": "testuser", "username": "테스트사용자"}
                            String authToken = response.getString("token");
                            String userId = response.getString("userId");
                            String userName = response.getString("username"); // 서버에서 사용자 이름을 반환한다고 가정

                            // 로그인 성공 시 토큰과 사용자 정보를 안전하게 저장
                            saveLoginSession(userId, userName, authToken);

                            Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                            navigateToMainActivity(); // 메인 화면으로 이동
                        } catch (JSONException e) {
                            Log.e(TAG, "로그인 응답 파싱 오류: " + e.getMessage());
                            Toast.makeText(LoginActivity.this, "로그인 응답 형식 오류", Toast.LENGTH_SHORT).show();
                        } catch (GeneralSecurityException | IOException e) {
                            Log.e(TAG, "로그인 세션 저장 오류: " + e.getMessage());
                            Toast.makeText(LoginActivity.this, "로그인 정보 저장 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "로그인 실패";
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                JSONObject errorJson = new JSONObject(responseBody);
                                errorMessage = errorJson.optString("message", errorMessage); // 서버에서 보낸 에러 메시지
                                Log.e(TAG, "로그인 오류 응답: " + responseBody);
                            } catch (Exception e) {
                                Log.e(TAG, "에러 응답 파싱 오류: " + e.getMessage());
                            }
                        } else {
                            errorMessage = "네트워크 오류: " + error.getMessage();
                        }
                        Log.e(TAG, "로그인 요청 오류: " + errorMessage);
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        // Volley 요청 큐에 추가
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * 로그인 성공 시 사용자 세션 정보를 EncryptedSharedPreferences에 저장합니다.
     * @param userId 사용자 고유 ID
     * @param username 사용자 이름
     * @param authToken 서버에서 발급받은 인증 토큰
     */
    private void saveLoginSession(String userId, String username, String authToken) throws GeneralSecurityException, IOException {
        SharedPreferences sharedPreferences = getEncryptedSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", authToken);
        editor.putString("logged_in_user_id", userId);
        editor.putString("logged_in_username", username);
        editor.apply(); // 비동기 저장
        Log.d(TAG, "로그인 세션 저장 완료: " + userId);
    }

    /**
     * EncryptedSharedPreferences 인스턴스를 가져옵니다.
     * @return EncryptedSharedPreferences 인스턴스
     */
    private SharedPreferences getEncryptedSharedPreferences() throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        return EncryptedSharedPreferences.create(
                "user_session_prefs", // Shared Preferences 파일 이름
                masterKeyAlias,
                this, // Context
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    /**
     * MainActivity로 이동하고 현재 액티비티를 종료합니다.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // 이전 액티비티 스택을 모두 지우고 새로운 태스크 시작 (뒤로가기 시 로그인 화면으로 돌아가지 않도록)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }
}
