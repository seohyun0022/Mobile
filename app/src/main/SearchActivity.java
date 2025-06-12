package min.mjc.foodie;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity {

    private final Context context;
    private final NaverMap naverMap;

    public SearchActivity(Context context, NaverMap map) {
        this.context = context;
        this.naverMap = map;
    }

    public void searchPlace(String keyword, LatLng coordinate) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");

                StringBuilder urlBuilder = new StringBuilder("https://openapi.naver.com/v1/search/local.json?query=")
                        .append(encodedKeyword)
                        .append("&display=5&start=1&sort=random");

                if (coordinate != null) {
                    urlBuilder.append("&coordinate=")
                            .append(coordinate.longitude) // 경도 (mapx)
                            .append(",")
                            .append(coordinate.latitude);  // 위도 (mapy)
                    Log.d("Search", "검색 좌표: " + coordinate.latitude + ", " + coordinate.longitude);
                } else {
                    Log.w("Search", "coordinate가 null입니다. 중심 좌표 없이 검색합니다.");
                }

                Request request = new Request.Builder()
                        .url(urlBuilder.toString())
                        .get()
                        .addHeader("X-Naver-Client-Id", "5Uyf6Gnmt9QJFgs8_vfC")
                        .addHeader("X-Naver-Client-Secret", "Dd9Eg30O1R")
                        .build();

                Response response = client.newCall(request).execute();
                String result = Objects.requireNonNull(response.body()).string();

                Log.d("Search", "응답 결과: " + result);

                if (!response.isSuccessful()) {
                    Log.e("Search", "HTTP 오류 코드: " + response.code());
                    return;
                }

                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.has("items")) {
                    Log.e("Search", "검색 결과 없음 또는 잘못된 응답 형식");
                    return;
                }

                JSONArray items = jsonObject.getJSONArray("items");

                ((Activity) context).runOnUiThread(() -> {
                    naverMap.getUiSettings().setLocationButtonEnabled(true);
                    LatLng firstResultLatLng = null;

                    for (int i = 0; i < items.length(); i++) {
                        try {
                            JSONObject item = items.getJSONObject(i);
                            String title = item.optString("title").replaceAll("<.*?>", "");
                            String address = item.optString("roadAddress");
                            String urlLink = item.optString("link");
                            double lat = item.getDouble("mapy") / 1E7;
                            double lng = item.getDouble("mapx") / 1E7;

                            LatLng latLng = new LatLng(lat, lng);

                            Marker marker = new Marker();
                            marker.setPosition(latLng);
                            marker.setMap(naverMap);

                            marker.setOnClickListener(overlay -> {
                                BottomSheet bottomSheet = new BottomSheet();
                                Bundle args = new Bundle();
                                args.putString("store_name", title);
                                args.putString("store_desc", address);
                                args.putString("store_url", urlLink);
                                args.putDouble("store_lat", lat);
                                args.putDouble("store_lng", lng);
                                bottomSheet.setArguments(args);
                                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                                return true;
                            });

                            // 첫 번째 결과 위치 저장
                            if (firstResultLatLng == null) {
                                firstResultLatLng = latLng;
                            }

                        } catch (Exception e) {
                            Log.e("Search", "마커 처리 오류: " + e.getMessage());
                        }
                    }

                    // 첫 번째 결과로 카메라 이동
                    if (firstResultLatLng != null) {
                        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(firstResultLatLng)
                                .animate(CameraAnimation.Easing, 1000);
                        naverMap.moveCamera(cameraUpdate);
                        Log.d("Search", "카메라가 첫 번째 결과 위치로 이동함");
                    }
                });

            } catch (Exception e) {
                Log.e("Search", "검색 실패: " + e.getMessage());
            }
        }).start();
    }

}