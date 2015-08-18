package br.uff.assinador.service;

import br.uff.assinador.dao.DaoSession;
import br.uff.assinador.dao.DocumentoDao;

/**
 * Created by matheus on 18/08/15.
 */
public class DocumentoService {

    private DocumentoDao documentoDao;

    public DocumentoService(DaoSession daoSession) {
        documentoDao = daoSession.getDocumentoDao();
    }
}
