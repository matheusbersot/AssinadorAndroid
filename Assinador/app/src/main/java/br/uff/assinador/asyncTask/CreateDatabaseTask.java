package br.uff.assinador.asyncTask;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import br.uff.assinador.dao.DaoMaster;

/**
 * Created by matheus on 18/08/15.
 */
public class CreateDatabaseTask extends AsyncTask<DaoMaster.DevOpenHelper, Integer, Resultado> {

    private final String TAG = CreateDatabaseTask.class.getSimpleName();


    @Override
    protected Resultado doInBackground(DaoMaster.DevOpenHelper... params) {

        DaoMaster.DevOpenHelper helper = params[0];
        Resultado<SQLiteDatabase> resultado = new Resultado<SQLiteDatabase>();

        try{
            resultado.set(helper.getWritableDatabase());
        }
        catch (SQLiteException e)
        {
            resultado.setException(e);
            Log.e(TAG, e.getMessage());
        }

        return resultado;
    }
}
