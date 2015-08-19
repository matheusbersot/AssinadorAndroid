package br.uff.assinador;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.daoservice.UsuarioDaoService;


public class MainActivity extends ActionBarActivity {

    protected @Inject UsuarioDaoService usuarioDaoService;
    protected @Inject DocumentoDaoService documentoDaoService;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    /*@Override
    protected void onDestroy() {
        super.onDestroy();

        //destruir tarefa assíncrona
        if (databaseTask != null) {
            databaseTask.cancel(true);
            databaseTask = null;
        }

        //fechar instância do banco antes de destruir o app
        if(db != null)
        {
            db.close();
            db = null;
        }
    }*/
}
