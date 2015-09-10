package br.uff.assinador.visao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.uff.assinador.R;
import br.uff.assinador.modelo.Documento;

/**
 * Created by matheus on 21/08/15.
 */
public class DocumentoArrayAdapter extends ArrayAdapter<Documento> {

    private final Context mContext;
    private final List<Documento> mListaDocumentos;
    private final int mLayoutItemLista;
    private SparseBooleanArray mIdsItensSelecionados;


    public DocumentoArrayAdapter(Context context, int layoutItemLista, List<Documento> listaDocumentos) {
        super(context, layoutItemLista, listaDocumentos);
        this.mContext = context;
        this.mListaDocumentos = listaDocumentos;
        this.mLayoutItemLista = layoutItemLista;
        mIdsItensSelecionados = new SparseBooleanArray();
    }

    //Esse método será chamado para cada linha da ListView e preencherá essa linha conforme a implementação abaixo
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //objeto usado para inserir os dados dentro de um layout
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //obtendo componentes de uma linha da Lista
        View rowView = inflater.inflate(mLayoutItemLista, parent, false);
        TextView nomeDocumentoView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView descricaoDocumentoView = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);


        Documento doc = mListaDocumentos.get(position);
        //define o nome do documento
        nomeDocumentoView.setText(doc.getNome());
        //define a descrição do documento
        descricaoDocumentoView.setText(doc.getDescricao());
        //define a ícone para o documento
        imageView.setImageResource(R.mipmap.ic_launcher);

        //muda a cor de fundo do item para indicar que já foi assinado
        if(doc.getAssinatura() != null){
            rowView.setBackgroundColor(Color.GREEN);
        }

        return rowView;
    }

    @Override
    public void remove(Documento doc) {
        mListaDocumentos.remove(doc);
        notifyDataSetChanged();
    }

    public void trocarSelecao(int posicao) {
        selecionarView(posicao, !mIdsItensSelecionados.get(posicao));
    }

    public void removerSelecao() {
        mIdsItensSelecionados = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selecionarView(int posicao, boolean valor) {
        if (valor)
            mIdsItensSelecionados.put(posicao, valor);
        else
            mIdsItensSelecionados.delete(posicao);
        notifyDataSetChanged();
    }

    public int obterNumeroItensSelecionados() {
        return mIdsItensSelecionados.size();
    }

    public SparseBooleanArray obterIdsSelecionados() {
        return mIdsItensSelecionados;
    }

    public boolean itensSelecionadosEstaoAssinados() {
        int tamanho = mIdsItensSelecionados.size();
        for (int i = 0; i < tamanho; ++i) {
            int posicao = mIdsItensSelecionados.keyAt(i);
            Documento doc = mListaDocumentos.get(posicao);
            if (doc.getAssinatura() == null) {
                return false;
            }
        }
        return true;
    }

    public boolean itensSelecionadosNaoEstaoAssinados() {
        int tamanho = mIdsItensSelecionados.size();
        for (int i = 0; i < tamanho; ++i) {
            int posicao = mIdsItensSelecionados.keyAt(i);
            Documento doc = mListaDocumentos.get(posicao);
            if (doc.getAssinatura() != null) {
                return false;
            }
        }
        return true;
    }

    public List<Documento> obterListaDocumentosSelecionados()
    {
        List<Documento> lista = new ArrayList<Documento>();
        int tamanho = mIdsItensSelecionados.size();
        for (int i = 0; i < tamanho; ++i) {
            int posicao = mIdsItensSelecionados.keyAt(i);
            lista.add(mListaDocumentos.get(posicao));
        }
        return lista;
    }

    public void atualizarDados(List<Documento> listaDocumentos)
    {
        removerSelecao();
        clear(); // remove todos os elementos da lista, no caso mListaDocumentos
        addAll(listaDocumentos);
        notifyDataSetChanged();
    }

    public void removerAssinaturasDocumentosSelecionados()
    {
        List<Documento> listaDocumentos = this.obterListaDocumentosSelecionados();
        int tamanho = listaDocumentos.size();
        for (int i = 0; i < tamanho; ++i) {
            listaDocumentos.get(i).setAssinatura(null);
            listaDocumentos.get(i).setTipo_assinatura(null);
        }
    }
}
