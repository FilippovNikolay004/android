package com.example.autowallpaperchanger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import java.io.IOException;

public class WallpaperService extends Service {
    private static final String CHANNEL_ID = "wallpaper_channel";
    private static final int NOTIFICATION_ID = 1;
    private Handler handler;
    private Runnable runnable;
    private int currentIndex = 0;

    // Массив картинок
    private final int[] wallpapers = {
            R.drawable.wallpaper_1,
            R.drawable.wallpaper_2,
            R.drawable.wallpaper_3,
            R.drawable.wallpaper_4,
            R.drawable.wallpaper_5
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                changeWallpaper();
                handler.postDelayed(this, 10000); // 10 сек
            }
        };
        handler.post(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Сервис запущен", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Сервис остановлен", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void changeWallpaper() {
        WallpaperManager manager = WallpaperManager.getInstance(this);
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), wallpapers[currentIndex]);
            manager.setBitmap(bitmap);
            currentIndex = (currentIndex + 1) % wallpapers.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Wallpaper Changer", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Автосмена обоев")
                .setContentText("Сервис работает...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
}