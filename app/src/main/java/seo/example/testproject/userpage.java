package seo.example.testproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class userpage extends AppCompatActivity {

    private TextView userName;
    private TextView userId;
    private TextView userIntro;
    private AppCompatButton menuProfileSettings;
    private LinearLayout reviewContainer;
    private RequestQueue requestQueue;
    private int userNumber;
    private int currentUserNumber = 1; // 현재 로그인한 사용자의 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.userpage_activity);

        // Volley RequestQueue 초기화
        requestQueue = Volley.newRequestQueue(this);

        // View 초기화
        userName = findViewById(R.id.user_name);
        userId = findViewById(R.id.user_id);
        userIntro = findViewById(R.id.user_intro);
        menuProfileSettings = findViewById(R.id.menu_profile_settings);
        reviewContainer = findViewById(R.id.review_container);

        // Intent에서 전달받은 데이터 가져오기
        userNumber = getIntent().getIntExtra("user_number", 1);

        // 사용자 정보 로드
        loadUserData();

        // 팔로우 상태 확인
        checkFollowingStatus();

        // 리뷰 데이터 로드
        loadUserReviews();
    }

    private void loadUserData() {
        String url = "http://10.0.2.2:8080/api/users/number/" + userNumber;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
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
                    userId.setText(response.getString("id"));

                } catch (Exception e) {
                    Toast.makeText(userpage.this, "데이터 파싱 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Toast.makeText(userpage.this, "API 호출 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void checkFollowingStatus() {
        String url = "http://10.0.2.2:8080/api/users/" + currentUserNumber + "/followings";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    JSONArray usersArray = response.getJSONArray("users");
                    boolean isFollowing = false;

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject user = usersArray.getJSONObject(i);
                        if (user.getInt("userNumber") == userNumber) {
                            isFollowing = true;
                            break;
                        }
                    }

                    menuProfileSettings.setText(isFollowing ? "팔로우 해제" : "팔로우 하기");
                    menuProfileSettings.setOnClickListener(v -> toggleFollow(isFollowing));

                } catch (Exception e) {
                    Toast.makeText(userpage.this, "팔로우 상태 확인 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Toast.makeText(userpage.this, "API 호출 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void toggleFollow(boolean isFollowing) {
        String url = "http://10.0.2.2:8080/api/users/" + currentUserNumber + "/follow/" + userNumber;
        int method = isFollowing ? Request.Method.DELETE : Request.Method.POST;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            method,
            url,
            null,
            response -> {
                // 팔로우 상태 업데이트 후 UI 갱신
                checkFollowingStatus();
            },
            error -> {
                Toast.makeText(userpage.this, "팔로우 처리 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void loadUserReviews() {
        String url = "http://10.0.2.2:8080/api/reviews/user/" + userNumber;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    reviewContainer.removeAllViews(); // 기존 뷰 제거

                    // 각 리뷰에 대해 아이템 생성
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject review = response.getJSONObject(i);

                        // 리뷰 아이템 레이아웃 인플레이트
                        LayoutInflater inflater = LayoutInflater.from(this);
                        ConstraintLayout reviewItem = (ConstraintLayout) inflater.inflate(R.layout.review_item, reviewContainer, false);

                        // 리뷰 데이터 설정
                        TextView placeName = reviewItem.findViewById(R.id.place_name);
                        RatingBar rating = reviewItem.findViewById(R.id.review_rating);
                        TextView reviewText = reviewItem.findViewById(R.id.review_text);
                        ImageView reviewImage = reviewItem.findViewById(R.id.review_image);

                        try {
                            // 스프링 서버 응답 형식에 맞게 데이터 설정
                            placeName.setText(review.getString("storeName"));
                            reviewText.setText(review.getString("body"));

                            // 이미지 설정
                            String imagePath = review.isNull("imagePath") ? null : review.getString("imagePath");
                            if (imagePath != null && !imagePath.isEmpty()) {
                                // TODO: Glide나 Picasso를 사용하여 이미지 로딩 구현
                                reviewImage.setImageResource(R.drawable.user);
                            } else {
                                reviewImage.setImageResource(R.drawable.user);
                            }

                            // 리뷰 아이템 클릭 이벤트
                            reviewItem.setOnClickListener(v -> {
                                // TODO: 리뷰 상세 페이지로 이동하는 기능 구현
                                Toast.makeText(userpage.this, "리뷰 상세 페이지로 이동", Toast.LENGTH_SHORT).show();
                            });

                        } catch (JSONException e) {
                            Toast.makeText(userpage.this, "리뷰 데이터 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                            continue;
                        }

                        // 컨테이너에 아이템 추가
                        reviewContainer.addView(reviewItem);
                    }
                } catch (JSONException e) {
                    Toast.makeText(userpage.this, "리뷰 데이터를 처리하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            },
            error -> {
                Toast.makeText(userpage.this, "API 호출 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );

        // 타임아웃 설정
        jsonArrayRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
            10000, // 타임아웃 10초
            com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonArrayRequest);
    }
}