package com.diegol.whatsappclone.whatsappclone.helper;

import android.content.Context;
import android.widget.Toast;


public class msgToast {

    private void msgToast(Context contexto, String msg, int duracao) {//duracao: 0 = rapido / 1 = demorado

        if (duracao == 0) {
            Toast.makeText(contexto, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(contexto, msg, Toast.LENGTH_LONG).show();
        }
    }
}
