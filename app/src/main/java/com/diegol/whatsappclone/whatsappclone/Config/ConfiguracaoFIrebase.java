package com.diegol.whatsappclone.whatsappclone.Config;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//usado o final para não ser instanciada por outras classes
public final class ConfiguracaoFIrebase {
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;


    //uso do static evitar ter que usar a isntancia nas outras classes, ex:
    //ConfiguracaoFIrebase config = new ConfiguracaoFirebase bla bla bla
    public static DatabaseReference getFirebase() {

        //usar o if para instanciar a referencia somente se não existir
        if(referenciaFirebase == null) {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAuth (){

        //If dedicado à checagem da existência de uam instância
        //para não haver a necessidade de criar toda hora uma nova instância do firebaseauth
        if (autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();

        }

        return autenticacao;
    }


}
