package com.mayv.gotrip;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

public class TripReminder {

    private final Calendar calendar;
    private final AlarmManager alarmManager;
    private final PendingIntent pi;
    private static TripReminder tripReminder;
    public static final int ONCE = 11;
    public static final int DAILY = 12;
    public static final int WEEKLY = 13;
    public static final int MONTHLY = 14;

    public TripReminder(Context context, Calendar calendar, AlarmManager alarmManager) {
        this.calendar = calendar;
        this.alarmManager = alarmManager;
        Intent intent = new Intent(context, BroadcastForReminder.class);
        pi = PendingIntent.getBroadcast(context, 1, intent, 0);
    }

    public static TripReminder getTripReminder() {
        return tripReminder;
    }

    public static void setTripReminder(TripReminder tr) {
        tripReminder = tr;
    }

    public void setReminder(int repeating){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
        if (repeating == DAILY){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pi);
        }
        if (repeating == WEEKLY){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 7*24*60*60*1000, pi);
        }
    }

    public void cancelReminder(){
        if(pi != null){
            if (Calendar.getInstance().compareTo(calendar) < 0){
                alarmManager.cancel(pi);
                if(BroadcastForReminder.vibrator != null){
                    BroadcastForReminder.vibrator.cancel();
                }
            } else {
                alarmManager.cancel(pi);
                if(BroadcastForReminder.mp != null || BroadcastForReminder.vibrator != null){
                    BroadcastForReminder.mp.stop();
                    BroadcastForReminder.vibrator.cancel();
                }
            }
        }
    }
}
