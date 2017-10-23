package android.example.com.squawker.fcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Darren on 10/10/2017.
 * The service will handle incoming FCM where the type is data
 */

public class SquawkFirebaseMessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_MAX_CHARACTERS = 30;
    private static final String JSON_KEY_AUTHOR = SquawkContract.COLUMN_AUTHOR;
    private static final String JSON_KEY_AUTHOR_KEY = SquawkContract.COLUMN_AUTHOR_KEY;
    private static final String JSON_KEY_MESSAGE = SquawkContract.COLUMN_MESSAGE;
    private static final String JSON_KEY_DATE = SquawkContract.COLUMN_DATE;
    /*
    This method is called when a message is received RemoteMessage can be used to obtain a Map
    of the data associated with the message.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().size()> 0){
            Map<String, String> data = remoteMessage.getData();

            sendNotification(data);
            loadMessage(data);
        }



    }

    //method to display information in the notification
    private void sendNotification(Map<String, String> data){

        //Create intent to open app
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_ONE_SHOT);

        String message = data.get(JSON_KEY_MESSAGE);
        String author = data.get(JSON_KEY_AUTHOR);

        //set maximum size to be displayed
        if(message.length()>NOTIFICATION_MAX_CHARACTERS){
            message = message.substring(0,NOTIFICATION_MAX_CHARACTERS )+ "\u2026";
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Squawk from " + author)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, builder.build());
    }

    private void loadMessage(final Map<String, String> data){

        AsyncTask<Void, Void, Void> asyncLoadData = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ContentValues dataValues = new ContentValues();
                dataValues.put(SquawkContract.COLUMN_AUTHOR, data.get(JSON_KEY_AUTHOR));
                dataValues.put(SquawkContract.COLUMN_AUTHOR_KEY, data.get(JSON_KEY_AUTHOR_KEY));
                dataValues.put(SquawkContract.COLUMN_MESSAGE, data.get(JSON_KEY_MESSAGE));
                dataValues.put(SquawkContract.COLUMN_DATE, data.get(JSON_KEY_DATE));
                getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, dataValues);
                return null;
            }
        };
        asyncLoadData.execute();

    }

}
