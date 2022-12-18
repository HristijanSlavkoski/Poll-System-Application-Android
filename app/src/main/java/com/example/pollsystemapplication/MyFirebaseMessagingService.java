package com.example.pollsystemapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            // Handle data payload of FCM message.
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            // Handle notification message.
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();

            createNotificationChannel();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MyNots")
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(0, builder.build());

        } else {
            createNotificationChannel();
            String message = remoteMessage.getData().get("message");

            // Create a notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MyNots")
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle("Notification")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            //Show the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, builder.build());
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "messages";
            // String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("MyNots", name, importance);
            //channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}