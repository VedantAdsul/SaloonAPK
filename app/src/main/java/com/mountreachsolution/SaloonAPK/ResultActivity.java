package com.mountreachsolution.SaloonAPK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mountreachsolution.SaloonAPK.databinding.ActivityResultBinding;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Bitmap bmp = BitmapHolder.getBitmap();
        String faceShape = getIntent().getStringExtra("faceShape");
        String gender = getIntent().getStringExtra("gender");
        String service = getIntent().getStringExtra("service");
        String debugInfo = getIntent().getStringExtra("debugInfo");

        if (bmp != null) {
            b.imgPreview.setImageBitmap(bmp);
        }
        b.tvFaceShape.setText(faceShape);

        if (debugInfo != null) {
            b.tvDebugInfo.setText(debugInfo);
            b.tvDebugInfo.setVisibility(View.VISIBLE);
        }

        setupSuggestions(faceShape, gender, service);

        b.btnTryAgain.setOnClickListener(v -> {
            Intent i = new Intent(ResultActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }

    private void setupSuggestions(String faceShape, String gender, String service) {
        boolean wantHair = service.contains("hair") || service.contains("both");
        boolean wantBeard = (gender.equals("male") || gender.equals("other")) && (service.contains("beard") || service.contains("both"));

        if (wantHair) {
            b.tvHairSuggestionsLabel.setVisibility(View.VISIBLE);
            b.rvHairSuggestions.setVisibility(View.VISIBLE);
            List<Suggestion> hairSuggestions = SuggestionRepository.getHairSuggestions(faceShape, gender);
            b.rvHairSuggestions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            b.rvHairSuggestions.setAdapter(new SuggestionAdapter(hairSuggestions));
        } else {
            b.tvHairSuggestionsLabel.setVisibility(View.GONE);
            b.rvHairSuggestions.setVisibility(View.GONE);
        }

        if (wantBeard) {
            b.tvBeardSuggestionsLabel.setVisibility(View.VISIBLE);
            b.rvBeardSuggestions.setVisibility(View.VISIBLE);
            List<Suggestion> beardSuggestions = SuggestionRepository.getBeardSuggestions(faceShape);
            b.rvBeardSuggestions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            b.rvBeardSuggestions.setAdapter(new SuggestionAdapter(beardSuggestions));
        } else {
            b.tvBeardSuggestionsLabel.setVisibility(View.GONE);
            b.rvBeardSuggestions.setVisibility(View.GONE);
        }
    }
}