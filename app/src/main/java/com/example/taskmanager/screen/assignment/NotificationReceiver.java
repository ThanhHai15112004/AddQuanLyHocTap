package com.example.taskmanager.screen.assignment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.taskmanager.model.Assignment;
import com.example.taskmanager.screen.DatabaseHelper;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long notificationId = intent.getLongExtra("notificationId", -1);
        long assignmentId = intent.getLongExtra("assignmentId", -1);

        // Gửi thông báo
        if (notificationId != -1 && assignmentId != -1) {
            // Tạo và hiển thị thông báo
            showNotification(context, assignmentId);
        }
    }

    private void showNotification(Context context, long assignmentId) {
        // Get the assignment from the database
        Assignment assignment = new DatabaseHelper(context).getAssignmentById(assignmentId);

        if (assignment != null) {
            // Create the notification content
            String title = assignment.getTitle();
            String description = assignment.getDescription();

            // Create the notification channel (required for Android 8.0 and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Assignment Notifications";
                String descriptionText = "Notifications for assignment reminders";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("assignment_channel", name, importance);
                channel.setDescription(descriptionText);

                // Register the channel with the system
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // Build the notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "assignment_channel")
                    .setSmallIcon(android.R.drawable.ic_notification_overlay)  // Set a default system icon
                    .setContentTitle(title)
                    .setContentText(description)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))  // Set sound for the notification
                    .setAutoCancel(true);  // Automatically cancel the notification when clicked

            // Get the NotificationManager system service and issue the notification
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify((int) assignmentId, notificationBuilder.build());  // Use assignmentId as notification ID

            new DatabaseHelper(context).updateNotificationStatussent(assignmentId);
        }
    }
}
