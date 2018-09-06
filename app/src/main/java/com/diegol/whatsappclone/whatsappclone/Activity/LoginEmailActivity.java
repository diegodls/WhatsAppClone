package com.diegol.whatsappclone.whatsappclone.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diegol.whatsappclone.whatsappclone.Config.ConfiguracaoFIrebase;
import com.diegol.whatsappclone.whatsappclone.Model.Usuario;
import com.diegol.whatsappclone.whatsappclone.R;
import com.diegol.whatsappclone.whatsappclone.helper.Base64Custom;
import com.diegol.whatsappclone.whatsappclone.helper.ClasseTeste;
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class LoginEmailActivity extends AppCompatActivity {

    private EditText etxt_email;
    private EditText etxt_senha;
    private Button btn_logar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private String email, senha;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerUsuario;
    private String identificadorUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        verificarLogado();

        etxt_email = findViewById(R.id.etxt_email);
        etxt_senha = findViewById(R.id.etxt_senha);
        btn_logar = findViewById(R.id.btn_logar);

        btn_logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etxt_email.getText().toString();
                senha = etxt_senha.getText().toString();
                if (email.isEmpty() || senha.isEmpty()) {
                    msgToast("Preencha todos os campos com dados validos e tente novamente.", 0);
                } else {

                    usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    validarLogin();
                }

            }
        });

    }

    private void verificarLogado(){//metodo para verificar se está logado, caso esteja, vai direto pra tela de mensagens
        autenticacao = ConfiguracaoFIrebase.getFirebaseAuth();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();

        }
    }

    private void validarLogin() {
        autenticacao = ConfiguracaoFIrebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    identificadorUsuarioLogado = Base64Custom.codificar_base64(usuario.getEmail());


                    firebase = ConfiguracaoFIrebase.getFirebase()
                            .child("usuarios")
                            .child(identificadorUsuarioLogado);


                    valueEventListenerUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Usuario usuarioRecuperado = dataSnapshot.getValue(Usuario.class);

                            Preferencias preferencias = new Preferencias(LoginEmailActivity.this);

                            preferencias.salvarDados(identificadorUsuarioLogado,usuarioRecuperado.getNome());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);




                    msgToast("Sucesso ao fazer o login", 0);

                    abrirTelaPrincipal();

                } else {
                    String erroException = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        erroException = "Usuario não existente ou desativado.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroException = "Senha incorreta, tente novamente.";
                    } catch (Exception e) {
                        e.printStackTrace();
                        erroException = "Erro desconhecido, tente novamente mais tarde.";
                    }
                    msgToast("Erro: " + erroException, 1);
                }
            }
        });
    }

    public void abrirTelaPrincipal() {
        Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void abrirCadastroUsuarario(View view) {
        Intent intent = new Intent(LoginEmailActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);

    }

    public void abrirClasseTeste(View view) {
        Intent intent = new Intent(LoginEmailActivity.this, ClasseTeste.class);
        startActivity(intent);
    }

    //metôdo para mostrar mensagens
    private void msgToast(String msg, int duracao) {//duracao: 0 = rapido / 1 = demorado

        if (duracao == 0) {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }


}
