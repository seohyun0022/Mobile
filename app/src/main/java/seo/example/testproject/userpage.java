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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class userpage extends AppCompatActivity {

    private TextView userName;
    private TextView userId;
    private TextView userIntro;
    private AppCompatButton menuProfileSettings;
    private LinearLayout reviewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.userpage_activity);

        // View 초기화
        userName = findViewById(R.id.user_name);
        userId = findViewById(R.id.user_id);
        userIntro = findViewById(R.id.user_intro);
        menuProfileSettings = findViewById(R.id.menu_profile_settings);
        reviewContainer = findViewById(R.id.review_container);

        // Intent에서 전달받은 데이터 가져오기
        String name = getIntent().getStringExtra("user_name");
        String id = getIntent().getStringExtra("user_id");

        // 데이터 표시
        if (name != null) {
            userName.setText(name);
        }
        if (id != null) {
            userId.setText(id);
            // 팔로잉 상태 확인
            checkFollowingStatus(id);
            // 리뷰 데이터 로드
            loadUserReviews(id);
        }
    }

    private void checkFollowingStatus(String userId) {
        try {
            String jsonString = loadJSONFromAsset();
            if (jsonString == null) {
                showError("사용자 데이터를 불러올 수 없습니다.");
                return;
            }

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray followingArray = jsonObject.getJSONArray("following");

            boolean isFollowing = false;
            for (int i = 0; i < followingArray.length(); i++) {
                JSONObject user = followingArray.getJSONObject(i);
                if (user.getString("id").equals(userId)) {
                    isFollowing = true;
                    break;
                }
            }

            menuProfileSettings.setText(isFollowing ? "팔로우 해제" : "팔로우 하기");

        } catch (Exception e) {
            e.printStackTrace();
            menuProfileSettings.setText("팔로우 하기");
        }
    }

    private void loadUserReviews(String userId) {
        try {
            String jsonString = loadJSONFromAsset();
            if (jsonString == null) {
                showError("리뷰 데이터를 불러올 수 없습니다.");
                return;
            }

            JSONObject json = new JSONObject(jsonString);
            JSONArray reviews = json.getJSONArray("reviews");

            // 각 리뷰에 대해 아이템 생성
            for (int i = 0; i < reviews.length(); i++) {
                JSONObject review = reviews.getJSONObject(i);

                // 리뷰 아이템 레이아웃 인플레이트
                LayoutInflater inflater = LayoutInflater.from(this);
                ConstraintLayout reviewItem = (ConstraintLayout) inflater.inflate(R.layout.review_item, reviewContainer, false);

                // 리뷰 데이터 설정
                TextView placeName = reviewItem.findViewById(R.id.place_name);
                RatingBar rating = reviewItem.findViewById(R.id.review_rating);
                TextView reviewText = reviewItem.findViewById(R.id.review_text);
                ImageView reviewImage = reviewItem.findViewById(R.id.review_image);

                try {
                    placeName.setText(review.getString("place_name"));
                    rating.setRating((float) review.getDouble("rating"));
                    reviewText.setText(review.getString("review_text"));

                    // 이미지 설정
                    String imageUrl = review.getString("image_url");
                    // TODO: Glide나 Picasso를 사용하여 이미지 로딩 구현
                    reviewImage.setImageResource(R.drawable.user);
                } catch (JSONException e) {
                    showError("리뷰 데이터 형식이 올바르지 않습니다.");
                    continue;
                }

                // 컨테이너에 아이템 추가
                reviewContainer.addView(reviewItem);
            }
        } catch (JSONException e) {
            showError("리뷰 데이터를 처리하는 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("user_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}