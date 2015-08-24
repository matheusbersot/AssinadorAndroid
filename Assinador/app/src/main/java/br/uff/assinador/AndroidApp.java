package br.uff.assinador;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import br.uff.assinador.asyncTask.CreateDatabaseTask;
import br.uff.assinador.dao.DaoMaster;
import br.uff.assinador.di.component.ApplicationComponent;
import br.uff.assinador.di.component.DaggerApplicationComponent;
import br.uff.assinador.di.module.ApplicationModule;
import br.uff.assinador.visao.activity.MainActivity;

/**
 * Created by matheus on 19/08/15.
 */
public class AndroidApp extends Application {

    private ApplicationComponent applicationComponent;
    private CreateDatabaseTask databaseTask;
    private SQLiteDatabase db;
    private static final String TAG = MainActivity.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        this.initializeInjector();
    }

    private void initializeInjector() {

        //criação do banco de dados em uma tarefa assíncrona
        //recomendação do tutorial de desenvolvimento Android
        // (http://developer.android.com/training/basics/data-storage/databases.html)
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "documentos-db", null);

        databaseTask = new CreateDatabaseTask();
        DaoMaster.DevOpenHelper[] params = new DaoMaster.DevOpenHelper[1];
        params[0] = helper;
        databaseTask.execute(params);

        try {
            //Espera se necessário para a computação completar e então retorna seu resultado.
            db = databaseTask.get();
            Log.i(TAG, "Criação do Banco de Dados: " + helper.getDatabaseName());
            Log.i(TAG, "Caminho do Banco de Dados: " + db.getPath());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(db))
                .build();

        this.onTerminate();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

    public void closeDB()
    {
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
    }
}
