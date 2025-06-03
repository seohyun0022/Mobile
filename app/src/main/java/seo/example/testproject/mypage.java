package seo.example.testproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class mypage extends AppCompatActivity {

    private TextView userName;
    private TextView userIntro;
    private TextView userAge;
    private TextView userLoc;
    private ActivityResultLauncher<Intent> profileSettingsLauncher;

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

        // TextView 초기화
        userName = findViewById(R.id.user_name);
        userIntro = findViewById(R.id.user_intro);
        userAge = findViewById(R.id.user_age);
        userLoc = findViewById(R.id.user_loc);

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
           // Intent intent = new Intent(mypage.this, .class);
            //startActivity(intent);
        });

        // 팔로워 목록 메뉴
        TextView menuFollowers = findViewById(R.id.menu_followers);
        menuFollowers.setOnClickListener(v -> {
            Intent intent = new Intent(mypage.this, follow_layout.class);
            intent.putExtra("selected_tab", "followers");
            startActivity(intent);
        });

        // 팔로잉 목록 메뉴
        TextView menuFollowing = findViewById(R.id.menu_following);
        menuFollowing.setOnClickListener(v -> {
            Intent intent = new Intent(mypage.this, follow_layout.class);
            intent.putExtra("selected_tab", "following");
            startActivity(intent);
        });
    }
}