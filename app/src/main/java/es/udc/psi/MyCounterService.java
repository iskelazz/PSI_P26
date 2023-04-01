package es.udc.psi;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyCounterService extends Service {
    private static final String TAG = "MyCounterService";
    private volatile boolean isStopped = true;
    private int currentCount = 0;
    private int finalCount;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            finalCount = intent.getIntExtra("number_of_counts", 0);
            isStopped = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doCounting();
                }
            }).start();
        }
        return START_NOT_STICKY;
    }

    private void doCounting() {
        while (!isStopped && currentCount < finalCount) {
            currentCount++;
            Log.d("MyCounterService", "Count: " + currentCount);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isStopped || currentCount == finalCount) {
                Intent counterFinishedIntent = new Intent("es.udc.psi.COUNTER_FINISHED");
                sendBroadcast(counterFinishedIntent);
                break;
            }
        }
    }

    public void resetCounter() {
        currentCount = 0;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setFinalCount(int finalCount) {
        this.finalCount = finalCount;
    }

    public void stopService() {
        isStopped = true;
    }

    public class MyBinder extends Binder {
        public MyCounterService getService() {
            return MyCounterService.this;
        }
    }

    private final IBinder binder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}