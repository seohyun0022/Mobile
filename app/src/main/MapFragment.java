package min.mjc.foodie;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;

import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import min.mjc.foodie.BottomSheet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String TAG = "MapFragment";
    private static final int REASON_GESTURE = 0; // 카메라 이동 사유: 사용자가 직접 이동

    private EditText etSearchQuery;
    private Button btnSearch;

    private NaverMap naverMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FusedLocationSource locationSource; // 위치 추적용 LocationSource
    private LatLng currentLocation; // 현재 위치 저장용 (초기 지도 로딩 시 현재 위치로 이동하는 데 사용)
    private boolean autoMoveToFirstResult = false;
    private boolean userMovedMap = false;

    private List<Marker> currentMarkers = new ArrayList<>();
    private InfoWindow currentOpenInfoWindow = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        etSearchQuery = view.findViewById(R.id.et_search_query);
        btnSearch = view.findViewById(R.id.btn_search);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // 위치 소스 초기화
        locationSource = new FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE);

        btnSearch.setOnClickListener(v -> {
            String query = etSearchQuery.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else if (naverMap != null) {
                LatLng mapCenter = naverMap.getCameraPosition().target;
                Log.d(TAG, "버튼 클릭 시 지도 중심: " + mapCenter.latitude + ", " + mapCenter.longitude);
                Log.d(TAG, "검색 좌표: " + mapCenter.latitude + ", " + mapCenter.longitude);

                autoMoveToFirstResult = true;
                userMovedMap = false;
                    // 랜드마크 검색 대신 좌표 -> 주소 검색 후 메인 검색을 시작합니다.
                reverseGeocodeAndSearch(query, mapCenter); // <-- 변경된 부분
                Log.d(TAG, "Search button clicked. Searching based on map center: " + mapCenter.latitude + ", " + mapCenter.longitude);
                }

        });

        FragmentManager fm = getChildFragmentManager();
        com.naver.maps.map.MapFragment naverMapFragment = (com.naver.maps.map.MapFragment) fm.findFragmentById(R.id.map_container);
        if (naverMapFragment == null) {
            naverMapFragment = com.naver.maps.map.MapFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.map_container, naverMapFragment)
                    .commit();
        }
        naverMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        Log.d(TAG, "NaverMap is ready.");

        // 위치 소스와 추적 모드 설정
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow); // 위치 따라가기 모드

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(false);

        // 사용자가 지도를 수동으로 이동했는지 감지
        naverMap.addOnCameraChangeListener((reason, animated) -> {
            LatLng currentCenter = naverMap.getCameraPosition().target;
            Log.d(TAG, "Camera moved! Reason: " + reason);

            if (reason == REASON_GESTURE) {
                userMovedMap = true;
                Log.d(TAG, "사용자가 제스처로 지도를 이동했습니다.");
            }
        });
    }

    // --- 위치 권한 및 현재 위치 가져오기 관련 로직 (초기 지도 로딩 시 사용) ---
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // 위치 권환 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource != null && locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                            if (naverMap != null) naverMap.moveCamera(cameraUpdate);
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

    /**
     * 현재 지도 중심 좌표를 기반으로 주소를 검색하고, 그 주소로 원래 검색어를 보강하여 최종 검색을 시작합니다.
     * @param originalQuery 사용자가 입력한 원래 검색어
     * @param mapCenter 현재 지도의 중심 좌표
     */
    private void reverseGeocodeAndSearch(String originalQuery, LatLng mapCenter) {
        OkHttpClient client = new OkHttpClient();

        String formattedLongitude = String.format("%.4f", mapCenter.longitude);
        String formattedLatitude = String.format("%.4f", mapCenter.latitude);
        final LatLng fixedMapCenter = mapCenter;

        String coords = formattedLongitude + "," + formattedLatitude;
        String encodedCoords;
        try {
            encodedCoords = URLEncoder.encode(coords, "UTF-8");
        } catch (IOException e) {
            Log.e(TAG, "Coords encoding failed for reverse geocode: " + e.getMessage());
            Toast.makeText(requireContext(), "좌표 인코딩 실패", Toast.LENGTH_SHORT).show();
            searchPlace(originalQuery, fixedMapCenter);
            return;
        }

        StringBuilder urlBuilder = new StringBuilder("https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=")
                .append(encodedCoords)
                .append("&output=json")
                .append("&orders=roadaddr,admcode")
                .append("&coord=wgs84");

        String url = urlBuilder.toString();
        Log.d(TAG, "Naver Reverse Geocode API URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-NCP-APIGW-API-KEY-ID", getString(R.string.naver_map_client_id))
                .addHeader("X-NCP-APIGW-API-KEY", getString(R.string.naver_map_client_secret))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Reverse Geocode API call failed: " + e.getMessage(), e);
                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "주소 검색 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        searchPlace(originalQuery, fixedMapCenter);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    final String errorBody = response.body().string();
                    Log.e(TAG, "Reverse Geocode API Response Error: " + response.code() + " - " + errorBody);
                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "주소 검색 API 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                            searchPlace(originalQuery, fixedMapCenter);
                        });
                    }
                    return;
                }

                final String jsonData = response.body().string();
                Log.d(TAG, "Reverse Geocode API Response: " + jsonData);

                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        String augmentedQuery = originalQuery;
                        String foundAddress = null;

                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);
                            JSONObject v2Object = jsonObject.optJSONObject("v2");
                            if (v2Object != null) {
                                JSONObject addressObject = v2Object.optJSONObject("address");
                                if (addressObject != null) {
                                    String directRoadAddress = addressObject.optString("roadAddress");
                                    if (directRoadAddress != null && !directRoadAddress.isEmpty()) {
                                        foundAddress = directRoadAddress;
                                        Log.d(TAG, "Direct roadAddress found: " + foundAddress);
                                    }
                                }

                                if (foundAddress == null || foundAddress.isEmpty()) {
                                    JSONArray results = v2Object.optJSONArray("results");
                                    if (results != null) {
                                        for (int i = 0; i < results.length(); i++) {
                                            JSONObject result = results.getJSONObject(i);
                                            if ("roadaddr".equals(result.optString("name"))) {
                                                JSONObject region = result.optJSONObject("region");
                                                JSONObject land = result.optJSONObject("land");

                                                StringBuilder fullAddress = new StringBuilder();
                                                if (region != null) {
                                                    fullAddress.append(region.optJSONObject("area1").optString("name")).append(" ");
                                                    fullAddress.append(region.optJSONObject("area2").optString("name")).append(" ");
                                                    fullAddress.append(region.optJSONObject("area3").optString("name")).append(" ");
                                                }
                                                if (land != null) {
                                                    fullAddress.append(land.optString("name")).append(" ");
                                                    fullAddress.append(land.optString("number1"));
                                                    if (!land.optString("number2").isEmpty()) {
                                                        fullAddress.append("-").append(land.optString("number2"));
                                                    }
                                                }
                                                foundAddress = fullAddress.toString().trim();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (foundAddress != null && !foundAddress.isEmpty()) {
                                augmentedQuery = originalQuery + " " + foundAddress;
                                Toast.makeText(requireContext(), "주소 '" + foundAddress + "'를 포함하여 검색합니다.", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Augmented Query: " + augmentedQuery);
                            } else {
                                Log.d(TAG, "No address found. Using original query.");
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
                            Toast.makeText(requireContext(), "주소 파싱 오류. 기본 검색어로 검색합니다.", Toast.LENGTH_SHORT).show();
                        } finally {
                            Log.d(TAG, "[finally] searchPlace 호출: " + augmentedQuery + ", 좌표: " + fixedMapCenter.latitude + ", " + fixedMapCenter.longitude);
                            searchPlace(augmentedQuery, fixedMapCenter);
                        }
                    });
                }
            }
        });
    }


    // searchPlace는 이제 랜드마크 정보가 추가된 검색어를 받을 수 있습니다.
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
        }

        String url = urlBuilder.toString();
        Log.d(TAG, "Final Search API URL: " + url);

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
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "API 호출 실패: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "API response error: " + response.code());
                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "API 응답 오류: " + response.code(), Toast.LENGTH_LONG).show());
                    }
                    return;
                }

                final String jsonData = response.body().string();
                Log.d(TAG, "Search API Response: " + jsonData);

                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() -> DisplayResults(jsonData));
                }
            }
        });
    }

    private void DisplayResults(String jsonResponse) {
        Log.d(TAG, "playResults method entered.");
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

                String title = item.getString("title").replaceAll("<.*?>", ""); // HTML 태그 제거
                String category = item.getString("category");
                String roadAddress = item.getString("roadAddress");
                String link = item.getString("link");

                long longMapx = item.getLong("mapx");
                long longMapy = item.getLong("mapy");

                double mapx = longMapx / 10_000_000.0;
                double mapy = longMapy / 10_000_000.0;

                LatLng latLng = new LatLng(mapy, mapx);

                Log.d(TAG, "Converted LatLng for " + title + ": Lat=" + latLng.latitude + ", Lng=" + latLng.longitude);

                Marker marker = new Marker();
                marker.setPosition(latLng);
                marker.setMap(naverMap);

                InfoWindow infoWindow = new InfoWindow();
                infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(requireContext()) {
                    @NonNull
                    @Override
                    public CharSequence getText(@NonNull InfoWindow infoWindow) {
                        return title + "\n" + roadAddress;
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

                    String description = "카테고리: " + category + "\n주소: " + roadAddress;
                    BottomSheet bottomSheet = BottomSheet.newInstance(
                            title,
                            description,
                            link,
                            latLng.latitude,
                            latLng.longitude
                    );
                    bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());

                    return true;
                });

                currentMarkers.add(marker);

                if (firstResultLatLng == null) {
                    firstResultLatLng = latLng;
                }
            }
            Log.d(TAG, "Finished processing all items.");

            if (firstResultLatLng != null) {
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(firstResultLatLng)
                        .animate(CameraAnimation.Easing, 1000);
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