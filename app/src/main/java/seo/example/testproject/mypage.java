package seo.example.testproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class mypage extends AppCompatActivity {

    private TextView userName;
    private TextView userIntro;
    private TextView userAge;
    private TextView userLoc;
    private ActivityResultLauncher<Intent> profileSettingsLauncher;
    private RequestQueue requestQueue;
    private int userNumber = 1; // 사용자 번호 변수 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mypage_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Volley RequestQueue 초기화
        requestQueue = Volley.newRequestQueue(this);

        // TextView 초기화
        userName = findViewById(R.id.user_name);
        userIntro = findViewById(R.id.user_intro);
        userAge = findViewById(R.id.user_age);
        userLoc = findViewById(R.id.user_loc);

        // API에서 사용자 정보 가져오기
        fetchUserData();

        // 프로필 설정 결과 처리
        profileSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String newName = result.getData().getStringExtra("new_name");
                        String newIntro = result.getData().getStringExtra("new_intro");
                        String newAge = result.getData().getStringExtra("new_age");
                        String newLoc = result.getData().getStringExtra("new_loc");

                        if (newName != null) userName.setText(newName);
                        if (newIntro != null) userIntro.setText(newIntro);
                        if (newAge != null) {
                            userAge.setText(newAge);
                            if (newAge.equals("")) userAge.setVisibility(View.GONE);
                            else userAge.setVisibility(View.VISIBLE);
                        }
                        if (newLoc != null) {
                            userLoc.setText(newLoc);
                            if (newLoc.equals("")) userLoc.setVisibility(View.GONE);
                            else userLoc.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        // 프로필 설정 버튼
        AppCompatButton menuProfileSettings = findViewById(R.id.menu_profile_settings);
        menuProfileSettings.setOnClickListener(v -> {
            Intent intent = new Intent(mypage.this, user_setting.class);
            // 현재 프로필 정보 전달
            intent.putExtra("current_name", userName.getText().toString());
            intent.putExtra("current_intro", userIntro.getText().toString());
            intent.putExtra("current_age", userAge.getText().toString());
            intent.putExtra("current_loc", userLoc.getText().toString());
            profileSettingsLauncher.launch(intent);
        });

        // 리뷰 리스트 메뉴
        TextView menuReviewList = findViewById(R.id.menu_review_list);
        menuReviewList.setOnClickListener(v -> {
            Intent intent = new Intent(mypage.this, my_review.class);
            intent.putExtra("user_number", userNumber);
            startActivity(intent);
        });

        // 팔로워 목록 메뉴
        TextView menuFollowers = findViewById(R.id.menu_followers);
        menuFollowers.setOnClickListener(v -> {
            Intent intent = new Intent(mypage.this, follow_layout.class);
            intent.putExtra("selected_tab", "followers");
            intent.putExtra("user_number", userNumber);
            startActivity(intent);
        });

        // 팔로잉 목록 메뉴
        TextView menuFollowing = findViewById(R.id.menu_following);
        menuFollowing.setOnClickListener(v -> {
            Intent intent = new Intent(mypage.this, follow_layout.class);
            intent.putExtra("selected_tab", "following");
            intent.putExtra("user_number", userNumber);
            startActivity(intent);
        });
    }

    private void fetchUserData() {
        // 에뮬레이터에서는 localhost 대신 10.0.2.2를 사용
        String url = "http://10.0.2.2:8080/api/users/number/" + userNumber;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // API 응답에서 데이터 추출
                        String userNameStr = response.getString("userName");
                        String userEmail = response.getString("email");
                        String userPhone = response.getString("phoneNumber");
                        String userGender = response.getString("gender");
                        String userBirth = response.getString("dateOfBirth");

                        // UI 업데이트
                        userName.setText(userNameStr);
                        userIntro.setText(userEmail); // 이메일을 소개로 사용
                        
                        // 나이 계산 (dateOfBirth에서)
                        String[] birthParts = userBirth.split("-");
                        int birthYear = Integer.parseInt(birthParts[0]);
                        int currentYear = java.time.Year.now().getValue();
                        int age = currentYear - birthYear;
                        userAge.setText(age + "세");

                        // 성별 표시
                        String genderText = userGender.equals("M") ? "남성" : "여성";
                        userLoc.setText(genderText);

                    } catch (Exception e) {
                        Toast.makeText(mypage.this, "데이터 파싱 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mypage.this, "API 호출 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );

        // 요청을 큐에 추가
        requestQueue.add(jsonObjectRequest);
    }

    // 사용자 번호를 설정하는 메서드 추가
    public void setUserNumber(int number) {
        this.userNumber = number;
        fetchUserData(); // 사용자 번호가 변경되면 데이터 다시 가져오기
    }
}