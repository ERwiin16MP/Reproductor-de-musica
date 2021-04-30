package com.erwin16mp.reproductordemusica;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.erwin16mp.reproductordemusica.services.NotificationActionService;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVIUOS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static Notification notification;

    public static void createNotification(Context context, Canciones canciones, int playbutton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), canciones.getImagen());

            PendingIntent Anterior = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIUOS), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent PlayPause = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent Siguiente = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, new Intent(context, Index.class), PendingIntent.FLAG_UPDATE_CURRENT);

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.reproduciendo)
                    .setContentTitle(canciones.getTitulo())
                    .setContentText(canciones.getArtista())
                    .setLargeIcon(bitmap)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(R.drawable.anterior, "Anterior", Anterior)
                    .addAction(playbutton, "Play/Pause", PlayPause)
                    .addAction(R.drawable.siguiente, "Siguiente", Siguiente)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManagerCompat.notify(1, notification);
        }
    }
} //66 Lineas -> 53 Lineas