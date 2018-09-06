package com.diegol.whatsappclone.whatsappclone.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diegol.whatsappclone.whatsappclone.R;
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias;
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias_old;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;

public class ValidadorActivity extends AppCompatActivity {

    private EditText etxt_codVerifica;
    private Button btn_validar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        etxt_codVerifica    = findViewById(R.id.etxt_codVerifica);
        btn_validar         = findViewById(R.id.btn_validar);

        SimpleMaskFormatter smfcodVerifica = new SimpleMaskFormatter("NNNN"); //cria a mascara
        MaskTextWatcher mtwcodVerifica = new MaskTextWatcher(etxt_codVerifica,smfcodVerifica); // add um listener
        etxt_codVerifica.addTextChangedListener(mtwcodVerifica); //aplica a mascara ao etxt

        btn_validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //recuperar dados do Preferencias
                Preferencias_old preferencias = new Preferencias_old(ValidadorActivity.this);
                HashMap<String, String> usuario = preferencias.getDadosUsuario();
                String tokenGerado = usuario.get("token");
                String tokenDigitado = etxt_codVerifica.getText().toString();

                if(tokenDigitado.equals(tokenGerado)){
                    msgToast("Token valida!!!", 0);

                }else
                    msgToast("Token não valido!!!", 0);
            }
        });

    }
    private void msgToast(String msg, int duracao) {//duracao: 0 = rapido / 1 = demorado

        if (duracao == 0) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }


    }
}
