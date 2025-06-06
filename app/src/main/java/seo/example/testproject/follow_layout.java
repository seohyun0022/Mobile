package seo.example.testproject;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
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
    private RequestQueue requestQueue;
    private int userNumber;

    private static class UserData {
        int userNumber;
        String userName;
        String id;
        String email;
        String phoneNumber;
        Character gender;
        String dateOfBirth;

        UserData(int userNumber, String userName, String id, String email, String phoneNumber, Character gender, String dateOfBirth) {
            this.userNumber = userNumber;
            this.userName = userName;
            this.id = id;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.gender = gender;
            this.dateOfBirth = dateOfBirth;
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

        // Volley RequestQueue 초기화
        requestQueue = Volley.newRequestQueue(this);

        // View 초기화
        textFollowers = findViewById(R.id.text_followers);
        textFollowing = findViewById(R.id.text_following);
        container = findViewById(R.id.container);
        inflater = LayoutInflater.from(this);

        // 사용자 번호 가져오기
        userNumber = getIntent().getIntExtra("user_number", 1);

        // 리스트 초기화
        followersList = new ArrayList<>();
        followingList = new ArrayList<>();

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

    private void loadInitialData(boolean isFollowingSelected) {
        container.removeAllViews();
        if (isFollowingSelected) {
            loadFollowingData();
        } else {
            loadFollowersData();
        }
    }

    private void loadFollowersData() {
        String url = "http://10.0.2.2:8080/api/users/"+userNumber+"/followers";
        Log.d("FollowLayout", "Attempting to load followers from URL: " + url);
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    Log.d("FollowLayout", "Response received: " + response.toString());
                    followersList.clear();
                    JSONArray usersArray = response.getJSONArray("users");
                    
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject user = usersArray.getJSONObject(i);
                        UserData userData = new UserData(
                            user.getInt("userNumber"),
                            user.getString("userName"),
                            user.getString("id"),
                            user.getString("email"),
                            user.isNull("phoneNumber") ? "" : user.getString("phoneNumber"),
                            user.isNull("gender") ? null : user.getString("gender").charAt(0),
                            user.getString("dateOfBirth")
                        );
                        followersList.add(userData);
                    }
                    
                    displayUserList(followersList);
                } catch (Exception e) {
                    Log.e("FollowLayout", "Error parsing followers data", e);
                    Toast.makeText(this, "데이터 파싱 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e("FollowLayout", "Network error: " + error.toString());
                if (error.networkResponse != null) {
                    Log.e("FollowLayout", "Error code: " + error.networkResponse.statusCode);
                    Log.e("FollowLayout", "Error data: " + new String(error.networkResponse.data));
                }
                Toast.makeText(this, "API 호출 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );

        // 타임아웃 설정
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
            10000, // 타임아웃 10초
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }

    private void loadFollowingData() {
        String url = "http://10.0.2.2:8080/api/users/"+userNumber+"/followings";
        Log.d("FollowLayout", "Attempting to load following from URL: " + url);
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    Log.d("FollowLayout", "Response received: " + response.toString());
                    followingList.clear();
                    JSONArray usersArray = response.getJSONArray("users");
                    
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject user = usersArray.getJSONObject(i);
                        UserData userData = new UserData(
                            user.getInt("userNumber"),
                            user.getString("userName"),
                            user.getString("id"),
                            user.getString("email"),
                            user.isNull("phoneNumber") ? "" : user.getString("phoneNumber"),
                            user.isNull("gender") ? null : user.getString("gender").charAt(0),
                            user.getString("dateOfBirth")
                        );
                        followingList.add(userData);
                    }
                    
                    displayUserList(followingList);
                } catch (Exception e) {
                    Log.e("FollowLayout", "Error parsing following data", e);
                    Toast.makeText(this, "데이터 파싱 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e("FollowLayout", "Network error: " + error.toString());
                if (error.networkResponse != null) {
                    Log.e("FollowLayout", "Error code: " + error.networkResponse.statusCode);
                    Log.e("FollowLayout", "Error data: " + new String(error.networkResponse.data));
                }
                Toast.makeText(this, "API 호출 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );

        // 타임아웃 설정
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
            10000, // 타임아웃 10초
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }

    private void displayUserList(List<UserData> userList) {
        container.removeAllViews();

        for (UserData user : userList) {
            View itemView = inflater.inflate(R.layout.follow_item, container, false);

            TextView nameView = itemView.findViewById(R.id.follow_name);
            TextView idView = itemView.findViewById(R.id.follow_id);

            nameView.setText(user.userName);
            idView.setText(user.id);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(follow_layout.this, userpage.class);
                intent.putExtra("user_name", user.userName);
                intent.putExtra("user_id", user.id);
                intent.putExtra("user_number", user.userNumber);
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
