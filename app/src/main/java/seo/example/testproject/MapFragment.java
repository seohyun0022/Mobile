package seo.example.testproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment; // androidx.fragment.app.Fragment 임포트 (현재 MapFragment가 상속받는 클래스)
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
// import com.naver.maps.map.MapFragment as NaverMapFragment; // <--- 이 줄을 제거했습니다. (Java에서 지원 안함)
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
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


public class MapFragment extends Fragment implements OnMapReadyCallback { // 이 MapFragment는 seo.example.testproject.MapFragment 입니다.

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String TAG = "MapFragment";

    private EditText etSearchQuery;
    private Button btnSearch;

    private NaverMap naverMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;

    private List<Marker> currentMarkers = new ArrayList<>();
    private InfoWindow currentOpenInfoWindow = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        etSearchQuery = view.findViewById(R.id.et_search_query);
        btnSearch = view.findViewById(R.id.btn_search);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        btnSearch.setOnClickListener(v -> {
            String query = etSearchQuery.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                checkLocationPermissionAndSearch(query);
            }
        });

        // Naver Map 초기화
        FragmentManager fm = getChildFragmentManager();
        // Naver 지도 SDK의 MapFragment를 정규화된 이름으로 참조
        com.naver.maps.map.MapFragment naverMapFragment = (com.naver.maps.map.MapFragment) fm.findFragmentById(R.id.map_container);
        if (naverMapFragment == null) {
            naverMapFragment = com.naver.maps.map.MapFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.map_container, naverMapFragment)
                    .commit();
        }
        naverMapFragment.getMapAsync(this); // OnMapReadyCallback 연결

        return view;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        Log.d(TAG, "NaverMap is ready.");

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(false);

        getMyLocation();
    }

    // --- 위치 권한 및 현재 위치 가져오기 관련 로직 (기존과 동일) ---
    private void checkLocationPermissionAndSearch(String query) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMyLocationAndSearch(query);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (etSearchQuery != null && !etSearchQuery.getText().toString().trim().isEmpty()) {
                    getMyLocationAndSearch(etSearchQuery.getText().toString().trim());
                } else {
                    getMyLocation();
                }
            } else {
                Toast.makeText(requireContext(), "위치 권한이 거부되었습니다. 주변 검색 기능을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
                LatLng gwanghwamun = new LatLng(37.572367, 126.977000);
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(gwanghwamun).animate(CameraAnimation.Easing, 1000);
                if (naverMap != null) naverMap.moveCamera(cameraUpdate);
            }
        }
    }

    private void getMyLocation() {
        if (naverMap == null) {
            Log.e(TAG, "NaverMap is not ready yet.");
            return;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location permission not granted. Requesting permission.");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d(TAG, "현재 위치: " + currentLocation.latitude + ", " + currentLocation.longitude);
                            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(currentLocation)
                                    .animate(CameraAnimation.Fly, 1500);
                            naverMap.moveCamera(cameraUpdate);
                        } else {
                            Log.w(TAG, "Last known location is null. Cannot get current location, moving to default.");
                            Toast.makeText(requireContext(), "현재 위치를 가져올 수 없어 기본 위치로 이동합니다.", Toast.LENGTH_SHORT).show();
                            LatLng gwanghwamun = new LatLng(37.572367, 126.977000);
                            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(gwanghwamun).animate(CameraAnimation.Easing, 1000);
                            naverMap.moveCamera(cameraUpdate);
                        }
                    }
                })
                .addOnFailureListener(requireActivity(), e -> {
                    Log.e(TAG, "Failed to get location: " + e.getMessage());
                    Toast.makeText(requireContext(), "위치 가져오기 실패. 기본 위치로 이동합니다.", Toast.LENGTH_SHORT).show();
                    LatLng gwanghwamun = new LatLng(37.572367, 126.977000);
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(gwanghwamun).animate(CameraAnimation.Easing, 1000);
                    if (naverMap != null) naverMap.moveCamera(cameraUpdate);
                });
    }

    private void getMyLocationAndSearch(String query) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "위치 권한이 없어 현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d(TAG, "현재 위치 (검색 기준): " + currentLocation.latitude + ", " + currentLocation.longitude);
                            searchPlace(query, currentLocation);
                        } else {
                            Log.w(TAG, "Last known location is null. Searching without specific coordinate.");
                            Toast.makeText(requireContext(), "현재 위치를 가져올 수 없어 일반 검색을 시작합니다.", Toast.LENGTH_SHORT).show();
                            searchPlace(query, null);
                        }
                    }
                })
                .addOnFailureListener(requireActivity(), e -> {
                    Log.e(TAG, "Failed to get location: " + e.getMessage());
                    Toast.makeText(requireContext(), "위치 가져오기 실패. 일반 검색을 시작합니다.", Toast.LENGTH_SHORT).show();
                    searchPlace(query, null);
                });
    }

    private void searchPlace(String query, LatLng coordinate) {
        OkHttpClient client = new OkHttpClient();
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (IOException e) {
            Log.e(TAG, "Query encoding failed: " + e.getMessage());
            Toast.makeText(requireContext(), "검색어 인코딩 실패", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder urlBuilder = new StringBuilder("https://openapi.naver.com/v1/search/local.json?query=")
                .append(encodedQuery)
                .append("&display=5");

        if (coordinate != null) {
            urlBuilder.append("&coordinate=")
                    .append(coordinate.longitude)
                    .append(",")
                    .append(coordinate.latitude);
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
                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "API 호출 실패: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonData = response.body().string();
                    Log.d(TAG, "API Response: " + jsonData);
                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() -> ParseAndDisplayResults(jsonData));
                    }
                } else {
                    final String errorBody = response.body().string();
                    Log.e(TAG, "API Response Error: " + response.code() + " - " + errorBody);
                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "API 응답 오류: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show());
                    }
                }
            }
        });
    }

    private void ParseAndDisplayResults(String jsonResponse) {
        Log.d(TAG, "ParseAndDisplayResults method entered.");
        if (naverMap == null) {
            Log.e(TAG, "NaverMap is null, cannot add markers.");
            Toast.makeText(requireContext(), "지도가 준비되지 않아 마커를 표시할 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }
        ClearExistingMarkers();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            Log.d(TAG, "JSONObject created successfully.");
            JSONArray items = jsonObject.getJSONArray("items");

            if (items.length() == 0) {
                Toast.makeText(requireContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng firstResultLatLng = null;

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                String title = item.getString("title").replaceAll("<.*?>", "");
                String address = item.getString("roadAddress");
                long rawLongitude = item.getLong("mapx");
                long rawLatitude = item.getLong("mapy");
                LatLng latLng = new com.naver.maps.geometry.Tm128(rawLongitude, rawLatitude).toLatLng();

                Log.d(TAG, "Raw TM128: X=" + rawLongitude + ", Y=" + rawLatitude);
                Log.d(TAG, "Converted LatLng for " + title + ": Lat=" + latLng.latitude + ", Lng=" + latLng.longitude);

                Marker marker = new Marker();
                marker.setPosition(latLng);
                marker.setMap(naverMap);
                Log.d(TAG, "마커 추가 시도: " + title + " at " + latLng.latitude + ", " + latLng.longitude);

                InfoWindow infoWindow = new InfoWindow();
                infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(requireContext()) {
                    @NonNull
                    @Override
                    public CharSequence getText(@NonNull InfoWindow infoWindow) {
                        return title + "\n" + address;
                    }
                });

                marker.setOnClickListener(overlay -> {
                    if (currentOpenInfoWindow != null) {
                        currentOpenInfoWindow.close();
                    }

                    infoWindow.open(marker);
                    currentOpenInfoWindow = infoWindow;

                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(marker.getPosition())
                            .animate(CameraAnimation.Easing, 500);
                    naverMap.moveCamera(cameraUpdate);

                    return true;
                });

                currentMarkers.add(marker);

                if (firstResultLatLng == null) {
                    firstResultLatLng = latLng;
                }
            }
            Log.d(TAG, "Finished processing all items.");

            if (firstResultLatLng != null) {
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(firstResultLatLng).animate(CameraAnimation.Fly, 1500);
                naverMap.moveCamera(cameraUpdate);
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "검색 결과 파싱 오류: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void ClearExistingMarkers() {
        for (Marker marker : currentMarkers) {
            marker.setMap(null);
        }
        currentMarkers.clear();

        if (currentOpenInfoWindow != null ) {
            currentOpenInfoWindow.close();
            currentOpenInfoWindow = null;
        }
        Log.d(TAG, "Cleared all existing markers and info window.");
    }
}
