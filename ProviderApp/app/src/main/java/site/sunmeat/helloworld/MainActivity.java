package site.sunmeat.helloworld;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Murzik";
    private static final int START_LIVES = 9;
    private static final int TOTAL_TIME_SECONDS = 30;
    private static final int FEED_TIME_SECONDS = 25;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            if (isDead || isFed) {
                return;
            }

            secondsLeft -= 1;
            if (secondsLeft % 5 == 0) {
                livesLeft = Math.max(0, livesLeft - 1);
                Log.i(TAG, getString(R.string.meow_log));
            }

            if (secondsLeft <= 0) {
                secondsLeft = 0;
                updateStatusText();
                handleDeath();
                return;
            }

            updateStatusText();
            mainHandler.postDelayed(this, 1000);
        }
    };

    private TextView statusText;
    private TextView messageText;
    private Button feedButton;
    private View mainRoot;

    private volatile int livesLeft = START_LIVES;
    private volatile int secondsLeft = TOTAL_TIME_SECONDS;
    private volatile boolean isFeeding = false;
    private volatile boolean isDead = false;
    private volatile boolean isFed = false;
    private Thread feedThread;
    private int defaultStatusColor;
    private int defaultMessageColor;
    private Drawable defaultBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        messageText = findViewById(R.id.messageText);
        feedButton = findViewById(R.id.feedButton);
        mainRoot = findViewById(R.id.main_root);

        defaultStatusColor = statusText.getCurrentTextColor();
        defaultMessageColor = messageText.getCurrentTextColor();
        defaultBackground = mainRoot.getBackground();

        feedButton.setOnClickListener(view -> startFeeding());

        resetState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeCallbacks(countdownRunnable);
        if (feedThread != null) {
            feedThread.interrupt();
        }
    }

    private void startFeeding() {
        if (isFeeding) {
            return;
        }

        if (isDead || isFed) {
            resetState();
        }

        isFeeding = true;
        feedButton.setEnabled(false);
        messageText.setText(getString(R.string.message_feeding_start));

        feedThread = new Thread(() -> {
            String[] messages = getResources().getStringArray(R.array.feed_messages);
            for (int i = 0; i < FEED_TIME_SECONDS; i++) {
                if (isDead) {
                    return;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interrupted) {
                    return;
                }

                String template = messages[i % messages.length];
                String message = template.contains("%1$d")
                        ? String.format(Locale.getDefault(), template, livesLeft)
                        : template;

                mainHandler.post(() -> {
                    if (isDead || isFed) {
                        return;
                    }
                    messageText.setText(message);
                });
            }

            mainHandler.post(this::finishFeeding);
        });
        feedThread.start();
    }

    private void finishFeeding() {
        if (isDead) {
            return;
        }
        isFed = true;
        isFeeding = false;
        feedThread = null;
        mainHandler.removeCallbacks(countdownRunnable);
        messageText.setText(getString(R.string.feed_success));
        feedButton.setText(getString(R.string.feed_again_button));
        feedButton.setEnabled(true);
        updateStatusText();
    }

    private void handleDeath() {
        if (isDead || isFed) {
            return;
        }

        isDead = true;
        isFeeding = false;
        if (feedThread != null) {
            feedThread.interrupt();
        }
        feedThread = null;

        mainHandler.removeCallbacks(countdownRunnable);
        feedButton.setText(getString(R.string.feed_again_button));
        feedButton.setEnabled(true);
        messageText.setText(getString(R.string.death_message));
        messageText.setTextColor(Color.WHITE);
        statusText.setTextColor(Color.WHITE);
        mainRoot.setBackgroundColor(Color.BLACK);
    }

    private void updateStatusText() {
        statusText.setText(getString(R.string.status_format, livesLeft, secondsLeft));
    }

    private void resetState() {
        mainHandler.removeCallbacks(countdownRunnable);
        if (feedThread != null) {
            feedThread.interrupt();
            feedThread = null;
        }

        isDead = false;
        isFed = false;
        isFeeding = false;
        livesLeft = START_LIVES;
        secondsLeft = TOTAL_TIME_SECONDS;

        statusText.setTextColor(defaultStatusColor);
        messageText.setTextColor(defaultMessageColor);
        if (defaultBackground != null) {
            mainRoot.setBackground(defaultBackground);
        } else {
            mainRoot.setBackgroundColor(Color.TRANSPARENT);
        }

        feedButton.setEnabled(true);
        feedButton.setText(getString(R.string.feed_button));
        updateStatusText();
        messageText.setText(getString(R.string.message_idle));
        mainHandler.postDelayed(countdownRunnable, 1000);
    }
}
