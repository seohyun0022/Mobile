package seo.example.testproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class user_setting extends AppCompatActivity {

    private EditText editName;
    private EditText editIntro;
    private Spinner edit_age;
    private Spinner edit_loc;
    private AppCompatButton menuProfileSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_setting_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // EditText 초기화
        editName = findViewById(R.id.edit_name);
        editIntro = findViewById(R.id.edit_intro);
        edit_age = findViewById(R.id.edit_age);
        edit_loc = findViewById(R.id.edit_loc);
        menuProfileSettings = findViewById(R.id.menu_profile_settings);

        String[] age_items = {"", "10대", "20대", "30대", "40대", "50대", "60대"};
        String[] loc_items = {"", "서울", "경기"};

        // Spinner 어댑터 설정
        ArrayAdapter<String> age_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, age_items);
        age_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_age.setAdapter(age_adapter);

        ArrayAdapter<String> loc_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loc_items);
        loc_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_loc.setAdapter(loc_adapter);

        // 현재 프로필 정보 가져오기
        Intent intent = getIntent();
        if (intent != null) {
            String currentName = intent.getStringExtra("current_name");
            String currentIntro = intent.getStringExtra("current_intro");
            String currentAge = intent.getStringExtra("current_age");
            String current_loc = intent.getStringExtra("current_loc");

            if (currentName != null) editName.setText(currentName);
            if (currentIntro != null) editIntro.setText(currentIntro);
            if (currentAge != null) {
                // currentAge에 해당하는 인덱스 찾기
                for (int i = 0; i < age_items.length; i++) {
                    if (age_items[i].equals(currentAge)) {
                        edit_age.setSelection(i);
                        break;
                    }
                }
            }
            if (current_loc != null) {
                for (int i = 0; i < loc_items.length; i++) {
                    if (loc_items[i].equals(current_loc)) {
                        edit_loc.setSelection(i);
                        break;
                    }
                }
            }
        }

        // 저장 버튼 클릭 이벤트
        menuProfileSettings.setOnClickListener(v -> {
            String newName = editName.getText().toString();
            String newIntro = editIntro.getText().toString();
            String newAge = edit_age.getSelectedItem().toString();
            String newLoc = edit_loc.getSelectedItem().toString();

            // 결과를 MainActivity로 전달
            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_name", newName);
            resultIntent.putExtra("new_intro", newIntro);
            resultIntent.putExtra("new_age", newAge);
            resultIntent.putExtra("new_loc", newLoc);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}