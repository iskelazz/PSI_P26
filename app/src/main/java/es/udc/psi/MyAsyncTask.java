package es.udc.psi;

import android.os.AsyncTask;
import android.widget.Switch;
import android.widget.TextView;

public class MyAsyncTask extends AsyncTask<Integer, Integer, Void> {

    private TextView textViewAsyncTaskProgress;
    private Switch switchAsyncTask;

    public MyAsyncTask(TextView textViewAsyncTaskProgress, Switch switchAsyncTask) {
        this.textViewAsyncTaskProgress = textViewAsyncTaskProgress;
        this.switchAsyncTask = switchAsyncTask;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        int numberOfCounts = params[0];
        for (int i = 1; i <= numberOfCounts; i++) {
            if (isCancelled()) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(i);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        textViewAsyncTaskProgress.setText("AsyncTask progress: " + values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        textViewAsyncTaskProgress.setText("AsyncTask finished");
        switchAsyncTask.setChecked(false);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        textViewAsyncTaskProgress.setText("AsyncTask cancelled");
        switchAsyncTask.setChecked(false);
    }
}