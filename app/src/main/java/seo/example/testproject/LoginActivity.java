package seo.example.testproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences; // SharedPreferences 임포트

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity"; // Logcat 태그
    private static final String BASE_URL = "http://10.0.2.2:8080"; // Spring Boot 서버 URL

    EditText etLoginId, etLoginPassword;
    Button btnLogin, btnCancelLogin;

    private RequestQueue requestQueue; // Volley RequestQueue

    // SharedPreferences 키 정의
    private static final String PREFS_NAME = "MyAuthPrefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_NUMBER = "user_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI 요소 초기화
        etLoginId = findViewById(R.id.etLoginId);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancelLogin = findViewById(R.id.btnCancelLogin);

        // Volley RequestQueue 초기화
        requestQueue = Volley.newRequestQueue(this);

        // 취소 버튼 클릭 시 액티비티 종료
        btnCancelLogin.setOnClickListener(v -> finish());

        // 로그인 버튼 클릭 리스너
        btnLogin.setOnClickListener(v -> {
            String id = etLoginId.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            // 입력 유효성 검사
            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 로그인 API 호출 메서드
            performLogin(id, password);
        });
    }

    /**
     * 로그인 API를 호출하여 사용자 인증을 시도합니다.
     * @param id 사용자 아이디
     * @param password 사용자 비밀번호
     */
    private void performLogin(String id, String password) {
        String url = BASE_URL + "/api/auth/login";

        // 요청 본문 (JSON) 생성
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
            requestBody.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "Login JSON creation error: " + e.getMessage());
            Toast.makeText(LoginActivity.this, "로그인 요청 데이터 생성 오류", Toast.LENGTH_SHORT).show();
            return;
        }

        // JsonObjectRequest를 사용하여 POST 요청 전송
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 로그인 성공 응답 처리
                        try {
                            String token = response.getString("token");
                            String userId = response.getString("userId");
                            String userName = response.getString("username"); // Spring Boot 응답 필드에 따라
                            int userNumber = response.getInt("userNumber");

                            // 토큰 및 사용자 정보 SharedPreferences에 저장
                            saveAuthInfo(token, userId, userName, userNumber);

                            Toast.makeText(LoginActivity.this, userName + "님 환영합니다!", Toast.LENGTH_SHORT).show();

                            // 메인 액티비티로 이동
                            Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                            startActivity(intent);
                            finish(); // 현재 로그인 액티비티 종료
                        } catch (JSONException e) {
                            Log.e(TAG, "Login Response JSON parsing error: " + e.getMessage());
                            Toast.makeText(LoginActivity.this, "로그인 응답 파싱 오류", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 로그인 실패 응답 처리
                        String errorMessage = "로그인 실패";
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            try {
                                JSONObject errorJson = new JSONObject(responseBody);
                                if (errorJson.has("message")) {
                                    errorMessage = errorJson.getString("message");
                                } else if (errorJson.has("error")) {
                                    errorMessage = errorJson.getString("error");
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Error Response JSON parsing error: " + e.getMessage());
                            }
                            errorMessage += " (HTTP " + error.networkResponse.statusCode + ")";
                        } else if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        } else {
                            errorMessage = "알 수 없는 로그인 오류 발생";
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Login error: " + errorMessage, error);
                    }
                });

        // 요청 큐에 추가
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * SharedPreferences에 인증 정보 (토큰, 사용자 ID 등)를 저장
     * @param token 발급받은 JWT 토큰
     * @param userId 사용자 ID
     * @param userName 사용자 이름
     * @param userNumber 사용자 번호
     */
    private void saveAuthInfo(String token, String userId, String userName, int userNumber) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_JWT_TOKEN, token);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putInt(KEY_USER_NUMBER, userNumber);
        editor.apply(); // 비동기 저장
        Log.d(TAG, "Auth info saved: Token=" + token + ", UserID=" + userId);
    }


    public static String getJwtToken(Context context) { // Context가 필요하므로 static으로 변경
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_JWT_TOKEN, null);
    }


     //SharedPreferences에서 저장된 사용자 번호를 가져옵니다.
     // @return 저장된 사용자 번호, 없으면 -1 반환

    public static int getUserNumber(Context context) { // Context가 필요하므로 static으로 변경
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(KEY_USER_NUMBER, -1);
    }


    public static void clearAuthInfo(Context context) { // Context가 필요하므로 static으로 변경
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Log.d(TAG, "Auth info cleared.");
    }
}
