package br.uff.assinador.daoservice;

import br.uff.assinador.dao.DaoSession;
import br.uff.assinador.dao.DocumentoDao;

/**
 * Created by matheus on 18/08/15.
 */
public class DocumentoDaoService {

    private DocumentoDao documentoDao;

    public DocumentoDaoService(DaoSession daoSession) {
        documentoDao = daoSession.getDocumentoDao();
    }
}
