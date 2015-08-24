package br.uff.assinador.di.module;

import android.database.sqlite.SQLiteDatabase;

import javax.inject.Singleton;

import br.uff.assinador.dao.DaoMaster;
import br.uff.assinador.dao.DaoSession;
import br.uff.assinador.dao.DocumentoDao;
import br.uff.assinador.dao.UsuarioDao;
import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.daoservice.UsuarioDaoService;
import dagger.Module;
import dagger.Provides;

/**
 * Created by matheus on 19/08/15.
 */
@Module
public class ApplicationModule {

    private final SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    public ApplicationModule(SQLiteDatabase db) {
        this.db = db;
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    @Provides
    UsuarioDao provideUsuarioDao() {
        return daoSession.getUsuarioDao();
    }

    @Provides
    DocumentoDao provideDocumentoDao() {
        return daoSession.getDocumentoDao();
    }

    @Provides @Singleton
    UsuarioDaoService provideUsuarioDaoService(UsuarioDao usuarioDao) {
        return new UsuarioDaoService(usuarioDao);
    }

    @Provides @Singleton
    DocumentoDaoService provideDocumentoDaoService(DocumentoDao documentoDao, UsuarioDaoService usuarioDaoService) {
        return new DocumentoDaoService(documentoDao, usuarioDaoService);
    }
}
