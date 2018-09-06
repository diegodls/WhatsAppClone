package com.diegol.whatsappclone.whatsappclone.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.diegol.whatsappclone.whatsappclone.Model.Mensagem;
import com.diegol.whatsappclone.whatsappclone.R;
import com.diegol.whatsappclone.whatsappclone.helper.Preferencias;

import java.util.ArrayList;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {
    private Context context;
    private ArrayList<Mensagem> mensagens;


    public MensagemAdapter(@NonNull Context c, @NonNull ArrayList<Mensagem> objects) {
        super(c, 0, objects);

        this.context = c;
        this.mensagens = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;

        if (mensagens != null) {
            //recuperar dados do usuario

            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRemetente = preferencias.getIdentificador();

            //inicialia objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            Mensagem mensagem = mensagens.get(position);

            if ( idUsuarioRemetente.equals( mensagem.getId_usuario() ) ) {

                view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);

            } else {

                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
            }


            TextView texto_mensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            texto_mensagem.setText(mensagem.getMensagem());

        }


        return view;
    }
}
