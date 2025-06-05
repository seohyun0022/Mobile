package seo.example.testproject;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BottomSheet extends BottomSheetDialogFragment {

    public static BottomSheet newInstance(String name, String desc, String url, double lat, double lng) {
        BottomSheet fragment = new BottomSheet();
        Bundle args = new Bundle();
        args.putString("store_name", name);
        args.putString("store_desc", desc);
        args.putString("store_url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        TextView storeInfo = view.findViewById(R.id.store_info);
        Button btnOpenUrl = view.findViewById(R.id.btn_open_url);
        ViewPager2 viewPager = view.findViewById(R.id.image_viewpager);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("store_name", "이름 없음");
            String desc = args.getString("store_desc", "정보 없음");

            String info = name + "\n" + desc;
            storeInfo.setText(info);

            fetchStoreImages(name, viewPager);

            btnOpenUrl.setOnClickListener(v -> {
                String keywordEncoded;
                try {
                    keywordEncoded = URLEncoder.encode(name.replaceAll("<.*?>", ""), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    keywordEncoded = name;
                }

                String mapUrl = "https://map.naver.com/v5/search/" + keywordEncoded;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
                startActivity(intent);
            });
        }

        return view;
    }

    private void fetchStoreImages(String keyword, ViewPager2 viewPager) {
        new Thread(() -> {
            try {
                String query = URLEncoder.encode(keyword, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/search/image?query=" + query + "&display=5";

                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", "");
                con.setRequestProperty("X-Naver-Client-Secret", "");

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray items = jsonObject.getJSONArray("items");
                List<String> imageUrls = new ArrayList<>();

                for (int i = 0; i < items.length(); i++) {
                    imageUrls.add(items.getJSONObject(i).getString("link"));
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        viewPager.setAdapter(new ImagePagerAdapter(imageUrls));
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("IMAGE_FETCH", "이미지 검색 실패: " + e.getMessage());
            }
        }).start();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog sheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet =
                    sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(dpToPx(100));
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        return dialog;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}
