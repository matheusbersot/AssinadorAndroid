package br.uff.assinador.daoservice;

import javax.inject.Inject;

import br.uff.assinador.dao.DaoSession;
import br.uff.assinador.dao.UsuarioDao;

/**
 * Created by matheus on 18/08/15.
 */
public class UsuarioDaoService {

    private final UsuarioDao usuarioDao;

    @Inject
    public UsuarioDaoService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }


}
