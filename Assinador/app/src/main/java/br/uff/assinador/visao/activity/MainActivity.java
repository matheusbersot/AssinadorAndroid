package br.uff.assinador.visao.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import br.uff.assinador.AndroidApp;
import br.uff.assinador.R;
import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.daoservice.UsuarioDaoService;
import br.uff.assinador.modelo.Documento;
import br.uff.assinador.util.Util;
import br.uff.assinador.visao.adapter.DocumentoArrayAdapter;
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
        this.getApplicationComponent().inject(this);
        mainPresenter = new MainPresenter(this);
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
                Util.Armazenamento.criarArquivo(getExternalFilesDir(null), item.getNome(), item.getArquivo());

                //Obtém URI do arquivo
                Uri uriArquivo = Util.Armazenamento.obterUriArquivo(getExternalFilesDir(null), item.getNome());

                //Visualiza qualquer tipo de arquivo, pois será indicado pelo android um programa para abrir o arquivo
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uriArquivo, item.getTipo());
                startActivity(intent);
            }
        });


        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private Menu menuItensSelecionados;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                // captura o número total de itens selecionados
                final int checkedCount = listView.getCheckedItemCount();

                // Define o título da contextual action bar(CAB) de acordo com o número de itens selecionados
                if (checkedCount == 1) {
                    mode.setTitle(checkedCount + " Selecionado");
                } else if (checkedCount > 1) {
                    mode.setTitle(checkedCount + " Selecionados");
                }

                //marcar seleção de um documento no adapter
                listViewAdapter.trocarSelecao(position);

                //modificar actionbar de acordo com o status do documento selecionado (assinado ou não)
                definirBotoesActionBar();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_delete:

                        /*SparseBooleanArray idsSelecionados = listViewAdapter.obterIdsSelecionados();

                        for (int i = 0; i < idsSelecionados.size(); ++i) {

                            if (idsSelecionados.valueAt(i)) {

                                Documento selecteditem = listViewAdapter.getItem(idsSelecionados.keyAt(i));

                                // Remove itens selecionados com os seguintes ids
                                listViewAdapter.remove(selecteditem);
                            }
                        }
                        // Close CAB
                        mode.finish();*/
                        return true;
                    case R.id.action_sign:
                        return true;
                    case R.id.action_validate:
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

            public void definirBotoesActionBar()
            {
                if(listViewAdapter.itensSelecionadosEstaoAssinados()) // mostrar botão de validar
                {
                    MenuItem btnValidar = this.menuItensSelecionados.findItem(R.id.action_validate);
                    btnValidar.setVisible(true);
                }
                else if (listViewAdapter.itensSelecionadosNaoEstaoAssinados())// mostrar botão de assinatura
                {
                    MenuItem btnAssinar = this.menuItensSelecionados.findItem(R.id.action_sign);
                    btnAssinar.setVisible(true);
                }
            }
        });
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
        this.listViewAdapter = new DocumentoArrayAdapter(this, R.layout.item_doc_lista_layout, listaDocumentos);
        listView.setAdapter(listViewAdapter);
    }
}
