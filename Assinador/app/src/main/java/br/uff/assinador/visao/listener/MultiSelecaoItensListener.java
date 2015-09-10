package br.uff.assinador.visao.listener;

import android.app.Activity;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import br.uff.assinador.R;
import br.uff.assinador.asyncTask.AssinarDocumentoTask;
import br.uff.assinador.asyncTask.Resultado;
import br.uff.assinador.asyncTask.ValidarDocumentoTask;
import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.modelo.Documento;
import br.uff.assinador.util.Constantes;
import br.uff.assinador.util.Util;
import br.uff.assinador.visao.adapter.DocumentoArrayAdapter;

/**
 * Created by matheus on 27/08/15.
 */
public class MultiSelecaoItensListener implements AbsListView.MultiChoiceModeListener {

    private Menu menuItensSelecionados;
    private DocumentoArrayAdapter listViewAdapter;
    private Activity parentActivity;
    private final String TAG = MultiSelecaoItensListener.class.getSimpleName();

    @Inject
    DocumentoDaoService documentoDaoService;

    public MultiSelecaoItensListener(Activity parentActivity, DocumentoArrayAdapter listViewAdapter) {
        this.listViewAdapter = listViewAdapter;
        this.parentActivity = parentActivity;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        // captura o número total de itens selecionados
        final int qtdItensSelecionados = listViewAdapter.obterNumeroItensSelecionados();

        // Define o título da contextual action bar(CAB) de acordo com o número de itens selecionados
        if (qtdItensSelecionados == 1) {
            mode.setTitle(qtdItensSelecionados + " Selecionado");
        } else if (qtdItensSelecionados > 1) {
            mode.setTitle(qtdItensSelecionados + " Selecionados");
        }

        //marcar seleção de um documento no adapter
        listViewAdapter.trocarSelecao(position);

        //modificar actionbar de acordo com o status do documento selecionado (assinado ou não)
        definirBotoesActionBar();
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_delete:
                deletarDocumentos();
                mode.finish(); // Fecha o CAB (contextual action bar)
                return true;
            case R.id.action_sign:
                Promise p = executarProcessoAssinaturaDigital();
                //fecha o CAB independente de ter tido erro ou não
                p.always(new AlwaysCallback() {
                    @Override
                    public void onAlways(Promise.State state, Object resolved, Object rejected) {
                        Util.closeContextualActionBar(parentActivity, mode);
                    }
                });
                return true;
            case R.id.action_validate:
                validarDocumentos();
                mode.finish(); // Fecha o CAB (contextual action bar)
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        this.menuItensSelecionados = menu;
        mode.getMenuInflater().inflate(R.menu.menu_itens_selecionados, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

        listViewAdapter.removerSelecao();
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    private void definirBotoesActionBar() {
        if (listViewAdapter.itensSelecionadosEstaoAssinados()) // mostrar botão de validar
        {
            MenuItem btnValidar = this.menuItensSelecionados.findItem(R.id.action_validate);
            btnValidar.setVisible(true);
        } else if (listViewAdapter.itensSelecionadosNaoEstaoAssinados())// mostrar botão de assinatura
        {
            MenuItem btnAssinar = this.menuItensSelecionados.findItem(R.id.action_sign);
            btnAssinar.setVisible(true);
        } else {
            MenuItem btnValidar = this.menuItensSelecionados.findItem(R.id.action_validate);
            btnValidar.setVisible(false);
            MenuItem btnAssinar = this.menuItensSelecionados.findItem(R.id.action_sign);
            btnAssinar.setVisible(false);
        }

    }

    private void deletarDocumentos() {
        List<Documento> listaDocumentos = listViewAdapter.obterListaDocumentosSelecionados();

        for (int i = 0; i < listaDocumentos.size(); ++i) {

            Documento doc = listaDocumentos.get(i);

            // Remove itens selecionados com os seguintes ids
            listViewAdapter.remove(doc);
            documentoDaoService.removerDocumento(doc);
        }
    }

    private Promise executarProcessoAssinaturaDigital() {

        final Deferred deferred = new DeferredObject();

        KeyChain.choosePrivateKeyAlias(parentActivity,

                new KeyChainAliasCallback() {

                    public void alias(String alias) {
                        // Credential alias selected.  Remember the alias selection for future use.
                        if (alias != null) {
                            Object[] params = new Object[2];
                            params[0] = parentActivity;
                            params[1] = alias;

                            String mensagem = "";

                            //assina documentos
                            boolean assinouTudo = assinarDocumentos(params);

                            //valida assinatura e cadeia de certificados
                            boolean validouTudo = validarDocumentos();

                            if(assinouTudo && validouTudo)
                            {
                                //TODO: pensar uma forma atômica de fazer isso

                                //persistir assinatura no banco de dados
                                List<Documento> listaDocumentoSelecionados = listViewAdapter.obterListaDocumentosSelecionados();
                                documentoDaoService.salvarDocumentos(listaDocumentoSelecionados);

                                //TODO: enviar para servidor

                                if (listaDocumentoSelecionados.size() == 1)
                                    mensagem = "O documento foi assinado com sucesso.";
                                else
                                    mensagem = "Os documentos foram assinados com sucesso.";

                                Util.showToastOnUIThread(parentActivity, mensagem , false);

                                deferred.resolve(assinouTudo && validouTudo);
                            }
                            else
                            {
                                listViewAdapter.removerAssinaturasDocumentosSelecionados();

                                if (listViewAdapter.obterNumeroItensSelecionados() == 1)
                                    mensagem = "Ocorreu um erro no processo de assinatura de documento.";
                                else
                                    mensagem = "Ocorreu um erro no processo de assinatura dos documentos.";

                                Util.showErrorAlertDialogOnUiThread(parentActivity, mensagem);

                                deferred.reject(assinouTudo && validouTudo);
                            }
                        }
                    }
                },
                new String[]{Constantes.TIPO_CHAVE_RSA, Constantes.TIPO_CHAVE_DSA}, // List of acceptable key types. null for any
                Util.Certificado.obterListaEmissoresPermitidos(),                       // issuer, null for any
                null,                       // host name of server requesting the cert, null if unavailable
                -1,                         // port of server requesting the cert, -1 if unavailable
                null);

        return deferred.promise();
    }

    //Executa tarefa assíncrona de assinatura
    private boolean assinarDocumentos(Object[] params)
    {
        AssinarDocumentoTask assinarDocumentoTask = new AssinarDocumentoTask(parentActivity, listViewAdapter);
        assinarDocumentoTask.execute(params);
        try {
            return assinarDocumentoTask.get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    //Executa tarefa assíncrona de validação da assinatura e da cadeia de certificados
    private boolean validarDocumentos() {
        ValidarDocumentoTask validarDocumentoTask = new ValidarDocumentoTask(parentActivity, listViewAdapter);
        validarDocumentoTask.execute();

        Resultado<Boolean> resultado;
        try{
            resultado = validarDocumentoTask.get();
            if(resultado.getException()!= null){
                throw resultado.getException();
            }
            else{
              return resultado.get();
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

}
