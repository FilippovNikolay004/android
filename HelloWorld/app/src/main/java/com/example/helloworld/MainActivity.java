package com.example.helloworld;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String[] verses = {
        "Дремлет за горой мрачный замок мой,\nДушу мучает порой царящий в нём покой.",
        "Я своих фантазий страждущий герой,\nА любви моей живой все образы со мной...",
        "Я часто вижу страх в смотрящих на меня глазах,\nИм суждено уснуть в моих стенах,\nЗастыть в моих мирах...",
        "Но сердце от любви горит,\nМоя душа болит,\nИ восковых фигур прекрасен вид,\nПокой везде царит!",
        "Я их приводил в свой прекрасный дом,\nИх вином поил — и развлекались мы потом.",
        "Иногда у них лёгкий был испуг\nОт прикосновений к нежной шее крепких рук...",
        "Я часто вижу страх в смотрящих на меня глазах,\nИм суждено уснуть в моих стенах,\nЗастыть в моих мирах...",
        "Но сердце от любви горит,\nМоя душа болит,\nИ восковых фигур прекрасен вид,\nПокой везде царит!",
        "Вот несёт одна мне свои цветы,\nВот стоит другая, погружённая в мечты...",
        "Я пытался их до смерти рассмешить,\nНо пришлось, как в старой сказке, просто задушить!",
        "Я часто вижу страх в смотрящих на меня глазах,\nИм суждено уснуть в моих стенах,\nЗастыть в моих мирах...",
        "Но сердце от любви горит,\nМоя душа болит,\nИ восковых фигур прекрасен вид,\nПокой везде царит!"
    };

    private int currentVerse = 0;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNextVerse = findViewById(R.id.btnNextVerse);

        mediaPlayer = MediaPlayer.create(this, R.raw.kishvospominaniya);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        btnNextVerse.setOnClickListener(v -> {
            Toast.makeText(this, verses[currentVerse], Toast.LENGTH_LONG).show();
            currentVerse = (currentVerse + 1) % verses.length;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
}