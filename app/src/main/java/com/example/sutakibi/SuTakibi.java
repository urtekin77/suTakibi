package com.example.sutakibi;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class SuTakibi extends Application {
    public static final String CHANNEL_ID = "your_channel_id";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        scheduleDailyWaterReset();
        scheduleHourlyWaterReminder();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Water Reminder Channel";
            String description = "Channel for Water Reminder Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleDailyWaterReset() {
        PeriodicWorkRequest dailyWaterResetRequest = new PeriodicWorkRequest.Builder(SuSifirlamaWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(calculateInitialDelayForMidnight(), TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueue(dailyWaterResetRequest);
    }

    private void scheduleHourlyWaterReminder() {
        PeriodicWorkRequest hourlyReminderRequest = new PeriodicWorkRequest.Builder(WaterReminderWorker.class, 1, TimeUnit.HOURS)
                .build();

        WorkManager.getInstance(this).enqueue(hourlyReminderRequest);
    }

    private long calculateInitialDelayForMidnight() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSecond = calendar.get(Calendar.SECOND);

        int hoursUntilMidnight = 23 - currentHour;
        int minutesUntilMidnight = 59 - currentMinute;
        int secondsUntilMidnight = 60 - currentSecond;

        return TimeUnit.HOURS.toMillis(hoursUntilMidnight) +
                TimeUnit.MINUTES.toMillis(minutesUntilMidnight) +
                TimeUnit.SECONDS.toMillis(secondsUntilMidnight);
    }
}
