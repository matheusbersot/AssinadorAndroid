package br.uff.assinador.visao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import br.uff.assinador.R;
import br.uff.assinador.modelo.Documento;

/**
 * Created by matheus on 21/08/15.
 */
public class DocumentoArrayAdapter extends ArrayAdapter<Documento> {

    private final Context context;
    private final List<Documento> listaDocumentos;
    private final int layoutItemLista;

    public DocumentoArrayAdapter(Context context, int layoutItemLista, List<Documento> listaDocumentos) {
        super(context, layoutItemLista, listaDocumentos);
        this.context = context;
        this.listaDocumentos = listaDocumentos;
        this.layoutItemLista = layoutItemLista;
    }

    //Esse método será chamado para cada linha da ListView e preencherá essa linha conforme a implementação abaixo
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //objeto usado para inserir os dados dentro de um layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //obtendo componentes de uma linha da Lista
        View rowView = inflater.inflate(layoutItemLista, parent, false);
        TextView nomeDocumentoView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView descricaoDocumentoView = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        //define o nome do documento
        nomeDocumentoView.setText(listaDocumentos.get(position).getNome());
        //define a descrição do documento
        descricaoDocumentoView.setText(listaDocumentos.get(position).getTipo());
        //define a ícone para o documento
        imageView.setImageResource(R.mipmap.ic_launcher);

        return rowView;
    }
}
