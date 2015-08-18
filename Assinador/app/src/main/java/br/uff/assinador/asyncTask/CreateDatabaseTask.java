package br.uff.assinador.asyncTask;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import br.uff.assinador.dao.DaoMaster;

/**
 * Created by matheus on 18/08/15.
 */
public class CreateDatabaseTask extends AsyncTask<DaoMaster.DevOpenHelper, Integer, SQLiteDatabase> {

    @Override
    protected SQLiteDatabase doInBackground(DaoMaster.DevOpenHelper... params) {

        DaoMaster.DevOpenHelper helper = params[0];
        return helper.getWritableDatabase();
    }
}
