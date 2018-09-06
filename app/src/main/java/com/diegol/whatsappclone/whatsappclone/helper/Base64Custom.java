package com.diegol.whatsappclone.whatsappclone.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codificar_base64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");
    }

    public static String decodificar_base64(String texto_codificado){

        return new String(Base64.decode(texto_codificado, Base64.DEFAULT));
    }
}
