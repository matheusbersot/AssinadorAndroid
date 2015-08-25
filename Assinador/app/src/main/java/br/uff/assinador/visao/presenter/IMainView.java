package br.uff.assinador.visao.presenter;

import android.widget.Adapter;
import android.widget.ListAdapter;

import java.util.List;

import br.uff.assinador.modelo.Documento;

/**
 * Created by matheus on 25/08/15.
 */
public interface IMainView {

    public void setListaDocumentos(List<Documento> listaDocumentos);
}
