package seo.example.testproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.util.FusedLocationSource;
// 사용x

/**
 * public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
 *     private NavigationActivity navigationActivity;
 *     private NaverMap naverMap;
 *     private FusedLocationSource locationSource;
 *     private SearchActivity searchActivity;
 *
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.layout.activity_main);
 *
 *         // Naver Map 초기화
 *         MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frame);
 *         if (mapFragment == null) {
 *             mapFragment = MapFragment.newInstance();
 *             getSupportFragmentManager().beginTransaction().add(R.id.map_frame, mapFragment).commit();
 *         }
 *         mapFragment.getMapAsync(this);
 *
 *         // 검색 버튼 이벤트 설정
 *         EditText etSearch = findViewById(R.id.et_search_query);
 *         Button btnSearch = findViewById(R.id.btn_search);
 *
 *         btnSearch.setOnClickListener(v -> {
 *             String query = etSearch.getText().toString();
 *             if (!query.isEmpty() && naverMap != null) {
 *                 if (searchActivity == null) {
 *                     searchActivity = new SearchActivity(this, naverMap);
 *                 }
 *                 searchActivity.searchPlace(query);
 *             }
 *         });
 *     }
 *
 *     @Override
 *     public void onMapReady(@NonNull NaverMap map) {
 *         this.naverMap = map;
 *         locationSource = new FusedLocationSource(this, 1000);
 *         map.setLocationSource(locationSource);
 *     }
 * }
 */
