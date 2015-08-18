package br.uff.assinador;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.ExecutionException;

import br.uff.assinador.asyncTask.CreateDatabaseTask;
import br.uff.assinador.dao.DaoMaster;
import br.uff.assinador.dao.DaoSession;
import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.daoservice.UsuarioDaoService;


public class MainActivity extends ActionBarActivity {

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private UsuarioDaoService usuarioDaoService;
    private DocumentoDaoService documentoDaoService;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //criação do banco de dados em um tarefa assíncrona
        //recomendação do tutorial de desenvolvimento Android
        // (http://developer.android.com/training/basics/data-storage/databases.html)
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "documentos-db", null);

        CreateDatabaseTask databaseTask = new CreateDatabaseTask();
        DaoMaster.DevOpenHelper[] params = new DaoMaster.DevOpenHelper[1];
        params[0] = helper;
        databaseTask.execute(params);

        try {
            //Espera se necessário para a computação completar e então retorna seu resultado.
            db = databaseTask.get();
            Log.i(TAG, "Criação do Banco de Dados: " + helper.getDatabaseName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //criação dos gerenciadores de DAOs
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        //criação de Services
        usuarioDaoService = new UsuarioDaoService(daoSession);
        documentoDaoService = new DocumentoDaoService(daoSession);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //fechar instância do banco antes de destruir o app
        db.close();
    }
}
