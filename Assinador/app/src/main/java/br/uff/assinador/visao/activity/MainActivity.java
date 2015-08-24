package br.uff.assinador.visao.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import javax.inject.Inject;

import br.uff.assinador.AndroidApp;
import br.uff.assinador.R;
import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.daoservice.UsuarioDaoService;


public class MainActivity extends BaseActivity {

    @Inject
    UsuarioDaoService usuarioDaoService;
    @Inject
    DocumentoDaoService documentoDaoService;

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Injeta dependências com a anotação @Inject nesta classe
        this.getApplicationComponent().inject(this);

        //Preenche listView com documentos
        final ListView listview = (ListView) findViewById(R.id.listView);

        listview.setAdapter();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Só foi colocado aqui, pois se a MainActivity for destruída, significa que toda a aplicação foi também.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((AndroidApp)getApplication()).closeDB();
    }
}
