package com.mayv.gotrip;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class BroadcastForReminder extends BroadcastReceiver {
    static MediaPlayer mp;
    static final String chID = "Reminder";
    static Vibrator vibrator;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        mp = MediaPlayer.create(context, R.raw.alarm_beep);
        mp.start();
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 1000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(pattern, 0);
        } else {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        }
        NotificationManager notMan = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "REMINDER NOTIFICATION";
            String description = "It's Trip Time";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(chID, name, importance);
            channel.setDescription(description);
            notMan.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, chID)
                .setSmallIcon(R.drawable.ic_baseline_alarm)
                .setContentTitle("Let's Go")
                .setContentText("It's Trip Time")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("CameFromBroadcast", true);
        editor.apply();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);
        notMan.notify(1, builder.build());
    }

}
