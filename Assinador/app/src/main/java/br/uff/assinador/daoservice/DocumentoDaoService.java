package br.uff.assinador.daoservice;

import android.util.Log;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.uff.assinador.dao.DocumentoDao;
import br.uff.assinador.modelo.Documento;
import br.uff.assinador.modelo.Usuario;
import br.uff.assinador.util.Armazenamento;
import br.uff.assinador.util.Util;

/**
 * Created by matheus on 18/08/15.
 */
public class DocumentoDaoService {

    private UsuarioDaoService usuarioDaoService;
    private DocumentoDao documentoDao;

    @Inject
    public DocumentoDaoService(DocumentoDao documentoDao, UsuarioDaoService usuarioDaoService) {
        this.documentoDao = documentoDao;
        this.usuarioDaoService = usuarioDaoService;
    }

    public List<Documento> obterDocumentosPorUsuario(String identificadorUsuario) throws Exception {
        Usuario usuario = usuarioDaoService.obterUsuarioPorIdentificador(identificadorUsuario);
        return documentoDao._queryUsuario_Documentos(usuario.getId());
    }

    public void adicionarDocumentoPorUsuario(String identificadorUsuario) throws Exception {
        Usuario usuario = usuarioDaoService.obterUsuarioPorIdentificador(identificadorUsuario);
        byte[] dadosDocumento = Armazenamento.obterArquivo("teste2.doc");

        Documento doc = new Documento(null, "teste2.doc", "application/msword", "teste2 doc", dadosDocumento, null, null,
                new Date(), Long.decode("1"));
        documentoDao.insert(doc);
        Log.d("DocumentoDaoService", "Inseriu novo documento, ID: " + doc.getId());
    }

    public void salvarDocumentos(List<Documento> listaDocumentos)
    {
        documentoDao.insertOrReplaceInTx(listaDocumentos);
    }

    public void removerDocumento(Documento documento)
    {
        documentoDao.delete(documento);
    }
}
