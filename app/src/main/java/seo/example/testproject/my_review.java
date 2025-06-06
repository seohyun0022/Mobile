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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class my_review extends AppCompatActivity {

    private RequestQueue requestQueue;
    private int userNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.review_layout);

        // Volley RequestQueue 초기화
        requestQueue = Volley.newRequestQueue(this);

        // userNumber 가져오기
        userNumber = getIntent().getIntExtra("user_number", 1);

        // 시스템 바 패딩 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 리뷰 데이터 로드
        loadReviews();
    }

    private void loadReviews() {
        String url = "http://10.0.2.2:8080/api/reviews/user/" + userNumber;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    // 리뷰 컨테이너 가져오기
                    LinearLayout container = findViewById(R.id.review_container);
                    container.removeAllViews(); // 기존 뷰 제거

                    // 각 리뷰에 대해 아이템 생성
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject review = response.getJSONObject(i);

                        // 리뷰 아이템 레이아웃 인플레이트
                        LayoutInflater inflater = LayoutInflater.from(this);
                        ConstraintLayout reviewItem = (ConstraintLayout) inflater.inflate(R.layout.review_item, container, false);

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
                                // 현재는 기본 이미지 사용
                                reviewImage.setImageResource(R.drawable.user);
                            } else {
                                reviewImage.setImageResource(R.drawable.user);
                            }

                            // 리뷰 아이템 클릭 이벤트
                            reviewItem.setOnClickListener(v -> {
                                // TODO: 리뷰 상세 페이지로 이동하는 기능 구현
                                Toast.makeText(my_review.this, "리뷰 상세 페이지로 이동", Toast.LENGTH_SHORT).show();
                            });

                        } catch (JSONException e) {
                            showError("리뷰 데이터 형식이 올바르지 않습니다.");
                            continue;
                        }

                        // 컨테이너에 아이템 추가
                        container.addView(reviewItem);
                    }
                } catch (JSONException e) {
                    showError("리뷰 데이터를 처리하는 중 오류가 발생했습니다.");
                    e.printStackTrace();
                }
            },
            error -> {
                showError("API 호출 오류: " + error.getMessage());
                if (error.networkResponse != null) {
                    showError("에러 코드: " + error.networkResponse.statusCode);
                }
            }
        );

        // 타임아웃 설정
        jsonArrayRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
            10000, // 타임아웃 10초
            com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // 요청을 큐에 추가
        requestQueue.add(jsonArrayRequest);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
