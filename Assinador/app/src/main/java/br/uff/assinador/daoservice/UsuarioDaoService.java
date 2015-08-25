package br.uff.assinador.daoservice;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import br.uff.assinador.dao.UsuarioDao;
import br.uff.assinador.modelo.Usuario;
import br.uff.assinador.util.Util;

/**
 * Created by matheus on 18/08/15.
 */
public class UsuarioDaoService {

    private UsuarioDao usuarioDao;

    @Inject
    public UsuarioDaoService (UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public Usuario obterUsuarioPorIdentificador(String identificadorUsuario) throws Exception {
        List<Usuario> lista = usuarioDao.queryBuilder().where(UsuarioDao.Properties.Cpf.eq(identificadorUsuario)).list();
        if(lista.isEmpty()) {
            throw new Exception(Util.Constantes.MSG_USUARIO_NAO_ENCONTRADO);
        }

        return lista.get(0);
    }

    public void adicionarUsuario(String identificadorUsuario){
        Usuario usuario = new Usuario(null, identificadorUsuario);
        usuarioDao.insert(usuario);
        Log.d("UsuarioDaoService", "Inseriu novo usuário, ID: " + usuario.getId());
    }
}
