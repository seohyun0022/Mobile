package redcube.android.viewmodel;

import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class userpage extends AppCompatActivity {

    private TextView userName;
    private TextView userId;
    private AppCompatButton menuProfileSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.userpage_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // View 초기화
        userName = findViewById(R.id.user_name);
        userId = findViewById(R.id.user_id);
        menuProfileSettings = findViewById(R.id.menu_profile_settings);

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
        }
    }

    private void checkFollowingStatus(String userId) {
        try {
            // JSON 파일 읽기
            InputStream is = getAssets().open("user_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            // JSON 파싱
            JSONObject jsonObject = new JSONObject(json);
            JSONArray followingArray = jsonObject.getJSONArray("following");

            // 팔로잉 목록에서 현재 사용자 ID 검색
            boolean isFollowing = false;
            for (int i = 0; i < followingArray.length(); i++) {
                JSONObject user = followingArray.getJSONObject(i);
                if (user.getString("id").equals(userId)) {
                    isFollowing = true;
                    break;
                }
            }

            // 버튼 텍스트 설정
            menuProfileSettings.setText(isFollowing ? "팔로우 해제" : "팔로우 하기");

        } catch (Exception e) {
            Log.e("MainActivity4", "Error checking following status", e);
            menuProfileSettings.setText("팔로우 하기");
        }
    }
}