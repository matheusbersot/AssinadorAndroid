package br.uff.assinador.daoservice;

import javax.inject.Inject;

import br.uff.assinador.dao.DaoSession;
import br.uff.assinador.dao.DocumentoDao;

/**
 * Created by matheus on 18/08/15.
 */
public class DocumentoDaoService {

    private final DocumentoDao documentoDao;

    @Inject
    public DocumentoDaoService(DocumentoDao documentoDao) {
        this.documentoDao = documentoDao;
    }
}
