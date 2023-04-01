package es.udc.psi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button buttonSendBroadcast;
    private Switch switchStartStopService;
    private EditText editTextNumberOfCounts;
    private MyBroadcastReceiver myBroadcastReceiver;
    private Switch switchCounterBound;

    private MyCounterService myCounterService;
    private Button buttonGet;
    private Button buttonSet;
    private boolean isBound = false;

    private BroadcastReceiver counterFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("es.udc.psi.COUNTER_FINISHED")) {
                switchStartStopService.setChecked(false);
                switchCounterBound.setChecked(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSendBroadcast = findViewById(R.id.button_send_broadcast);
        switchStartStopService = findViewById(R.id.switch_start_stop_service);
        editTextNumberOfCounts = findViewById(R.id.editText_number_of_counts);
        switchCounterBound = findViewById(R.id.switch_counter_bound);
        buttonGet = findViewById(R.id.button_get);
        buttonSet = findViewById(R.id.button_set);
        buttonSendBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCustomBroadcast();
            }
        });

        switchStartStopService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int numberOfCounts = Integer.parseInt(editTextNumberOfCounts.getText().toString());
                    Intent intent = new Intent(MainActivity.this, MyCounterService.class);
                    intent.putExtra("number_of_counts", numberOfCounts);
                    startService(intent);
                    if (!isBound) {
                        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                    }
                } else {
                    if (isBound) {
                        myCounterService.stopService();
                        myCounterService.resetCounter();
                    }
                    Intent intent = new Intent(MainActivity.this, MyCounterService.class);
                    stopService(intent);
                }
            }
        });

        switchCounterBound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(MainActivity.this, MyCounterService.class);
                    if (!isBound) {
                        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                    }
                } else {
                    if (isBound && !switchStartStopService.isChecked()) {
                        unbindService(serviceConnection);
                        isBound = false;
                    }
                }
            }
        });

        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    int currentCount = myCounterService.getCurrentCount();
                    Toast.makeText(MainActivity.this, "Current count: " + currentCount, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Service is not bound", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    int newCount = Integer.parseInt(editTextNumberOfCounts.getText().toString());
                    myCounterService.setFinalCount(newCount);
                } else {
                    Toast.makeText(MainActivity.this, "Service is not bound", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("es.udc.PSI.broadcast.GENERAL");
        registerReceiver(myBroadcastReceiver, filter);

        IntentFilter counterFinishedFilter = new IntentFilter("es.udc.psi.COUNTER_FINISHED");
        registerReceiver(counterFinishedReceiver, counterFinishedFilter);
        //bindService(new Intent(MainActivity.this, MyCounterService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyCounterService.MyBinder binder = (MyCounterService.MyBinder) service;
            myCounterService = binder.getService();
            isBound = true;
            if (!switchStartStopService.isChecked()) {
                myCounterService.stopService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
        @Override
        protected void onDestroy() {
            super.onDestroy();
            unregisterReceiver(myBroadcastReceiver);
            unregisterReceiver(counterFinishedReceiver);
            if (isBound) {
                unbindService(serviceConnection);
                isBound = false;
            }
        }

        private void sendCustomBroadcast() {
            Intent intent = new Intent();
            intent.setAction("es.udc.PSI.broadcast.GENERAL");
            sendBroadcast(intent);
        }
}

