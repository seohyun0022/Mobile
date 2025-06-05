package seo.example.testproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        bottomNavigationView = findViewById(R.id.bottomNavi);

        fragmentManager = getSupportFragmentManager();

        // 앱 시작 시 초기 탭 (검색/지도) 로드
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_frame_container, new MapFragment())
                    .commit();
            Log.d(TAG, "Initial MapFragment loaded into main_frame_container.");
        }

        // 하단 메뉴 선택 처리
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();

                if (id == R.id.fragment_search) {
                    Log.d(TAG, "Search tab clicked. Loading MapFragment.");
                    selectedFragment = new MapFragment();
                } else if (id == R.id.fragment_notifications) {
                    Log.d(TAG, "Notifications tab clicked.");
                    Toast.makeText(NavigationActivity.this, "알림 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    return true;

                } else if(id == R.id.fragment_profile){
                    Log.d(TAG, "Profile tab clicked. Launching UserPageActivity.");
                    Intent intent = new Intent(NavigationActivity.this, userpage.class);
                    startActivity(intent);
                    return true;
                }

                if (selectedFragment != null) {
                    Log.d(TAG, "Replacing fragment with: " + selectedFragment.getClass().getSimpleName());
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.map_frame, selectedFragment)
                            .commit();
                    return true; // 프래그먼트 교체 완료
                }
                Log.w(TAG, "Unhandled navigation item ID: " + item.getItemId());
                return false; // 이벤트 처리 실패
            }
        });
    }
}
