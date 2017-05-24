package com.cn.fs.fenxiangxiaoke;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>Created by Administrator</p>
 * <p/>
 * 纷享逍客签到外挂服务
 */
public class EnvelopeService extends AccessibilityService {

    static final String TAG = "Jackie";
    public boolean isClick = false;

    /**
     * 纷享逍客的包名
     */
    static final String WECHAT_PACKAGENAME = "fs.he.com.dailao";
    /**
     * 纷享逍客签到消息的关键字
     */
    static final String ENVELOPE_TEXT_KEY = "[我是标题]";

    Handler handler = new Handler();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();


        Log.d(TAG, "事件1---->" + event.getText());

//        //通知栏事件
//        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
//            List<CharSequence> texts = event.getText();
//            Log.d(TAG, "事件2---->" + texts);
//            if (!texts.isEmpty()) {
//                for (CharSequence t : texts) {
//                    String text = String.valueOf(t);
//                    if (text.contains(ENVELOPE_TEXT_KEY)) {
//                        openNotification(event);
//                        break;
//                    }
//                }
//            }
//        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            openEnvelope(event);
//        }
        openNotification(event);
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            openEnvelope(event);
        }
        if (isClick) {

            isClick = false;
            //返回桌面
            Timer timer=new Timer();
            TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {
                    back2Home();
                }
            };
            timer.schedule(timerTask,5000);


            //如果之前是锁着屏幕的则重新锁回去
        }

    }


    @Override
    public void onInterrupt() {
        Toast.makeText(this, "中断纷享逍客签到服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "连接纷享逍客签到服务", Toast.LENGTH_SHORT).show();
    }


    /**
     * 打开通知栏消息
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNotification(AccessibilityEvent event) {
        if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
            return;
        }

        //以下是精华，将纷享逍客的通知栏消息打开
        Notification notification = (Notification) event.getParcelableData();
        PendingIntent pendingIntent = notification.contentIntent;
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openEnvelope(AccessibilityEvent event) {
        checkKey1();
/**        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
 //点中了纷享逍客签到，下一步就是去拆纷享逍客签到
 checkKey1();
 }
 } else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
 //在聊天界面,去点中纷享逍客签到
 //            checkKey2();
 }
 **/
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkKey1() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("签到");
        for (AccessibilityNodeInfo n : list) {
            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            isClick = true;
        }
    }

    /**
     * 返回桌面
     */
    private void back2Home() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }


}