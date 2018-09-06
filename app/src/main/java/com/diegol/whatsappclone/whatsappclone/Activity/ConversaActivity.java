package com.diegol.whatsappclone.whatsappclone.Activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.diegol.whatsappclone.whatsappclone.Adapter.MensagemAdapter;
import com.diegol.whatsappclone.whatsappclone.Config.ConfiguracaoFIrebase;
import com.diegol.whatsappclone.whatsappclone.Model.Conversa;
import com.diegol.whatsappclone.whatsappclone.Model.Mensagem;
import com.diegol.whatsappclone.whatsappclone.R;
import com.diegol.whatsappclone.whatsappclone.helper.Base64Custom;
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversaActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private EditText etxt_mensagem;
    private ImageButton btn_enviar;
    private DatabaseReference firebase;
    private ListView lv_conversas;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenermensagens;

    //dados destinatario
    private String nome_usuario_destinatario;
    private String id_usuario_destinatario;


    //dados remetente
    private String id_usuario_remetente;
    private String nome_usuario_remetente;




    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = findViewById(R.id.tb_conversa);
        etxt_mensagem = findViewById(R.id.etxt_mensagem);
        btn_enviar = findViewById(R.id.btn_enviar);
        lv_conversas = findViewById(R.id.lv_conversas);

        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        id_usuario_remetente = preferencias.getIdentificador();
        nome_usuario_remetente = preferencias.getNome();


        Bundle extra = getIntent().getExtras();

        if (extra != null) {

            nome_usuario_destinatario = extra.getString("nome");
            String email_usuario_destinatario = extra.getString("email");
            id_usuario_destinatario = Base64Custom.codificar_base64(email_usuario_destinatario);

        }

        //configurar toolbar
        toolbar.setTitle(nome_usuario_destinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //configurar listview e adapter
        mensagens = new ArrayList<>();

        adapter = new MensagemAdapter(
                ConversaActivity.this,
                mensagens);
        lv_conversas.setAdapter(adapter);


        //recuperar mensagens

        firebase = ConfiguracaoFIrebase.getFirebase()
                .child("mensagens")
                .child(id_usuario_remetente)
                .child(id_usuario_destinatario);

        //listener para mensagens
        valueEventListenermensagens = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //limpar o "mensagens"
                mensagens.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    //mensagens.add(mensagem.getMensagem()); usar com o adapter padrão
                    mensagens.add(mensagem);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenermensagens);

        //enviar
        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String texto_msg = etxt_mensagem.getText().toString();
                if (texto_msg.isEmpty()) {
                    msgToast("Digite uma mensagem para enviar");
                } else {

                    Mensagem mensagem = new Mensagem();
                    mensagem.setId_usuario(id_usuario_remetente);
                    mensagem.setMensagem(texto_msg);


                    //o salvar abaixo serve para mostrar as mensgens dos dois ao mesmo tempo
                    //senão, apenas a mensagem do usuário logado aparece

                    //salvar mensagem remetente
                    Boolean retorno_mensagem_remetente = salvarMensagem(id_usuario_remetente, id_usuario_destinatario, mensagem);

                    if (!retorno_mensagem_remetente) {
                        msgToast("Erro ao enviar mensagem.");
                    } else {
                        //salvar mensagem destinatário
                        Boolean retorno_mensagem_destinatário = salvarMensagem(id_usuario_destinatario, id_usuario_remetente, mensagem);
                        if (!retorno_mensagem_destinatário) {
                            msgToast("Erro ao enviar mensagem.");
                        }
                    }

                    //salvar conversa da tela conversa

                    Conversa conversaD = new Conversa();
                    conversaD.setIdUsuario(id_usuario_destinatario);
                    conversaD.setNome(nome_usuario_destinatario);
                    conversaD.setMensagem(texto_msg);

                    Boolean retornoConversaRemetente = salvarConversa(id_usuario_remetente, id_usuario_destinatario, conversaD);

                    if(!retornoConversaRemetente){
                        msgToast("Erro ao salvar conversa");
                    }else{

                        Conversa conversaR = new Conversa();
                        conversaR.setIdUsuario(id_usuario_remetente);
                        conversaR.setNome(nome_usuario_remetente);
                        conversaR.setMensagem(texto_msg);

                        Boolean retornoConversaDestinatario = salvarConversa(id_usuario_destinatario, id_usuario_remetente, conversaR);
                        if(!retornoConversaDestinatario){
                            msgToast("Erro ao salvar conversa destinatario");
                        }

                    }
                }
            }
        });

    }

    private boolean salvarMensagem(String idRemetente, String idDestinarario, Mensagem mensagem) {

        try {
            firebase = ConfiguracaoFIrebase.getFirebase().child("mensagens");
            firebase.child(idRemetente)
                    .child(idDestinarario)
                    .push()//o .push() cria identificador unico para o nó em questão
                    .setValue(mensagem);
            etxt_mensagem.setText("");


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String idDestinarario, Conversa conversa) {

        try {

            firebase = ConfiguracaoFIrebase.getFirebase().child("conversas");
            firebase.child(idRemetente)
                    .child(idDestinarario)
                    .setValue(conversa);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void msgToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenermensagens);
    }
}
