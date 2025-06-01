package redcube.android.viewmodel;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.content.Intent;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class follow_layout extends AppCompatActivity {

    private TextView textFollowers;
    private TextView textFollowing;
    private LinearLayout container;
    private LayoutInflater inflater;
    private static final String SELECTED_COLOR = "#A2E9A6";
    private List<UserData> followersList;
    private List<UserData> followingList;

    private static class UserData {
        String name;
        String id;

        UserData(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.follow_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // View 초기화
        textFollowers = findViewById(R.id.text_followers);
        textFollowing = findViewById(R.id.text_following);
        container = findViewById(R.id.container);
        inflater = LayoutInflater.from(this);

        // 데이터 로드
        loadUserData();

        // 선택된 탭 정보 가져오기
        String selectedTab = getIntent().getStringExtra("selected_tab");
        boolean isFollowingSelected = "following".equals(selectedTab);

        // 초기 상태 설정
        setTabSelected(textFollowers, !isFollowingSelected);
        setTabSelected(textFollowing, isFollowingSelected);

        // 초기 데이터 로드
        loadInitialData(isFollowingSelected);

        // 팔로워 탭 클릭 이벤트
        textFollowers.setOnClickListener(v -> {
            setTabSelected(textFollowers, true);
            setTabSelected(textFollowing, false);
            loadFollowersData();
        });

        // 팔로잉 탭 클릭 이벤트
        textFollowing.setOnClickListener(v -> {
            setTabSelected(textFollowers, false);
            setTabSelected(textFollowing, true);
            loadFollowingData();
        });
    }

    private void loadUserData() {
        try {
            InputStream is = getAssets().open("user_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(json);
            followersList = new ArrayList<>();
            followingList = new ArrayList<>();

            // 팔로워 데이터 파싱
            JSONArray followersArray = jsonObject.getJSONArray("followers");
            for (int i = 0; i < followersArray.length(); i++) {
                JSONObject user = followersArray.getJSONObject(i);
                followersList.add(new UserData(
                    user.getString("name"),
                    user.getString("id")
                ));
            }

            // 팔로잉 데이터 파싱
            JSONArray followingArray = jsonObject.getJSONArray("following");
            for (int i = 0; i < followingArray.length(); i++) {
                JSONObject user = followingArray.getJSONObject(i);
                followingList.add(new UserData(
                    user.getString("name"),
                    user.getString("id")
                ));
            }
        } catch (Exception e) {
            Log.e("MainActivity3", "Error loading user data", e);
        }
    }

    private void loadInitialData(boolean isFollowingSelected) {
        container.removeAllViews(); // 기존 뷰 제거
        if (isFollowingSelected) {
            loadFollowingData();
        } else {
            loadFollowersData();
        }
    }

    private void loadFollowersData() {
        container.removeAllViews(); // 기존 뷰 제거

        for (UserData user : followersList) {
            View itemView = inflater.inflate(R.layout.follow_item, container, false);

            TextView nameView = itemView.findViewById(R.id.follow_name);
            TextView idView = itemView.findViewById(R.id.follow_id);

            nameView.setText(user.name);         // 이름 표시
            idView.setText(user.id);            // ID 표시

            // 항목 클릭 이벤트 추가
            final String name = user.name;
            final String id = user.id;
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(follow_layout.this, userpage.class);
                intent.putExtra("user_name", name);
                intent.putExtra("user_id", id);
                startActivity(intent);
            });

            container.addView(itemView);
        }
    }

    private void loadFollowingData() {
        container.removeAllViews(); // 기존 뷰 제거

        for (UserData user : followingList) {
            View itemView = inflater.inflate(R.layout.follow_item, container, false);

            TextView nameView = itemView.findViewById(R.id.follow_name);
            TextView idView = itemView.findViewById(R.id.follow_id);

            nameView.setText(user.name);         // 이름 표시
            idView.setText(user.id);            // ID 표시

            // 항목 클릭 이벤트 추가
            final String name = user.name;
            final String id = user.id;
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(follow_layout.this, userpage.class);
                intent.putExtra("user_name", name);
                intent.putExtra("user_id", id);
                startActivity(intent);
            });

            container.addView(itemView);
        }
    }

    private void setTabSelected(TextView tab, boolean isSelected) {
        if (isSelected) {
            tab.setTextColor(Color.parseColor(SELECTED_COLOR));
            tab.setBackgroundResource(android.R.color.white);
        } else {
            tab.setTextColor(Color.BLACK);
            tab.setBackgroundResource(android.R.color.transparent);
        }
    }
}