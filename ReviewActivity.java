package min.mjc.reviewtest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    private TextView storeName;
    private EditText reviewEditText;
    private ImageButton addPhotoButton;
    private ImageView previewImageView; // 썸네일 미리보기 용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        storeName = findViewById(R.id.storeName);
        reviewEditText = findViewById(R.id.reviewEditText);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        TextView closeButton = findViewById(R.id.closeButton);
        TextView doneButton = findViewById(R.id.doneButton);

        // (1) 가게 이름 받아오기
        String storeInfo = getIntent().getStringExtra("store_name");
        if (storeInfo != null && !storeInfo.isEmpty()) {
            storeName.setText(storeInfo);
        }

        // (2) 완료 버튼 클릭 시
        doneButton.setOnClickListener(v -> {
            String review = reviewEditText.getText().toString();
            Log.d("리뷰 작성", "내용: " + review);
            // 서버 업로드 등 작업 추가
        });

        // (4) 사진 추가
        addPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        });

        // (5) 썸네일 추가용 ImageView (코드에서 추가)
        previewImageView = new ImageView(this);
        previewImageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        ((LinearLayout) addPhotoButton.getParent()).addView(previewImageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            previewImageView.setImageURI(imageUri);
        }
    }
}
