package br.uff.assinador.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import br.uff.assinador.modelo.Usuario;
import br.uff.assinador.modelo.Documento;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig usuarioDaoConfig;
    private final DaoConfig documentoDaoConfig;

    private final UsuarioDao usuarioDao;
    private final DocumentoDao documentoDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        usuarioDaoConfig = daoConfigMap.get(UsuarioDao.class).clone();
        usuarioDaoConfig.initIdentityScope(type);

        documentoDaoConfig = daoConfigMap.get(DocumentoDao.class).clone();
        documentoDaoConfig.initIdentityScope(type);

        usuarioDao = new UsuarioDao(usuarioDaoConfig, this);
        documentoDao = new DocumentoDao(documentoDaoConfig, this);

        registerDao(Usuario.class, usuarioDao);
        registerDao(Documento.class, documentoDao);
    }
    
    public void clear() {
        usuarioDaoConfig.getIdentityScope().clear();
        documentoDaoConfig.getIdentityScope().clear();
    }

    public UsuarioDao getUsuarioDao() {
        return usuarioDao;
    }

    public DocumentoDao getDocumentoDao() {
        return documentoDao;
    }

}
