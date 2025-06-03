package seo.example.testproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient; // 위치 서비스 클라이언트
import com.google.android.gms.location.LocationServices; // 위치 서비스
import com.google.android.gms.tasks.OnSuccessListener; // 위치 성공 리스너

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128; // TM128 좌표 변환을 위한 클래스
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String TAG = "MainActivity";

    private EditText etSearchQuery;
    private Button btnSearch;
    private FrameLayout map_container;

    private NaverMap naverMap;
    private FusedLocationProviderClient fusedLocationClient; // 위치 서비스 클라이언트
    private LatLng currentLocation; // 현재 위치를 저장할 변수

    // 지도에 표시된 마커들을 관리하기 위한 리스트
    private List<Marker> currentMarkers = new ArrayList<>();
    // 현재 열려있는 InfoWindow를 추적 (하나만 열리도록)
    private InfoWindow currentOpenInfoWindow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etSearchQuery = findViewById(R.id.et_search_query);
        btnSearch = findViewById(R.id.btn_search);
        map_container = findViewById(R.id.map_container);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnSearch.setOnClickListener(v -> {
            String query = etSearchQuery.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(MainActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 검색 버튼 클릭 시 현재 위치 권한 확인 후 검색 시작
                checkLocationPermissionAndSearch(query);
            }
        });

        // MapFragment 초기화
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.map_container, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this);
    }

    // 지도 준비 완료 콜백
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        Log.d(TAG, "NaverMap is ready.");

        // UI 설정 (현위치 버튼 활성화)
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true); // 현위치 버튼 활성화
        uiSettings.setCompassEnabled(false); // 나침반 비활성화 (선택 사항)

        LatLng gwanghwamun = new LatLng(37.572367, 126.977000); // 광화문역 위도, 경도

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(gwanghwamun)
                .animate(CameraAnimation.Easing, 1000); // 1초 동안 부드럽게 이동
        naverMap.moveCamera(cameraUpdate);

        // 사용자 현재 위치 가져오기 시작
        // getMyLocation();
    }

    // --- 위치 권한 및 현재 위치 가져오기 관련 로직 ---

    private void checkLocationPermissionAndSearch(String query) {
        // 위치 권한이 있는지 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // 권한이 있으면 현재 위치 가져온 후 검색
            getMyLocationAndSearch(query);
        } else {
            // 권한이 없으면 사용자에게 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여되면 현재 위치 가져온 후 검색 (사용자가 검색 버튼을 눌렀을 때)
                // 또는 지도 초기 로드 시 현위치로 이동
                getMyLocationAndSearch(etSearchQuery.getText().toString().trim()); // 검색어 전달
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다. 주변 검색 기능을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getMyLocation() {
        // 지도 현위치 버튼 활성화를 위해 NaverMap 객체가 필요합니다.
        if (naverMap == null) {
            Log.e(TAG, "NaverMap is not ready yet.");
            return;
        }

        // 권한이 없는 경우
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청 (onCreate나 onMapReady에서 호출하는 대신 사용자 액션에 따라 호출하는 것이 좋음)
            // 여기서는 단순히 권한이 없음을 로그로 남깁니다.
            Log.w(TAG, "Location permission not granted. Cannot get current location.");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d(TAG, "현재 위치: " + currentLocation.latitude + ", " + currentLocation.longitude);
                            // 현재 위치로 카메라 이동
                            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(currentLocation)
                                    .animate(CameraAnimation.Fly, 1500);
                            naverMap.moveCamera(cameraUpdate);
                        } else {
                            Log.w(TAG, "Last known location is null. Cannot get current location.");
                            Toast.makeText(MainActivity.this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getMyLocationAndSearch(String query) {
        // 권한 확인은 이미 checkLocationPermissionAndSearch에서 했으므로 다시 안 해도 됩니다.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치 권한이 없어 현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d(TAG, "현재 위치 (검색 기준): " + currentLocation.latitude + ", " + currentLocation.longitude);
                            // 현재 위치를 기준으로 검색 API 호출
                            searchPlace(query, currentLocation);
                        } else {
                            Log.w(TAG, "Last known location is null. Searching without specific coordinate.");
                            Toast.makeText(MainActivity.this, "현재 위치를 가져올 수 없어 일반 검색을 시작합니다.", Toast.LENGTH_SHORT).show();
                            // 현재 위치를 가져오지 못하면 좌표 없이 검색
                            searchPlace(query, null);
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Failed to get location: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "위치 가져오기 실패. 일반 검색을 시작합니다.", Toast.LENGTH_SHORT).show();
                    searchPlace(query, null); // 위치 가져오기 실패 시 일반 검색
                });
    }


    // --- 장소 검색 API 호출 로직 ---
    private void searchPlace(String query, LatLng coordinate) {
        OkHttpClient client = new OkHttpClient();
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (IOException e) {
            Log.e(TAG, "Query encoding failed: " + e.getMessage());
            Toast.makeText(this, "검색어 인코딩 실패", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder urlBuilder = new StringBuilder("https://openapi.naver.com/v1/search/local.json?query=")
                .append(encodedQuery)
                .append("&display=5"); // 검색 결과 5개만 요청

        // 현재 위치 좌표가 있다면 검색 API에 포함
        if (coordinate != null) {
            // 네이버 검색 API는 경도(X),위도(Y) 순서로 coordinate 파라미터를 받습니다.
            // LatLng 객체는 위도(latitude), 경도(longitude) 순서이므로 주의
            urlBuilder.append("&coordinate=")
                    .append(coordinate.longitude) // 경도 (X)
                    .append(",")
                    .append(coordinate.latitude);  // 위도 (Y)
            Log.d(TAG, "Searching with coordinate: " + coordinate.longitude + "," + coordinate.latitude);
        }

        String url = urlBuilder.toString();
        Log.d(TAG, "Naver Search API URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Naver-Client-Id", getString(R.string.naver_client_id))
                .addHeader("X-Naver-Client-Secret", getString(R.string.naver_client_secret))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "API call failed: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "API 호출 실패: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonData = response.body().string();
                    Log.d(TAG, "API Response: " + jsonData);
                    runOnUiThread(() -> ParseAndDisplayResults(jsonData));
                } else {
                    final String errorBody = response.body().string();
                    Log.e(TAG, "API Response Error: " + response.code() + " - " + errorBody);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "API 응답 오류: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    // --- 검색 결과 파싱 및 지도 표시 로직 ---

    private void ParseAndDisplayResults(String jsonResponse) {
        Log.d(TAG, "ParseAndDisplayResults method entered.");
        if (naverMap == null) {
            Log.e(TAG, "NaverMap is null, cannot add markers.");
            Toast.makeText(this, "지도가 준비되지 않아 마커를 표시할 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }
        // 기존 마커 및 InfoWindow 제거
        ClearExistingMarkers();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            Log.d(TAG, "JSONObject created successfully."); //
            JSONArray items = jsonObject.getJSONArray("items");

            if (items.length() == 0) {
                Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 첫 번째 검색 결과의 위치로 카메라를 이동하기 위한 변수 (만약 현재 위치가 없으면)
            LatLng firstResultLatLng = null;



            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                String title = item.getString("title").replaceAll("<.*?>", ""); // HTML 태그 제거
                String address = item.getString("roadAddress"); // 도로명 주소
                long rawLongitude = item.getLong("mapx"); // x 좌표 (경도)
                long rawLatitude = item.getLong("mapy");  // y 좌표 (위도)
                double longitude = rawLongitude / 10000000.0;
                double latitude = rawLatitude / 10000000.0;

                // LatLng 객체를 직접 생성합니다.
                LatLng latLng = new LatLng(latitude, longitude); // LatLng은 (위도, 경도) 순서입니다!
                // *******************************************************************

                // 로그를 통해 최종 변환된 위경도 값을 확인합니다.
                Log.d(TAG, "Raw WGS84 (long): X=" + rawLongitude + ", Y=" + rawLatitude);
                Log.d(TAG, "Final LatLng for " + title + ": Lat=" + latLng.latitude + ", Lng=" + latLng.longitude);

                // 마커 생성 및 설정
                Marker marker = new Marker();
                marker.setPosition(latLng);
                marker.setMap(naverMap); // 지도에 마커 추가
                Log.d(TAG, "마커 추가 시도: " + title + " at " + latLng.latitude + ", " + latLng.longitude);

                // InfoWindow 생성 및 설정
                InfoWindow infoWindow = new InfoWindow();
                infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
                    @NonNull
                    @Override
                    public CharSequence getText(@NonNull InfoWindow infoWindow) {
                        return title + "\n" + address;
                    }
                });

                // 마커 클릭 리스너 설정
                marker.setOnClickListener(overlay -> {
                    // 현재 열려있는 InfoWindow가 있다면 닫기
                    if (currentOpenInfoWindow != null) {
                        currentOpenInfoWindow.close();
                    }

                    infoWindow.open(marker); // 클릭된 마커에 InfoWindow 열기
                    currentOpenInfoWindow = infoWindow; // 현재 열린 InfoWindow 추적

                    // 클릭된 마커 위치로 카메라 이동 (선택 사항)
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(marker.getPosition())
                            .animate(CameraAnimation.Easing, 500);
                    naverMap.moveCamera(cameraUpdate);

                    return true;
                });

                currentMarkers.add(marker); // 생성된 마커 리스트에 추가

                // 현재 위치가 설정되지 않았거나 (첫 로드 시) 첫 번째 검색 결과로 카메라 이동
                if (currentLocation == null && i == 0) {
                    firstResultLatLng = latLng;
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(firstResultLatLng)
                            .animate(CameraAnimation.Fly, 1500);
                    naverMap.moveCamera(cameraUpdate);
                }
            }
            Log.d(TAG, "Finished processing all items."); // 루프 종료 로그
            // 검색 결과가 있지만 현재 위치가 없어 초기 카메라 이동이 안된 경우 (예외 처리)
            if (currentLocation == null && firstResultLatLng != null) {
                // 이미 위에서 처리되지만, 혹시 모를 경우를 대비한 안전 장치
                // naverMap.moveCamera(CameraUpdate.scrollTo(firstResultLatLng).animate(CameraAnimation.Fly, 1500));
            }
            Marker marker = new Marker();
            marker.setPosition(new LatLng(37.5670135, 126.9783740));
            marker.setMap(naverMap);


        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
            Toast.makeText(this, "검색 결과 파싱 오류: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // TM128 좌표를 LatLng(위경도)로 변환하는 메서드
    private LatLng convertTM128ToLatLng(int mapx, int mapy) {
        // 네이버 지도 SDK의 Tm128 클래스를 사용하여 위도/경도로 변환
        Tm128 tm = new Tm128(mapx, mapy);
        return tm.toLatLng(); // 이 메서드가 정확한 변환을 수행합니다.
    }

    // 지도에 표시된 모든 마커와 열려있는 InfoWindow를 제거하는 메서드
    private void ClearExistingMarkers() {
        for (Marker marker : currentMarkers) {
            marker.setMap(null); // 지도에서 마커 제거
        }
        currentMarkers.clear(); // 리스트 비우기

        if (currentOpenInfoWindow != null ) {
            currentOpenInfoWindow.close();
            currentOpenInfoWindow = null;
        }
        Log.d(TAG, "Cleared all existing markers and info window.");
    }
}