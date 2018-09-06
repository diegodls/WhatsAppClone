package com.diegol.whatsappclone.whatsappclone.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diegol.whatsappclone.whatsappclone.Config.ConfiguracaoFIrebase;
import com.diegol.whatsappclone.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ClasseTeste extends AppCompatActivity {

    private Button      btn_inserir;
    private EditText    etxt_texto;
    //private DatabaseReference referenciaDatabase = FirebaseDatabase.getInstance().getReference();
    private Date data;
    private Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classe_teste);


        etxt_texto = findViewById(R.id.etxt_texto);
        btn_inserir = findViewById(R.id.btn_inserir);

        btn_inserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String texto = etxt_texto.getText().toString();

                if(texto.isEmpty()){
                    msgToast("Digite algo antes!");


                }else{
                    final String textofinal = texto;
                    data = Calendar.getInstance().getTime();
                    msgToast("Inserindo: " + texto);
                    //referenciaDatabase.child("teste").child(data.toString()).setValue(texto);
                    ConfiguracaoFIrebase.getFirebase().child("teste")
                            .child(data.toString()).setValue(texto)
                            .addOnCompleteListener(ClasseTeste.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        msgToast("Sucesso ao inserir \"" + textofinal + "\" no Banco:");
                                    }else{
                                        msgToast("Erro ao inserir \"" + textofinal + "\" no Banco:");
                                    }
                                }
                            });

                    int r = random.nextInt(1000);
                    etxt_texto.setText("Teste: " + r);

                }

            }
        });
    }
    private void msgToast(String msg){

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
