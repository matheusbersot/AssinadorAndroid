package br.uff.assinador.service;

import br.uff.assinador.dao.DaoSession;
import br.uff.assinador.dao.UsuarioDao;

/**
 * Created by matheus on 18/08/15.
 */
public class UsuarioService {

    private UsuarioDao usuarioDao;

    public UsuarioService(DaoSession daoSession) {
        usuarioDao = daoSession.getUsuarioDao();
    }


}
