package com.example.notificationtest.other;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


/**
 * Created by Administrator on 2018/8/3 0003.
 * <p>
 * setContentTitle(CharSequence)：设置标题
 * setContentText(CharSequence)：设置内容
 * setSubText(CharSequence)：设置内容下面一小行的文字
 * setTicker(CharSequence)：设置收到通知时在顶部显示的文字信息
 * setWhen(long)：设置通知时间，一般设置的是收到通知时的System.currentTimeMillis()
 * setSmallIcon(int)：设置右下角的小图标，在接收到通知的时候顶部也会显示这个小图标
 * setLargeIcon(Bitmap)：设置左边的大图标
 * setAutoCancel(boolean)：用户点击Notification点击面板后是否让通知取消(默认不取消)
 * setDefaults(int)：向通知添加声音、闪灯和振动效果的最简单、 使用默认（defaults）属性，可以组合多个属性
 * Notification.DEFAULT_VIBRATE(添加默认震动提醒)
 * Notification.DEFAULT_SOUND(添加默认声音提醒)
 * Notification.DEFAULT_LIGHTS(添加默认三色灯提醒)
 * Notification.DEFAULT_ALL(添加默认以上3种全部提醒)
 * setVibrate(long[])：设置振动方式，比如：setVibrate(new long[] {0,300,500,700});延迟0ms，然后振动300ms，在延迟500ms， 接着再振动700ms
 * setLights(int argb, int onMs, int offMs)：设置三色灯，参数依次是：灯光颜色， 亮持续时间，暗的时间，不是所有颜色都可以，这跟设备有关，有些手机还不带三色灯； 另外，还需要为Notification设置flags为Notification.FLAG_SHOW_LIGHTS才支持三色灯提醒！
 * setSound(Uri)：设置接收到通知时的铃声，可以用系统的，也可以自己设置，例子如下:
 * .setDefaults(Notification.DEFAULT_SOUND) //获取默认铃声
 * .setSound(Uri.parse("file:///sdcard/xx/xx.mp3")) //获取自定义铃声
 * .setSound(Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "5")) //获取Android多媒体库内的铃声
 * setOngoing(boolean)：设置为ture，表示它为一个正在进行的通知。他们通常是用来表示 一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载, 同步操作,主动网络连接)
 * setProgress(int,int,boolean)：设置带进度条的通知 参数依次为：进度条最大数值，当前进度，进度是否不确定 如果为确定的进度条：调用setProgress(max, progress, false)来设置通知， 在更新进度的时候在此发起通知更新progress，并且在下载完成后要移除进度条 ，通过调用setProgress(0, 0, false)既可。如果为不确定（持续活动）的进度条， 这是在处理进度无法准确获知时显示活动正在持续，所以调用setProgress(0, 0, true) ，操作结束时，调用setProgress(0, 0, false)并更新通知以移除指示条
 * setContentInten(PendingIntent)：PendingIntent和Intent略有不同，它可以设置执行次数， 主要用于远程服务通信、闹铃、通知、启动器、短信中，在一般情况下用的比较少。比如这里通过 Pending启动Activity：getActivity(Context, int, Intent, int)，当然还可以启动Service或者Broadcast PendingIntent的位标识符(第四个参数)
 * FLAG_ONE_SHOT 表示返回的PendingIntent仅能执行一次，执行完后自动取消
 * FLAG_NO_CREATE 表示如果描述的PendingIntent不存在，并不创建相应的PendingIntent，而是返回NULL
 * FLAG_CANCEL_CURRENT 表示相应的PendingIntent已经存在，则取消前者，然后创建新的PendingIntent， 这个有利于数据保持为最新的，可以用于即时通信的通信场景
 * FLAG_UPDATE_CURRENT 表示更新的PendingIntent
 */

public class NotificationUtils {


    private NotificationManager notificationManager;

    private Notification current;

    private String channelGroupId;
//    private Notification.Builder builder;

