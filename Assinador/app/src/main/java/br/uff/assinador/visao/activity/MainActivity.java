package br.uff.assinador.visao.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import br.uff.assinador.AndroidApp;
import br.uff.assinador.R;
import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.daoservice.UsuarioDaoService;
import br.uff.assinador.modelo.Documento;
import br.uff.assinador.util.Armazenamento;
import br.uff.assinador.util.Util;
import br.uff.assinador.visao.adapter.DocumentoArrayAdapter;
import br.uff.assinador.visao.listener.MultiSelecaoItensListener;
import br.uff.assinador.visao.presenter.IMainView;
import br.uff.assinador.visao.presenter.MainPresenter;


public class MainActivity extends BaseActivity implements IMainView {

    @Inject
    UsuarioDaoService usuarioDaoService;
    @Inject
    DocumentoDaoService documentoDaoService;

    MainPresenter mainPresenter;
    Menu mainMenu;
    ListView listView;
    DocumentoArrayAdapter listViewAdapter;

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Injeta dependências com a anotação @Inject nesta classe
        mainPresenter = new MainPresenter(this);

        this.getApplicationComponent().inject(this);
        this.getApplicationComponent().inject(mainPresenter);

        //obtém listView
        this.listView = (ListView) findViewById(R.id.listView);

        //preenche listView com documentos
        mainPresenter.preencherListaDocumentos("11232299707");

        //Define evento de clique em um item da lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                //obtém o documento escolhido
                final Documento item = (Documento) parent.getItemAtPosition(position);

                //grava o arquivo temporariamente no storage externo
                Armazenamento.criarArquivo(getExternalFilesDir(null), item.getNome(), item.getArquivo());

                //Obtém URI do arquivo
                Uri uriArquivo = Armazenamento.obterUriArquivo(getExternalFilesDir(null), item.getNome());

                //Visualiza qualquer tipo de arquivo, pois será indicado pelo android um programa para abrir o arquivo
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uriArquivo, item.getTipo());
                startActivity(intent);
            }
        });

        MultiSelecaoItensListener multiSelecaoItensListener = new MultiSelecaoItensListener(this,listViewAdapter);
        this.getApplicationComponent().inject(multiSelecaoItensListener);

        listView.setMultiChoiceModeListener(multiSelecaoItensListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_add_user:
                adicionarUsuario();
                return true;
            case R.id.action_add_doc:
                adicionarDocumento();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void adicionarUsuario() {
        usuarioDaoService.adicionarUsuario("11232299707");
    }

    private void adicionarDocumento() {

        try {
            documentoDaoService.adicionarDocumentoPorUsuario("11232299707");
            mainPresenter.atualizarListaDocumentos("11232299707");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Só foi colocado aqui, pois se a MainActivity for destruída, significa que toda a aplicação foi também.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((AndroidApp) getApplication()).closeDB();
    }

    @Override
    public void setListaDocumentos(List<Documento> listaDocumentos) {

        // define um adapter para esse componente
        // através do adapter é que os documentos serão inseridos no componente ListView
        listViewAdapter = new DocumentoArrayAdapter(this, R.layout.item_doc_lista_layout, listaDocumentos);
        listView.setAdapter(listViewAdapter);
    }

    @Override
    public void atualizarListaDocumentos(List<Documento> listaDocumentos) {

        listViewAdapter.atualizarDados(listaDocumentos);
    }
}
