package com.diegol.whatsappclone.whatsappclone.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.diegol.whatsappclone.whatsappclone.Adapter.TabAdapter;
import com.diegol.whatsappclone.whatsappclone.Config.ConfiguracaoFIrebase;
import com.diegol.whatsappclone.whatsappclone.Model.Contato;
import com.diegol.whatsappclone.whatsappclone.Model.Usuario;
import com.diegol.whatsappclone.whatsappclone.R;
import com.diegol.whatsappclone.whatsappclone.helper.Base64Custom;
import com.diegol.whatsappclone.whatsappclone.helper.ClasseTeste;
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias;
import com.diegol.whatsappclone.whatsappclone.helper.SlidingTabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //getInstance é para ter acesso ao banco de dados
    //getReference é para acessar a raiz do banco de dados

    private Button btn_sair;
    private FirebaseAuth usuarioAutenticacao;
    private android.support.v7.widget.Toolbar toolbar;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificador_contato;
    private DatabaseReference firebasereferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        usuarioAutenticacao = ConfiguracaoFIrebase.getFirebaseAuth();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Whatsapp");
        //setsuport é um metodo de suporte para retorno
        setSupportActionBar(toolbar);

        slidingTabLayout = findViewById(R.id.slt_tabs);
        viewPager = findViewById(R.id.vp_pagina);

        //configurar sliding tab
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));

        //configurar tab adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //criar um getinflater já com o contexto, para exibir os menus na tela
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);//vai pegar o menu criado(menu_main) e jogar no menu padrão(menu)
        return true;
    }


    //metodo para aplicar ações aos itens selecionados da toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //retorna qual item foi selecionado
        switch (item.getItemId()){

            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_teste:
                abrirClasseTeste();
                return true;
            case R.id.item_pesquisar:
                msgToast("Pesquisar", 0);
                return true;
            case R.id.item_configuracao:
                msgToast("Configurações", 0);
                return true;
            case R.id.item_adicionar:
                msgToast("Adicionar", 0);
                abrirCadastroContato();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void abrirLoginEmail(){
        Intent intent = new Intent(MainActivity.this, LoginEmailActivity.class);
        startActivity(intent);
        finish();
    }

    private void deslogarUsuario(){

        usuarioAutenticacao.signOut();
        abrirLoginEmail();
    }

    private void msgToast(String msg, int duracao){ //0 para mensagem rápida, 1 para mensagem longa
        if(duracao ==0){
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    private void abrirClasseTeste(){
        Intent intent = new Intent(MainActivity.this, ClasseTeste.class);
        startActivity(intent);
        finish();
    }

    private void abrirCadastroContato(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        alertDialog.setTitle("Novo Contato");
        alertDialog.setMessage("Email do Usuário");
        alertDialog.setCancelable(false);

        //caixa de texto dentro do dialog

        final EditText editText = new EditText(MainActivity.this);
        alertDialog.setView(editText);

        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String emailContato = editText.getText().toString();

                if(emailContato.isEmpty()){
                    msgToast("Campo Email Vazio", 1);
                }else{
                    //verificar se o contato existe
                    identificador_contato = Base64Custom.codificar_base64(emailContato);

                    //recuperar instancia firebase
                    firebasereferencia = ConfiguracaoFIrebase.getFirebase().child("usuarios").child(identificador_contato);

                    //o metodo abaixo recupera um dado após sua mudança uma unica vez sem anotificação
                    firebasereferencia.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null){

                                //recuperar dados do contato a ser adicionado
                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);

                                //recuperar identificador
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado = preferencias.getIdentificador();

                                usuarioAutenticacao.getCurrentUser().getEmail();
                                firebasereferencia = ConfiguracaoFIrebase.getFirebase();
                                firebasereferencia = firebasereferencia.child("contatos")
                                        .child(identificadorUsuarioLogado)
                                        .child(identificador_contato);

                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(identificador_contato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());

                                firebasereferencia.setValue(contato);

                            }else{
                                msgToast("Contato não encontrado!", 1);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog.create();
        alertDialog.show();
    }
}


