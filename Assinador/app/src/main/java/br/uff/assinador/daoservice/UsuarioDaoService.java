package br.uff.assinador.daoservice;

import br.uff.assinador.dao.DaoSession;
import br.uff.assinador.dao.UsuarioDao;

/**
 * Created by matheus on 18/08/15.
 */
public class UsuarioDaoService {

    private UsuarioDao usuarioDao;

    public UsuarioDaoService(DaoSession daoSession) {
        usuarioDao = daoSession.getUsuarioDao();
    }


}
