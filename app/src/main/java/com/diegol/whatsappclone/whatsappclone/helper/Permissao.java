package com.diegol.whatsappclone.whatsappclone.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {
    public static boolean validaPermissoes(int requestCode, Activity activity, String[] permissoes) {

        //verificar a versão do SDK, caso seja menor que 23, não precisa verificar permissões
        if (Build.VERSION.SDK_INT >= 23) {

            List<String> listaPermisoes = new ArrayList<String>();

            //usando o for para percorrer as permissões passadas pelas activitys
            for (String permissao : permissoes) {

                //o metodo contextcompact verifica se a activity passada
                //possui a permissao requisitada(também passada)
                boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;

                //se a pessoa não tem a permissao, adiciona em um
                //array com as permissões que não tem
                if (!validaPermissao) {
                    //se não validou ainda
                    listaPermisoes.add(permissao);
                }
            }

            //se tiver a permissão, retorna true para a permissão requisistada
            if (listaPermisoes.isEmpty()) {
                return true;
            }

            //convertendo o List listaPermissoes para string, pois o metodo de solicitar
            //necessita de um array de string e não um list de string
            String[] novasPermissoes = new String[listaPermisoes.size()];
            listaPermisoes.toArray(novasPermissoes);

            //requestCode é um código de requisição de onde foi requisitado as permissoes
            //
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);


        }
        return true;
    }
}
