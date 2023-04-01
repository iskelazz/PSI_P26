package es.udc.psi;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyCounterService extends IntentService {

    private int currentCount = 0;
    private final IBinder binder = new MyBinder();
    private boolean shouldStopCounting = false;
    private int finalCount;

    public MyCounterService() {
        super("MyCounterService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int numberOfCounts = intent.getIntExtra("number_of_counts", 0);
            finalCount = numberOfCounts;

            if (intent.getAction() != null && intent.getAction().equals("STOP_SERVICE")) {
                shouldStopCounting = true;
                return;
            }

            for (int i = 1; i <= finalCount && !shouldStopCounting; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentCount = i;
                Log.d("MyCounterService", "Count: " + i);
                if (i == finalCount) {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("es.udc.psi.COUNTER_FINISHED");
                    sendBroadcast(broadcastIntent);
                }
            }
            shouldStopCounting = false;
        }
    }

    public class MyBinder extends Binder {
        MyCounterService getService() {
            return MyCounterService.this;
        }
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setFinalCount(int finalCount) {
        this.finalCount = finalCount;
    }

    public void stopCounting() {
        shouldStopCounting = true;
    }
}