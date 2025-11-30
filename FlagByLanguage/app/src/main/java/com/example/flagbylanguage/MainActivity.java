package com.example.flagbylanguage;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView imageFlagAuto;
    private ImageView imageFlagGlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageFlagAuto = findViewById(R.id.imageFlagAuto);
        imageFlagGlide = findViewById(R.id.imageFlagGlide);

        // === СПОСОБ 1: Автоматически через drawable-ru/uk/es ===

        // === СПОСОБ 2: Программно через Glide ===
        String language = Locale.getDefault().getLanguage(); // "uk", "es", "en"
        String flagUrl;

        switch (language) {
            case "uk":
                flagUrl = "https://flagcdn.com/w1280/ua.png";
                break;
            case "es":
                flagUrl = "https://flagcdn.com/w1280/es.png";
                break;
            default: // en
                flagUrl = "https://flagcdn.com/w1280/us.png";
                break;
        }

        imageFlagGlide.setVisibility(ImageView.VISIBLE);
        imageFlagAuto.setVisibility(ImageView.GONE);

        Glide.with(this)
                .load(flagUrl)
                .placeholder(R.drawable.us)
                .error(R.drawable.us)
                .into(imageFlagGlide);
    }
}