package es.udc.psi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String message = "Acción desconocida";

        if (action != null) {
            if (action.equals(Intent.ACTION_USER_PRESENT)) {
                message = "Acción del sistema: USER_PRESENT";
            } else if (action.equals("es.udc.PSI.broadcast.GENERAL")) {
                message = "Acción propia: es.udc.PSI.broadcast.GENERAL";
            }
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
