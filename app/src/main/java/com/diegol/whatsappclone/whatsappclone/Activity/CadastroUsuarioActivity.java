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
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText etxt_nome;
    private EditText etxt_email;
    private EditText etxt_senha;
    private Button btn_cadastrar;
    private Usuario usuario;

    private String nome;
    private String email;
    private String senha;

    private FirebaseAuth autencicacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        etxt_nome = findViewById(R.id.etxt_nome);
        etxt_email = findViewById(R.id.etxt_email);
        etxt_senha = findViewById(R.id.etxt_senha);
        btn_cadastrar = findViewById(R.id.btn_cadastrar);

        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nome = etxt_nome.getText().toString();
                senha = etxt_senha.getText().toString();
                email = etxt_email.getText().toString();

                if (nome.isEmpty() || nome.length() < 2 || senha.isEmpty() || email.isEmpty()) {
                    msgToast("Preencha todos os campos corretamente!", 1);

                } else {
                    usuario = new Usuario();
                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    cadastrarUsuario();

                }


            }
        });
    }

    private void cadastrarUsuario() {
        autencicacao = ConfiguracaoFIrebase.getFirebaseAuth();
        autencicacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(CadastroUsuarioActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //task é um objeto de retorno após o envio ao firebase
                        //o if testa se deu certo o cadastro
                        if (task.isSuccessful()) {
                            msgToast("Sucesso ao cadastrar usuário!", 1);
                            FirebaseUser usurioFirebase = task.getResult().getUser();//pega o resultado do usuario cadastrado
                            //usuario.setId(usurioFirebase.getUid());//pega UID do usuario cadastrado acima e inserir com ID

                            String identificador_usuario = Base64Custom.codificar_base64(usuario.getEmail());
                            usuario.setId(identificador_usuario);
                            usuario.salvar();



                            Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);

                            preferencias.salvarDados(identificador_usuario, usuario.getNome());

                            abrirLoginUsuario();

                            //autencicacao.signOut();//após cadastrar o usuario, ele já está logado, aqui fazemos o logoff
                            finish();

                        } else {
                            String erroExcecao = "";
                            try {
                                //pegar os erros caso apresente
                                throw task.getException();

                            } catch (FirebaseAuthWeakPasswordException e) {
                                erroExcecao = "Digite uma senha mais forte, com mais de 6 caracteres, numeros e/ou letras.";

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExcecao = "O email digitado é invalido, digite outro email.";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erroExcecao = "Esse email já está em uso, utilize outro email valido.";
                            } catch (Exception e) {
                                e.printStackTrace();
                                erroExcecao = "Erro ao cadastrar usuário,  tente novamente";
                            }

                            msgToast("Erro: " + erroExcecao, 1);
                        }

                    }
                });

    }

    private void abrirLoginUsuario() {
        Intent intent = new Intent(CadastroUsuarioActivity.this, LoginEmailActivity.class);
        startActivity(intent);
        finish();
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
