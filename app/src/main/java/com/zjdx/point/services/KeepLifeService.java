package com.zjdx.point.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.zjdx.point.NameSpace;
import com.zjdx.point.PointApplication;
import com.zjdx.point.data.repository.RegisterRepository;
import com.zjdx.point.data.repository.Repository;
import com.zjdx.point.data.repository.TravelRepository;
import com.zjdx.point.db.model.Location;
import com.zjdx.point.db.model.TravelRecord;
import com.zjdx.point.event.UpdateMsgEvent;
import com.zjdx.point.ui.travel.TravelActivity;
import com.zjdx.point.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KeepLifeService extends IntentService {


    private String mPackName;
    private ActivityManager mActivityManager;

    private final Timer timerMail = new Timer();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public KeepLifeService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Android O上才显示通知栏
        if (Build.VERSION.SDK_INT >= 26) {
            showNotify();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //显示通知栏
    @SuppressLint("NewApi")
    public void showNotify() {
        //调用这个方法把服务设置成前台服务
        startForeground(Utils.NOTIFY_ID, Utils.buildNotification(getApplicationContext()));
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        String process = getProcessName();
        mPackName = getPackageName();

        timerMail.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean isRun = isRunningProcess(mActivityManager, mPackName);
                LogUtils.i(String.format("onCreate: %s %s pid=%d uid=%d isRun=%s", mPackName, process, android.os.Process.myPid(), android.os.Process.myUid(), isRun));

                if (!isRun) {
                    if (SPUtils.getInstance().getBoolean(NameSpace.Companion.getISRECORDING())) {
                        TravelRepository repository = ((PointApplication) getApplication()).getTravelRepository();
                        TravelRecord travelRecord = repository.getTravelRecordById(SPUtils.getInstance().getString(NameSpace.Companion.getRECORDINGID()));
                        saveTravelRecord(repository, travelRecord);
                        return;
                    }
                }
            }
        }, 2000, 2000);
    }

    /**
     * 获取当前进程名称
     *
     * @return
     */
    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void saveTravelRecord(TravelRepository repository, TravelRecord travelRecord) {
        Long endTime = new Date().getTime();
        if (endTime - travelRecord.getStartTime() < 60 * 1000) {
            List<Location> locations = repository.getLocationListById(travelRecord.getId());
            repository.deteleLocation(locations);
            repository.deteleTravel(travelRecord);
        } else {
            travelRecord.setEndTime(endTime);
            repository.updateTravelRecord(travelRecord);
            UpdateMsgEvent event = new UpdateMsgEvent();
            event.setMsg("记录保存完成。");
            EventBus.getDefault().post(event);
        }
        if (TravelActivity.Companion.getMLocationClient() != null) {
            TravelActivity.Companion.getMLocationClient().disableBackgroundLocation(true);
            TravelActivity.Companion.getMLocationClient().stopLocation();
            TravelActivity.Companion.getMLocationClient().onDestroy();

        }

        SPUtils.getInstance().put(NameSpace.Companion.getISRECORDING(), false);
        SPUtils.getInstance().put(NameSpace.Companion.getRECORDINGID(), "");
    }

    /**
     * 进程是否存活
     *
     * @return
     */
    public static boolean isRunningProcess(ActivityManager manager, String processName) {
        if (manager == null)
            return false;
        List<ActivityManager.RunningAppProcessInfo> runnings = manager.getRunningAppProcesses();
        if (runnings != null) {
            for (ActivityManager.RunningAppProcessInfo info : runnings) {
                if (TextUtils.equals(info.processName, processName)) {
                    return true;
                }
            }
        }
        return false;
    }

}