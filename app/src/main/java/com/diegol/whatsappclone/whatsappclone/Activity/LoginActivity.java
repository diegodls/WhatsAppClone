package com.diegol.whatsappclone.whatsappclone.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diegol.whatsappclone.whatsappclone.R;
import com.diegol.whatsappclone.whatsappclone.helper.Permissao;
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias_old;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;
import java.util.Random;

/*Esta classe serve para fazer login ou cadastro atraves de SMS
Foi desenvolvida e utilizada outra classe com o metodo de login
utilizando email/senha pelo firebase
 */

public class LoginActivity extends AppCompatActivity {
    private EditText etxt_nome;
    private EditText etxt_telefone;
    private EditText etxt_codPais;
    private EditText etxt_codEstado;
    private Button btn_cadastrar;
    private String[] permissoesNecessarias = new String[]{
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //pedir permissões necessárias para a classe Permissoes
        Permissao.validaPermissoes(1, this,permissoesNecessarias);

        etxt_nome = findViewById(R.id.etxt_nome);
        etxt_telefone = findViewById(R.id.etxt_telefone);
        etxt_codPais = findViewById(R.id.etxt_codPais);
        etxt_codEstado = findViewById(R.id.etxt_codEstado);
        btn_cadastrar = findViewById(R.id.btn_cadastrar);

        //criando a mascara(lembre-se de adicionar o classpath no build grade project
        //implementation 'com.github.rtoshiro.mflibrary:mflibrary:1.0.0'

        SimpleMaskFormatter smfTelefone = new SimpleMaskFormatter("NNNNN-NNNN");
        //aplicando um texto a um edittext
        MaskTextWatcher mtwTelefone = new MaskTextWatcher(etxt_telefone, smfTelefone);
        //Usando um listener para aplicar a mascara ao edittext
        etxt_telefone.addTextChangedListener(mtwTelefone);

        SimpleMaskFormatter smfcodPais = new SimpleMaskFormatter("+NN");
        MaskTextWatcher mtwcodPais = new MaskTextWatcher(etxt_codPais, smfcodPais);
        etxt_codPais.addTextChangedListener(mtwcodPais);

        SimpleMaskFormatter smfcodEstado = new SimpleMaskFormatter("NN");
        MaskTextWatcher mtwcodEstado = new MaskTextWatcher(etxt_codEstado, smfcodEstado);
        etxt_codEstado.addTextChangedListener(mtwcodEstado);

        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String nomeUsuario = etxt_nome.getText().toString();
                String estadoUsuario = etxt_codEstado.getText().toString();
                String paisUsuario = etxt_codPais.getText().toString();
                String teleUsuario = etxt_telefone.getText().toString();
                String telefoneCompleto =//formatando o numero completo tirando o + e -
                                etxt_codPais.getText().toString().replace("+", "") + //retirando o "+" do +55
                                etxt_codEstado.getText().toString() +
                                etxt_telefone.getText().toString().replace("-", ""); //retirando o "-"





                if (nomeUsuario.isEmpty() || estadoUsuario.isEmpty() || paisUsuario.isEmpty() || teleUsuario.isEmpty()) {
                    msgToast("Dados faltando!", 0);

                } else {
                    //gerando token
                    Random r = new Random();
                    int random =  r.nextInt((9999 - 1000) + 1000);
                    String token = String.valueOf(random);
                    String msgEnvio = "Whatsapp: código de confirmação: " + token;

                    //salvando preferencias
                    Preferencias_old preferencias = new Preferencias_old
                            (LoginActivity.this);


                    //preferencias.salvarDados(nomeUsuario, telefoneCompleto, token);

                    //caso for testar no emulador, tem que setar a variavel telefoneCompleto com o numero
                    //do emulador, basta ir no "sobre o telefone" no emulador e ver lá os 4 ultimos digitos
                    //como abaixo
                    //telefoneCompleto = "4556"

                    //envio sms (email)
                    boolean enviadoSMS = enviarSms("+" + telefoneCompleto, msgEnvio);

                    if(enviadoSMS){

                        Intent intent = new Intent(LoginActivity.this, ValidadorActivity.class);
                        startActivity(intent);
                        finish();

                    }else{

                        msgToast("Problema o enviar SMS!!!", 1);
                    }

                    //retornando dados
                    HashMap<String, String> usuario = preferencias.getDadosUsuario();
                    msgToast("Nome: " + usuario.get("nome") + "\n" +
                            "Telefone: " + usuario.get("telefone") + "\n" +
                            "Token: " + usuario.get("token") + "\n" +
                            "Enviado: " + enviadoSMS,1);

                    //print dos resultados
                    //Log.i("TELEFONE", "INFO:" + telefoneCompleto + "\n" + "Token:" + token);
                    //msgToast(telefoneCompleto + "\n" + token, 1);

                }




            }
        });
    }

    //envio sms
    private boolean enviarSms(String telefone, String msg){

        try{
            SmsManager smsManager = SmsManager.getDefault();//recuperar instância da classe smsmaneger
            smsManager.sendTextMessage(telefone, null, msg, null, null);
            return true;

        }catch (Exception e){
            e.printStackTrace();

            return false;

        }


    }


    //tratar caso a pessoa negar as permissões
    public void onRequestPermissionsResult(int requestCode, String[] permissios, int[] grantResults){
        //sobrescrevendo o metodo original
        super.onRequestPermissionsResult(requestCode, permissios, grantResults);

        //percorrer as permissoes que o usuário deu(ou negou)
        for(int resultado : grantResults){

            //verifica se a permissao foi negada
            if(resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }

        }
    }

    //metedo para criar um alerta na tela
    public void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar este aplicativo, você deve aceitar as permissões necessárias");
        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dlg = builder.create();
        dlg.show();
    }


    private void msgToast(String msg, int duracao) {//duracao: 0 = rapido / 1 = demorado

        if (duracao == 0) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }


    }

}
