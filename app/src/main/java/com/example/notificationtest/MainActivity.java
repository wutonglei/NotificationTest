package com.example.notificationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * 疑问 channelId起到的作用是什么
 * <p>
 * NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
 * channel.setBypassDnd(true); //设置绕过免打扰模式
 * channel.canBypassDnd(); //检测是否绕过免打扰模式
 * channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);//设置在锁屏界面上显示这条通知
 * channel.setDescription("description of this notification");
 * channel.setLightColor(Color.GREEN);
 * channel.setName("name of this notification");
 * channel.setShowBadge(true);
 * channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
 * channel.enableVibration(true);
 * getNotificationManager().createNotificationChannel(channel);
 * <p>
 * 作者：方_f666
 * 链接：https://www.jianshu.com/p/d2ef361cedfe
 * 来源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 *
 *
 * 1.为什么 需要点击2次以后才能显示通知  第一次无法显示
 */
public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "chat";

    /**
     * 状态栏和抽屉式通知栏
     * 提醒式通知  短暂地显示在浮动窗口
     * 锁定屏幕
     * 应用图标的标志
     */
    NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();


        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "subscribe";
            String channelName = "订阅消息";

            createNotificationChannel(channelId, channelName);


            channelId = "chat";
            channelName = "聊天消息";
            createNotificationChannel(channelId, channelName);


        }

        mShow7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show7();
            }
        });

        mShow8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show78();
            }
        });

        mShow_jump1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJump1();
            }
        });
        mShow_jump2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJump2();
            }
        });


        mShow_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustom();
            }
        });
        mShow_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGroup();
            }
        });
        mShow_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLock();
            }
        });
    }

    /**
     * 必须先通过向 createNotificationChannel() 传递 NotificationChannel 的实例在系统中注册应用的通知渠道
     * ，然后才能在 Android 8.0 及更高版本上提供通知。因此以下代码会被 SDK_INT 版本上的条件阻止：
     * 但您还必须使用 setPriority() 设置优先级，才能支持 Android 7.1 和更低版本（如上所示）。
     * 疑问 是注册了全局的还是 什么意思
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String name) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH ;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription("随便描述下");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 8.0 失败
     */
    public void show7() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(MainActivity.this)
                .setContentTitle("这是测试通知标题")  //设置标题
                .setContentText("这是测试通知内容") //设置内容

                .setWhen(System.currentTimeMillis())  //设置时间
                .setSmallIcon(R.mipmap.ic_launcher)  //设置小图标  只能使用alpha图层的图片进行设置
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))   //设置大图标
                .setContentIntent(pi)
                //                       .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }

    /**
     * 尝试78版本的都试下能不能正常显示   都可以显示
     * 请注意，NotificationCompat.Builder 构造函数要求您提供渠道 ID。这是兼容 Android 8.0（API 级别 26）及更高版本所必需的，但会被较旧版本忽略。
     * 意思是都可以通用咯？
     * manager.notify(i++, notification); id不同的可以不覆盖显示
     */

    int i = 1;



    /**
     * 不知道是不是
     * 默认情况下，通知的文本内容会被截断以放在一行。如果您想要更长的通知，可以使用 setStyle() 添加样式模板来启用可展开的通知kminisite.exe
     */
    public void showBigPicture() {

//显示大图片
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_girl)
                .setContentTitle("显示大图")
                .setContentText("超级的大的图")
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap))
                .build();
        manager.notify(1, notification);

    }


    public void sendChatMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, "chat")
                .setContentTitle("收到一条聊天消息")
                .setContentText("今天中午吃什么？")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_mute)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_mute))
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }


    /**
     * 1.定义应用的 Activity 层次结构  android:parentActivityName=".MainActivity" />
     */
    public void showJump1() {
        /**
         * 常规 Activity
         * 这类 Activity 是应用的正常用户体验流程的一部分。因此，当用户从通知转到这类 Activity 时，
         * 新任务应包括完整的返回堆栈，以便用户可以按“返回”按钮并沿应用层次结构向上导航。
         */
        Intent resultIntent = new Intent(this, ShowActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_girl)
                .setContentTitle("显示大图")
                .setContentText("超级的大的图")
                .setContentIntent(resultPendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);
    }

//    很迷啊  自己的手机失败了
    public void showJump2() {
        /**
         * 特殊 Activity
         * 只有当 Activity 从通知启动时，用户才可以看到此类 Activity。从某种意义上来说，这类 Activity
         * 通过提供通知本身难以显示的信息来扩展通知界面。因此，这类 Activity 不需要返回堆栈。
         */
        Intent notifyIntent = new Intent(this, SpecialActivity.class);
        // Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_girl)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(notifyPendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(5, notification);
    }


    public void showApp() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                .setContentTitle("New Messages")
                .setContentText("You've received 3 new messages.")
                .setSmallIcon(R.drawable.ic_mute)
                .setNumber(5)
                .build();

