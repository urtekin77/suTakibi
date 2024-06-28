package com.example.sutakibi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sutakibi.R;

public class WaterReminderWorker extends Worker {

    private static final String CHANNEL_ID = "your_channel_id";
    private static final String TAG = "WaterReminderWorker";

    public WaterReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Worker started at: " + System.currentTimeMillis());
        sendNotification();
        Log.d(TAG, "Worker completed at: " + System.currentTimeMillis());
        return Result.success();
    }

    private void sendNotification() {
        Context context = getApplicationContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.water)
                .setContentTitle("Su Hatırlatıcı")
                .setContentText("Bir bardak su içmeyi unutmayın!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Log.d(TAG, "Sending notification");
            notificationManager.notify(1, builder.build());
        } else {
            Log.e(TAG, "NotificationManager is null");
        }
    }
}
