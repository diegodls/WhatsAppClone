package com.diegol.whatsappclone.whatsappclone.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.diegol.whatsappclone.whatsappclone.Model.Contato;
import com.diegol.whatsappclone.whatsappclone.R;

import java.util.ArrayList;

public class ContatoAdapter extends ArrayAdapter<Contato> {

    private ArrayList<Contato> contatos;
    private Context context;


    public ContatoAdapter(@NonNull Context c, @NonNull ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.contatos = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //verifica se a lista está vazia(só mostra contatos se tiver itens)
        if( contatos != null){

            //inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //montar uma view apartir do xml

            view = inflater.inflate(R.layout.lista_contato, parent, false);

            //recupera elemento para exibição
            TextView nome_contato = (TextView) view.findViewById(R.id.tv_nome);
            TextView nome_email = (TextView) view.findViewById(R.id.tv_email);

            Contato contato = contatos.get(position);
            nome_contato.setText(contato.getNome());
            nome_email.setText(contato.getEmail());

        }

        return view;
    }
}