    private NotificationCompat.Builder builder;

    private ChannelBuilder channelBuilder;
    private int currentNotiId;

    private Activity context;

    private String currentChannel;

    private NotificationChannelGroup notificationChannelGroup;

    private NotificationChannel notificationChannel;

    public NotificationUtils(Activity context) {
        this(context, null);
    }

    public NotificationUtils(Activity context, ChannelBuilder channelBuilder) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.channelBuilder = channelBuilder;
    }

    private static final String TAG = "NotificationUtils";

    /**
     * 创建一个channel
     *
     * @param channelGroupId
     * @param channelId
     * @param channelName
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(String channelGroupId, String channelId, String channelName) {
        Log.i(TAG, "createChannel: ");
        if (channelBuilder == null) {
            channelBuilder = new ChannelBuilder(channelGroupId, channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                    .setByPassDnd(false)
                    .setChannelGroupId(channelGroupId)
                    .setShowBadge(false)
                    .setSoundUri(null)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setLightColor(Color.GREEN)
                    .setVibrate(null);
        }
        channelBuilder.setChannelGroupId(channelGroupId).setChannelId(channelId);


        notificationChannel = channelBuilder.build();

        notificationManager.createNotificationChannel(notificationChannel);
    }


    /**
     * @param channelBuilder
     */
    public void setChannelBuilder(ChannelBuilder channelBuilder) {
        this.channelBuilder = channelBuilder;
        init(channelBuilder.channelId, channelBuilder.channelName, channelBuilder.channelGroupId, channelBuilder.channelGroupName);

    }

    public void init(String channelId, String channelName, String channelGroupId, String channelGroupName) {
        this.currentChannel = channelId;
        this.channelGroupId = channelGroupId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroup(channelGroupId, channelGroupName);

            createChannel(channelGroupId, channelId, channelName);
        }


    }


    public static class ChannelBuilder {

//       对NotificationChannel的设置只能对8.0及以上的系统才生效 （铃声 震动 指示灯这些） 8.0以下的在NotificationCompat.Builder中的设置会生效
//     * @param channelId            频道id
//     * @param channelDesc          具体描述
//     * @param channelName
//     * @param byPassDnd            是否绕过免打扰
//     * @param lockScreenVisibility 是否在锁屏界面显示 example: Notification.VISIBILITY_PRIVATE
//     * @param showBadge            是否在桌面图标上显示未读消息数量
//     * @param importance  NotificationManager.IMPORTANCE_MAX

//        NotificationManager.IMPORTANCE_UNSPECIFIED 状态栏不会显示应用的通知图标(smallIcon) 只有下拉才能看到缩略的通知 (此通知在下拉后还需用户手动展开才能看到完整的通知) 三星s8
//        NotificationManager.IMPORTANCE_MIN 同上

//        NotificationManager.IMPORTANCE_LOW 无声 状态栏有应用的通知图标(smallIcon)

//        NotificationManager.IMPORTANCE_DEFAULT 有声音震动 状态栏显示应用的通知图标(smallIcon)

//        NotificationManager.IMPORTANCE_HIGH 有声音震动 状态栏显示应用的通知图标(smallIcon) 并且会有heads-up的浮动通知效果

        private String channelGroupId;
        private String channelGroupName;
        private String channelId;
        private String channelDesc;
        private String channelName;
        private boolean byPassDnd = false;
        private int visibility = Notification.VISIBILITY_PUBLIC;
        private boolean showBadge = false;
        private int importance = NotificationManager.IMPORTANCE_DEFAULT;

        private boolean enableSound = true;
        private boolean enableVibrate = true;
        private boolean enableLight = true;

        private Uri soundUri;

        private long[] vibrateLong;

        private int lightColor = -1;

        public ChannelBuilder(String channelGroupId, String channelId, String channelName, int importance) {
            this.channelGroupId = channelGroupId;
            this.channelId = channelId;
            this.importance = importance;
            this.channelName = channelName;
        }

        public ChannelBuilder setChannelGroupId(String channelGroupId) {
            this.channelGroupId = channelGroupId;
            return this;
        }

        public ChannelBuilder setChannelGroupName(String channelGroupName) {
            this.channelGroupName = channelGroupName;
            return this;
        }


        public ChannelBuilder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        /**
         * @param channelDesc 用户可以在设置-》应用程序通知中看见此描述（8.0以上可见）
         * @return
         */
        public ChannelBuilder setChannelDesc(String channelDesc) {
            this.channelDesc = channelDesc;
            return this;
        }

        /**
         * @param channelName 用户可以在设置-》应用程序通知中看见此描述（8.0以上可见）
         * @return
         */
        public ChannelBuilder setChannelName(String channelName) {
            this.channelName = channelName;
            return this;
        }

        public ChannelBuilder setByPassDnd(boolean byPassDnd) {
            this.byPassDnd = byPassDnd;
            return this;
        }

        public ChannelBuilder setVisibility(int visibility) {
            this.visibility = visibility;
            return this;
        }

        public ChannelBuilder setShowBadge(boolean showBadge) {
            this.showBadge = showBadge;
            return this;
        }

        public ChannelBuilder setEnableSound(boolean enableSound) {
            this.enableSound = enableSound;
            return this;
        }

        public ChannelBuilder setEnableVibrate(boolean enableVibrate) {
            this.enableVibrate = enableVibrate;
            return this;
        }

        public ChannelBuilder setEnableLight(boolean enableLight) {
            this.enableLight = enableLight;
            return this;
        }

        public ChannelBuilder setImportance(int importance) {
            this.importance = importance;
            return this;
        }

        public ChannelBuilder setSoundUri(Uri soundUri) {
            this.soundUri = soundUri;
            return this;
        }

        public ChannelBuilder setVibrate(long[] vibrate) {
            this.vibrateLong = vibrate;
            return this;
        }


        public ChannelBuilder setLightColor(int lightColor) {
            this.lightColor = lightColor;
            return this;
        }

        private static final String TAG = "ChannelBuilder";
        /**
         * 创建一个NotificationChannel
         *
         * @return
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public NotificationChannel build() {
            Log.i(TAG, "build: ");
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);

            notificationChannel.setShowBadge(showBadge);
            notificationChannel.setBypassDnd(byPassDnd);
            notificationChannel.setLockscreenVisibility(visibility);

            if (!TextUtils.isEmpty(channelDesc)) {
                notificationChannel.setDescription(channelDesc);
            }

            if (!TextUtils.isEmpty(channelGroupId)) {
                notificationChannel.setGroup(channelGroupId);
            }

            if (!TextUtils.isEmpty(channelDesc)) {
                notificationChannel.setDescription(channelDesc);
            }

            if (true) {
                if (soundUri != null) {
                    AudioAttributes.Builder builder = new AudioAttributes.Builder();
                    notificationChannel.setSound(soundUri, builder.build());
                }
            } else {
//                用此方法达到静音的效果 = = （因为在8.0的heads-up效果中importance必须会有声音 如果想静音且想有heads-up效果的话就必须传空值）
                notificationChannel.setSound(Uri.EMPTY, null);
            }
            notificationChannel.enableLights(enableLight);
            if (true) {
                if (lightColor != -1) {
                    notificationChannel.setLightColor(lightColor);
                }
            }

            if (true) {
                notificationChannel.enableVibration(true);
                if (vibrateLong != null) {
                    notificationChannel.setVibrationPattern(vibrateLong);
                }
            } else {
                notificationChannel.enableVibration(false);
            }

            return notificationChannel;
        }
    }


    /**
     * 创建一个频道组
     *
     * @param channelGroupId
     * @param channelGroupName 用户可以在设置-》应用程序通知中看见此描述（8.0以上可见）
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannelGroup(String channelGroupId, String channelGroupName) {

        NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(channelGroupId, channelGroupName);
        this.channelGroupId = channelGroupId;
        this.notificationChannelGroup = notificationChannelGroup;
        notificationManager.createNotificationChannelGroup(notificationChannelGroup);

    }

    /**
     * 浮动通知 在[5.0,8.0)的系统中浮动通知的产生条件
     * 是NotificationCompat.Builder中设置setPriority()的参数
     * 必须在NotificationCompat.PRIORITY_HIGH及以上并且有铃声或者震动才能有效果
     * 但在[8.0，8.0+)的时候因为NotificationChannel中的设置高于一切 所以
     * NotificationChannel中的importance必须要在NotificationManager.IMPORTANCE_HIGH及以上(!注意 此时会有默认的铃声和震动的效果哦～)
     * 5.0以下的系统就不支持啦
     *
     * @param noticationId
     * @param pendingIntent
     * @param largeIcon
     * @param smallIcon
     * @param ticker
     * @param subText
     * @param contentTitle
     * @param contentText
     * @param sound
     * @param vibrate
     * @param light
     */
    public void notifyHeadUp(int noticationId, PendingIntent pendingIntent, @DrawableRes int largeIcon, @DrawableRes int smallIcon, String ticker, String subText, String contentTitle, String contentText, boolean sound, boolean vibrate, boolean light) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationChannel.getImportance() < NotificationManager.IMPORTANCE_HIGH) {
                notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//
//        }
        builderNotification(pendingIntent, largeIcon, smallIcon, ticker, subText, contentTitle, contentText, NotificationCompat.PRIORITY_HIGH, sound, vibrate, light);


        notifyNotification(noticationId);
    }


    /**
     * 发送一个普通的通知
     *
     * @param noticationId
     * @param pendingIntent
     * @param largeIcon
     * @param smallIcon
     * @param ticker
     * @param subText
     * @param contentTitle
     * @param contentText
     * @param priority
     * @param sound
     * @param vibrate
     * @param light
     */
    public void notifyMessage(int noticationId, PendingIntent pendingIntent, @DrawableRes int largeIcon, @DrawableRes int smallIcon, String ticker, String subText, String contentTitle, String contentText, int priority, boolean sound, boolean vibrate, boolean light) {

        builderNotification(pendingIntent, largeIcon, smallIcon, ticker, subText, contentTitle, contentText, priority, sound, vibrate, light);


        notifyNotification(noticationId);

    }


    /**
     * 大图的通知类型
     *
     * @param noticationId
     * @param pendingIntent
     * @param largeIcon
     * @param bigPicFilePath 大图的绝对路径地址
     * @param smallIcon
     * @param ticker
     * @param subText
     * @param contentTitle
     * @param contentText
     * @param priority
     * @param sound
     * @param vibrate
     * @param light
     */
    public void notifyBigPic(int noticationId, PendingIntent pendingIntent, @DrawableRes int largeIcon, String bigPicFilePath, @DrawableRes int smallIcon, String ticker, String subText, String contentTitle, String contentText, int priority, boolean sound, boolean vibrate, boolean light) {
        builderNotification(pendingIntent, largeIcon, smallIcon, ticker, subText, contentTitle, contentText, priority, sound, vibrate, light);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inScaled = true;
        Bitmap bitmap = BitmapFactory.decodeFile(bigPicFilePath, options);

        bigPictureStyle.bigLargeIcon(bitmap);
        bigPictureStyle.bigPicture(bitmap);


//        bigPictureStyle.setBigContentTitle("这是大图");
//        bigPictureStyle.setSummaryText("这是大图的Summary");

        builder.setStyle(bigPictureStyle);


        notifyNotification(noticationId);


    }


    /**
     * 发送长文字类型的通知 5.0以下长文字会全部显示出来 5.0以上会折叠
     *
     * @param noticationId
     * @param pendingIntent
     * @param largeIcon
     * @param smallIcon
     * @param ticker
     * @param subText
     * @param contentTitle
     * @param contentText
     * @param priority
     * @param sound
     * @param vibrate
     * @param light
     */
    public void notifyBigText(int noticationId, PendingIntent pendingIntent, @DrawableRes int largeIcon, @DrawableRes int smallIcon, String ticker, String subText, String contentTitle, String contentText, int priority, boolean sound, boolean vibrate, boolean light) {
        builderNotification(pendingIntent, largeIcon, smallIcon, ticker, subText, contentTitle, contentText, priority, sound, vibrate, light);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(contentText);
        builder.setStyle(bigTextStyle);
//        }


        notifyNotification(noticationId);


    }


    /**
     * 在5.0的系统以下没有进度条显示 8.0及以上显示进度条是没有铃声和震动的效果的
     *
     * @param noticationId
     * @param pendingIntent
     * @param largeIcon
     * @param smallIcon
     * @param ticker
     * @param subText
     * @param contentTitle
     * @param contentText
     * @param maxProgress
     * @param curProgress
     */
    public void notifyProgress(int noticationId, PendingIntent pendingIntent, @DrawableRes int largeIcon, @DrawableRes int smallIcon, String ticker, String subText, String contentTitle, String contentText, final int maxProgress, final int curProgress) {
        builderNotification(pendingIntent, largeIcon, smallIcon, ticker, subText, contentTitle, contentText, NotificationCompat.PRIORITY_HIGH, false, true, false);

        if (curProgress >= maxProgress) {
            builder.setProgress(0, 0, false);
        } else {
            builder.setProgress(maxProgress, curProgress, false);
        }


        notifyNotification(noticationId);
    }


    /**
     * 添加两个左右的按钮  发送通知
     * <p>
     * 按钮最多可以加到三个
     *
     * @param noticationId
     * @param pendingIntent
     * @param leftPendingIntent
     * @param leftIcon
     * @param leftBtnText
     * @param rightPendingIntent
     * @param rightIcon
     * @param rightBtnText
     * @param largeIcon
     * @param smallIcon
     * @param ticker
     * @param subText
     * @param contentTitle
     * @param contentText
     * @param priority
     * @param sound
     * @param vibrate
     * @param light
     */
    public void notifyButton(int noticationId, PendingIntent pendingIntent, PendingIntent leftPendingIntent, int leftIcon, CharSequence leftBtnText, PendingIntent rightPendingIntent, int rightIcon, CharSequence rightBtnText, @DrawableRes int largeIcon, @DrawableRes int smallIcon, String ticker, String subText, String contentTitle, String contentText, int priority, boolean sound, boolean vibrate, boolean light) {

        builderNotification(pendingIntent, largeIcon, smallIcon, ticker, subText, contentTitle, contentText, priority, sound, vibrate, light);
        builder.addAction(leftIcon, leftBtnText, leftPendingIntent);
        builder.addAction(rightIcon, rightBtnText, rightPendingIntent);

        notifyNotification(noticationId);
    }


    /**
     * 发送一个消息类的通知7.0以上有效 7.0以下效果不友好
     *
     * @param noticationId
     * @param pendingIntent
     * @param largeIcon
     * @param smallIcon
     * @param ticker
     * @param subText
     * @param contentTitle
     * @param contentText
     * @param priority
     * @param sound
     * @param vibrate
     * @param light
     */
    public void notifyMessageType(int noticationId, PendingIntent pendingIntent, @DrawableRes int largeIcon, @DrawableRes int smallIcon, String ticker, String subText, String contentTitle, String contentText, int priority, boolean sound, boolean vibrate, boolean light) {

        builderNotification(pendingIntent, largeIcon, smallIcon, ticker, subText, contentTitle, contentText, priority, sound, vibrate, light);

        builder.setStyle(new NotificationCompat.MessagingStyle(contentTitle).setConversationTitle("xx")
                .addMessage(new NotificationCompat.MessagingStyle.Message(contentText, System.currentTimeMillis(), "wo")));

        notifyNotification(noticationId);

    }


    /**
     * 使用自定义的view发送通知
     *
     * @param remoteViews
     * @param noticationId
     * @param pendingIntent
     * @param largeIcon
     * @param smallIcon
     * @param ticker
     * @param subText
     * @param contentTitle
     * @param contentText
     * @param priority
     * @param sound
     * @param vibrate
     * @param light
     */
    public void notifyRemoteViews(RemoteViews remoteViews, int noticationId, PendingIntent pendingIntent, @DrawableRes int largeIcon, @DrawableRes int smallIcon, String ticker, String subText, String contentTitle, String contentText, int priority, boolean sound, boolean vibrate, boolean light) {
        builderNotification(pendingIntent, largeIcon, smallIcon, ticker, subText, contentTitle, contentText, priority, sound, vibrate, light);

        builder.setContent(remoteViews);

        notifyNotification(noticationId);
    }

    //不要尝试去缩减参数  很烦的
    public void notifyRemoteViews(RemoteViews remoteViews, int notificationId, int priority) {
        builderNotification(priority);

        builder.setContent(remoteViews);

        notifyNotification(notificationId);
    }


    public void cancelNoti(int noticationId) {
        notificationManager.cancel(noticationId);
    }


    public void cancelCurrentNotification() {
        notificationManager.cancel(currentNotiId);
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }

    private void builderNotification(int priority) {
        builder = new NotificationCompat.Builder(context, currentChannel);
        builder.setGroup(channelGroupId);
        builder.setChannelId(currentChannel);
        builder.setAutoCancel(true);


        builder.setPriority(priority);

    }

    /**
     * 创建一个Notification
     *
     * @param pendingIntent 通知内容被点击处理
     * @param largeIcon
     * @param smallIcon
     * @param ticker        ticker在5.0以上没有显示 在[4.0,5.0)在状态栏有短暂的停留
     * @param subText
     * @param contentTitle
     * @param contentText
     */
    private void builderNotification(PendingIntent pendingIntent, @DrawableRes int largeIcon, @DrawableRes int smallIcon, String ticker, String subText, String contentTitle, String contentText, int priority, boolean sound, boolean vibrate, boolean light) {

        builder = new NotificationCompat.Builder(context, currentChannel);
//    android 5.0以下 ticker 会在状态栏短暂显示一会上移消失 5.0以上 没有效果
        builder.setTicker(ticker);
        builder.setSubText(subText);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setSmallIcon(smallIcon);
        builder.setGroup(channelGroupId);
        builder.setChannelId(currentChannel);

//        builder.set
        if (largeIcon != -1) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon));
        }


        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(true);

        builder.setWhen(System.currentTimeMillis());

        builder.setPriority(priority);

//        notificationChannel.setLightColor();
//        notificationChannel.
//        在8.0以上的系统中 铃声、震动、指示灯 都会使用NotificationChannel中的设置 以下的设置会失效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationChannel.setLightColor();

            notificationChannel.setSound(sound ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) : null, sound ? Notification.AUDIO_ATTRIBUTES_DEFAULT : null);

            notificationChannel.enableVibration(vibrate);
            long[] pattern = new long[]{1000, 1000, 1000, 1000, 1000};
            notificationChannel.setVibrationPattern(pattern);
        } else {
            int def = 0;
            if (sound) {
                def |= NotificationCompat.DEFAULT_SOUND;
            }

            if (vibrate) {
                def |= NotificationCompat.DEFAULT_VIBRATE;
            }

            if (light) {
                def |= NotificationCompat.DEFAULT_LIGHTS;
            }

            builder.setDefaults(def);

        }

    }

    private void notifyNotification(int noticationId) {
        currentNotiId = noticationId;
        current = builder.build();
        notificationManager.notify(currentNotiId, current);
    }
}
