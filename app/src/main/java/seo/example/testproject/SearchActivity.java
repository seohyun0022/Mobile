package seo.example.testproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;

import org.json.JSONArray;
import org.json.JSONObject;

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

    public void searchPlace(String keyword) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = "https://openapi.naver.com/v1/search/local.json?query=" + keyword + "&display=5&start=1&sort=random";
                Request request = new Request.Builder()
                        .url(url)
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
                    for (int i = 0; i < items.length(); i++) {
                        try {
                            JSONObject item = items.getJSONObject(i);
                            String title = item.optString("title").replaceAll("<.*?>", "");
                            String address = item.optString("roadAddress");
                            String urlLink = item.optString("link");
                            double lat = item.getDouble("mapy") / 1E7;
                            double lng = item.getDouble("mapx") / 1E7;

                            Marker marker = new Marker();
                            marker.setPosition(new LatLng(lat, lng));
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

                        } catch (Exception e) {
                            Log.e("Search", "마커 처리 오류: " + e.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                Log.e("Search", "검색 실패: " + e.getMessage());
            }
        }).start();
    }
}
