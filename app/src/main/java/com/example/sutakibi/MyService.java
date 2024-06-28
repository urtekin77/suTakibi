package com.example.sutakibi;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    private static final String TAG = "MyService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind called");
        return null;  // Bağlanma desteği yok
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand called");
        scheduleSuSifirlamaWorker();
        scheduleWaterReminderWorker();
        startForegroundService();
        return START_STICKY; // Service durdurulursa sistem tarafından tekrar başlatılacak
    }

    private void scheduleSuSifirlamaWorker() {
        Log.d(TAG, "Scheduling Su Sifirlama Worker");
        PeriodicWorkRequest suSifirlamaRequest = new PeriodicWorkRequest.Builder(SuSifirlamaWorker.class, 1, TimeUnit.DAYS)
                .build(); // Gecikme tanımlamak isterseniz setInitialDelay kullanabilirsiniz

        WorkManager.getInstance(this).enqueue(suSifirlamaRequest);
    }

    private void scheduleWaterReminderWorker() {
        Log.d(TAG, "Scheduling Water Reminder Worker");
        PeriodicWorkRequest waterReminderRequest = new PeriodicWorkRequest.Builder(WaterReminderWorker.class, 1, TimeUnit.HOURS)
                .build();

        WorkManager.getInstance(this).enqueue(waterReminderRequest);
    }

    @SuppressLint("ForegroundServiceType")
    private void startForegroundService() {
        Log.d(TAG, "Starting Foreground Service");
        String channelId = createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Service is Running")
                .setContentText("Doing some work.")
                .setSmallIcon(R.drawable.water)
                .build();

        startForeground(1, notification);
    }

    private String createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "my_service_channel";
            String channelName = "My Service Channel";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created");
            return channelId;
        } else {
            // Return an empty ID for notification channels on pre-Oreo devices
            Log.d(TAG, "Notification channel not required for pre-Oreo");
            return "";
        }
    }
}

