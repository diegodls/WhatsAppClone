package com.diegol.whatsappclone.whatsappclone.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.diegol.whatsappclone.whatsappclone.Activity.ConversaActivity;
import com.diegol.whatsappclone.whatsappclone.Adapter.ConversaAdapter;
import com.diegol.whatsappclone.whatsappclone.Config.ConfiguracaoFIrebase;
import com.diegol.whatsappclone.whatsappclone.Model.Conversa;
import com.diegol.whatsappclone.whatsappclone.R;
import com.diegol.whatsappclone.whatsappclone.helper.Base64Custom;
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Conversa> adapter;
    private ArrayList<Conversa> conversas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerConversas;



    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);
        listView = view.findViewById(R.id.lv_conversas);

        conversas = new ArrayList<>();

        adapter = new ConversaAdapter(getActivity(),conversas);
        listView.setAdapter(adapter);

        //recuperar dados do usuário
        Preferencias preferencias = new Preferencias(getActivity());
        String idUsuarioLogado = preferencias.getIdentificador();

        //recupera dados firebase
        firebase = ConfiguracaoFIrebase.getFirebase()
                .child("conversas")
                .child( idUsuarioLogado);

        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conversas.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Conversa conversa = dados.getValue(Conversa.class);
                    conversas.add(conversa);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Conversa conversa = conversas.get(i);

                Intent intent = new Intent(getActivity(), ConversaActivity.class);
                intent.putExtra("nome",conversa.getNome());
                String email = Base64Custom.decodificar_base64(conversa.getIdUsuario());
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addListenerForSingleValueEvent(valueEventListenerConversas);

    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerConversas);
    }
}