//        Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
//                .setContentTitle("New Messages")
//                .setContentText("You've received 3 new messages.")
//                .setSmallIcon(R.drawable.ic_mute)
//                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
//                .build();
        manager.notify(1, notification);
    }

    /**
     * 从 Android 7.0（API 级别 24）开始，您可以在一个通知组中显示相关通知（以前称为“捆绑式”通知）。
     * 例如，如果您的应用针对收到的电子邮件显示通知，您应将所有通知放入同一个通知组，以便它们可以收起。
     * <p>
     * 为了支持较低版本，您还可以添加摘要通知，该摘要通知会单独显示以总结所有单独的通知
     * - 通常最好使用收件箱样式的通知实现此目的。
     */
    public void showGroup() {
        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";
        //use constant ID for notification used as group summary
        int SUMMARY_ID = 0;

        Notification newMessageNotification1 =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_girl)
                        .setContentTitle("通讯1")
                        .setContentText("1111111111111111111111")
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .build();

        Notification newMessageNotification2 =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_error)
                        .setContentTitle("通信2")
                        .setContentText("2222222222222")
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .build();
        Notification newMessageNotification3 =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_error)
                        .setContentTitle("通信3")
                        .setContentText("2222222222222")
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .build();
        Notification newMessageNotification4 =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_error)
                        .setContentTitle("通信4")
                        .setContentText("2222222222222")
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .build();

        Notification summaryNotification =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setContentTitle("摘要")
                        //set content text to support devices running API level < 24
                        .setContentText("Two new messages")
                        .setSmallIcon(R.drawable.img_music)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Alex Faarborg  Check this out")
                                .addLine("Jeff Chang    Launch Party")
                                .setBigContentTitle("2 new messages")
                                .setSummaryText("janedoe@example.com"))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        注意 一组的通知必须使用id不同    不止组内 组外也要不同
//        如果组外相同 会根据后来的notification的实际属性来判断是否是一组显示

        notificationManager.notify(1, newMessageNotification1);
        notificationManager.notify(2, newMessageNotification2);
        notificationManager.notify(3, newMessageNotification3);
        notificationManager.notify(5, newMessageNotification4);
//          单独显示 就是一个可


        notificationManager.notify(0, summaryNotification);
    }

    /**
     * AnalogClock，Button，Chronometer，ImageButton，mageView，ProgressBar，TextView这7种，
     * 布局中的宽，必须为0，wrap_content，或match_parent
     * 因为从别的地方拷贝的布局。里面用的居然是fill_parent。果断抛出异常，源码中是这样的
     */
    int count=0;

    public void show78() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_error)
                .setContentTitle("ssssssssss")
                .setContentText("1111111111111111111111")
                .setChannelId("5555")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        manager.notify(1, notification);
    }
    public void showCustom() {
        Intent resultIntent = new Intent(this, ShowActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        : Notification(channel=chat pri=0 contentView=com.example.notificationtest/0x7f0a0023 vibrate=null sound=null defaults=0x0 flags=0x0 color=0x00000000 vis=PRIVATE)
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);

//        Intent play = new Intent("play");
//        PendingIntent play_intent = PendingIntent.getBroadcast(this, 0, play, 0);
//        remoteView.setOnClickPendingIntent(R.id.notification_pre, play_intent);
        notificationLayout.setOnClickPendingIntent(R.id.tvxx, resultPendingIntent);
//        notificationLayout.setOnClickFillInIntent(R.id.tvxx,resultIntent);
        count++;
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("sadsadasd:"+count)
                .setContentTitle("13213212")
                .setSmallIcon(R.drawable.ic_mute)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setCustomContentView(notificationLayoutExpanded)
                .setCustomBigContentView(notificationLayout)
                .build();
        manager.notify(1, notification);

    }

    /**
     * 1.锁屏
     * 2.等待2s
     * 3.发送通知
     */
    public void showLock() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(new ScreenBroadcastReceiver(), filter);
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏状态下显示
        );

        handler.sendEmptyMessageDelayed(1, 4000);


        Intent fullScreenIntent = new Intent(this, ShowActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_error)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setFullScreenIntent(fullScreenPendingIntent, true);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            show78();
        }
    };

    class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;
        private static final String TAG = "ScreenBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            Log.i(TAG, "onReceive: " + action);
            switch (action) {
                case Intent.ACTION_SCREEN_ON:

                    break;
                case Intent.ACTION_SCREEN_OFF:
                    showCustom();

                    break;
                case Intent.ACTION_USER_PRESENT:


                    break;


            }

        }
    }

   public void  showFloat(){

    }
    private Button mShow7;
    private Button mShow8;

    private Button mShow_float;
    private Button mShow_lock;

    private Button mShow_custom;
    private Button mShow_jump2;
    private Button mShow_jump1;
    private Button mShow_reply;
    private Button mShow_app;
    private Button mShow_group;

    // End Of Content View Elements

    private void bindViews() {

        mShow7 = (Button) findViewById(R.id.show7);
        mShow8 = (Button) findViewById(R.id.show8);
        mShow_custom = (Button) findViewById(R.id.show_custom);
        mShow_jump1 = (Button) findViewById(R.id.show_jump1);
        mShow_jump2 = (Button) findViewById(R.id.show_jump2);
        mShow_reply = (Button) findViewById(R.id.show_reply);
        mShow_float = (Button) findViewById(R.id.show_float);
        mShow_lock = (Button) findViewById(R.id.show_lock);
        mShow_app = (Button) findViewById(R.id.show_app);
        mShow_group = (Button) findViewById(R.id.show_group);


    }

    private static final String TAG = "MainActivity";
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

}
