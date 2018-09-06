package com.diegol.whatsappclone.whatsappclone.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.diegol.whatsappclone.whatsappclone.Activity.ConversaActivity;
import com.diegol.whatsappclone.whatsappclone.Adapter.ContatoAdapter;
import com.diegol.whatsappclone.whatsappclone.Config.ConfiguracaoFIrebase;
import com.diegol.whatsappclone.whatsappclone.Model.Contato;
import com.diegol.whatsappclone.whatsappclone.R;
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;
    private DatabaseReference firebase;
    private ValueEventListener  valueEventListenerContatos;




    public ContatosFragment() {
        // Required empty public constructor
    }



    @Override
    public void onStart() {
        super.onStart();
        //inicia o listener para recuperar os contatos pouco antes do inicio do fragmento
        msgToast("Carregando contatos...");

        firebase.addValueEventListener( valueEventListenerContatos );
    }

    @Override
    public void onStop() {
        super.onStop();
        //remove o listener de contatos para economizar recursos enquanto não está em uso
        firebase.removeEventListener( valueEventListenerContatos );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        //instanciar objetos

        contatos = new ArrayList<>();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //monta listview e adapter
        listView = view.findViewById(R.id.lv_contatos);


        //adapter abaixo é o padrão
        /*adapter = new ArrayAdapter(getActivity(),
                R.layout.lista_contato,
                contatos);*/

        adapter = new ContatoAdapter(getActivity(),contatos);

        listView.setAdapter(adapter);

        //recuperar contatos firebase
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFIrebase.getFirebase()
                .child("contatos")
                .child(identificadorUsuarioLogado);

        //listener para recuperar contatos
        //vai notificar sempre que ocorrer alguma alteração no firebase(db)
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //limpar a lista para não duplicar os contatos
                contatos.clear();

                //listar contatos do firebase
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class);
                    //contatos.add(contato.getNome()); usar com o adapter normal
                    contatos.add(contato);

                }
                adapter.notifyDataSetChanged();//avisa para o adapter que mudou os dados

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //recupera dados a serem passados
                Contato contato = contatos.get(i);

                //envia dados para outra activity(conversa)
                intent.putExtra("nome",contato.getNome());
                intent.putExtra("email",contato.getEmail());
                startActivity(intent);
            }
        });



        return view;
    }

    public void msgToast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}
