package br.uff.assinador;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import br.uff.assinador.dao.DaoMaster;
import br.uff.assinador.dao.DaoSession;
import br.uff.assinador.dao.DocumentoDao;
import br.uff.assinador.dao.UsuarioDao;
import br.uff.assinador.service.DocumentoService;
import br.uff.assinador.service.UsuarioService;


public class MainActivity extends ActionBarActivity {

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private UsuarioService usuarioService;
    private DocumentoService documentoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //criação do banco de dados
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "documentos-db", null);
        db = helper.getWritableDatabase();

        //criação dos DAOs e Services
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        usuarioService = new UsuarioService(daoSession);
        documentoService = new DocumentoService(daoSession);
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
}
