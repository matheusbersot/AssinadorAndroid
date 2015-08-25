package br.uff.assinador.visao.presenter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import br.uff.assinador.R;
import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.daoservice.UsuarioDaoService;
import br.uff.assinador.modelo.Documento;
import br.uff.assinador.util.Util;
import br.uff.assinador.visao.adapter.DocumentoArrayAdapter;

/**
 * Created by matheus on 25/08/15.
 */
public class MainPresenter {

    private IMainView mainView;

    @Inject
    protected UsuarioDaoService usuarioDaoService;
    @Inject
    protected DocumentoDaoService documentoDaoService;

    public MainPresenter(IMainView view) {
        mainView = view;
    }

    public void preencherListaDocumentos(String identificadorUsuario)
    {
        List<Documento> listaDocumentos = null;
        try {
            listaDocumentos = documentoDaoService.obterDocumentosPorUsuario(identificadorUsuario);
        } catch (Exception e) {
            usuarioDaoService.adicionarUsuario("11232299707");
        }
        mainView.setListaDocumentos(listaDocumentos);
    }
}
