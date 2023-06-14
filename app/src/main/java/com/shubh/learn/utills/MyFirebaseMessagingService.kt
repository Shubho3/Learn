package com.shubh.learn.utills

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.shubh.learn.R
import com.shubh.learn.dto.NotificationData
import com.shubh.learn.utills.GlobalUtility.Companion.getTwoDigitRandomNo
import com.vilborgtower.user.utils.Constant.NOTIFICATION_CHANNEL_ID

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var mNotificationData: NotificationData? = null

    //    private var mAlert: String? = null
//    private var mSound: String? = null
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            Log.e(TAG, "onMessageReceived messagess--"+remoteMessage)

        if (remoteMessage != null && remoteMessage!!.getData() != null) {
            Log.e(TAG, "onMessageReceived messagess--"+remoteMessage!!.getData() )
            val payload = remoteMessage!!.getData().get("message")
            Log.e(TAG, "onMessageReceived messagess--"+payload )
            val gson = Gson()
            mNotificationData = gson.fromJson(payload, NotificationData::class.java)
          //  mAlert = remoteMessage!!.getData().get("alert")
          //  mSound = remoteMessage!!.getData().get("sound")
            Log.e(TAG, "onMessageReceived: mNotificationData"+mNotificationData.toString() )
            Log.e(TAG, "onMessageReceived: mNotificationData"+mNotificationData?.key )
            Log.e(TAG, "onMessageReceived: mNotificationData"+mNotificationData?.title )
            showNotification(this, mNotificationData)
        } else {

        }
        }catch (e:Exception){
            Log.e(TAG, "onMessageReceived messagess--"+remoteMessage.notification)
            Log.e(TAG, "onMessageReceived messagess-- "+remoteMessage.notification?.title)
            Log.e(TAG, "onMessageReceived messagess--"+remoteMessage.notification?.body)

            showNotificationNormal(this,remoteMessage)
            e.printStackTrace()
        }

    }

    private fun showNotification(context: Context, mNotificationData: NotificationData?) {
        val intent = GlobalUtility.getIntentForPush(context, mNotificationData)

        if (intent != null) {
            val pendingIntent = PendingIntent.getActivity(context, getTwoDigitRandomNo(), intent, getflag())
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context)
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
            notificationBuilder.setLargeIcon(
                BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            notificationBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            notificationBuilder.setContentTitle(mNotificationData?. key)
//            if (mAlert != null) {
            notificationBuilder.setContentText(mNotificationData?.title)
            notificationBuilder.setStyle(
                NotificationCompat.BigTextStyle().bigText(mNotificationData?.title)
            )
//            }
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setSound(defaultSoundUri)
            notificationBuilder.setVibrate(longArrayOf(1000, 1000))
            notificationBuilder.setContentIntent(pendingIntent)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_NAME",
                    importance
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(1000, 1000)
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationManager.notify(getTwoDigitRandomNo()/*Id of Notification*/, notificationBuilder.build())
        }
    }
    private fun showNotificationNormal(context: Context, remoteMessage: RemoteMessage) {
        val intent = GlobalUtility.getIntentForPush2(context, remoteMessage)

        if (intent != null) {
            val pendingIntent = PendingIntent.getActivity(context, getTwoDigitRandomNo(), intent, getflag())
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context)
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
            notificationBuilder.setLargeIcon(
                BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            notificationBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            notificationBuilder.setContentTitle(remoteMessage.notification?.title)
//            if (mAlert != null) {remoteMessage.notification?.title
//remoteMessage.notification?.body)
            notificationBuilder.setContentText(remoteMessage.notification?.body)
            notificationBuilder.setStyle(
                NotificationCompat.BigTextStyle().bigText(remoteMessage.notification?.body)
            )
//            }
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setSound(defaultSoundUri)
            notificationBuilder.setVibrate(longArrayOf(1000, 1000))
            notificationBuilder.setContentIntent(pendingIntent)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_NAME",
                    importance
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(1000, 1000)
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationManager.notify(getTwoDigitRandomNo()/*Id of Notification*/, notificationBuilder.build())
        }
    }

    private fun getflag(): Int {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.FLAG_IMMUTABLE

        } else {
            return PendingIntent.FLAG_ONE_SHOT

        }
    }


}