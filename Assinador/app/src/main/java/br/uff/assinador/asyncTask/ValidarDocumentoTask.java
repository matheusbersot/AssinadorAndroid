package br.uff.assinador.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.SignerInformation;
import org.spongycastle.cms.SignerInformationStore;
import org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.spongycastle.util.Store;

import java.security.MessageDigest;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.uff.assinador.modelo.Documento;
import br.uff.assinador.util.Constantes;
import br.uff.assinador.util.Util;
import br.uff.assinador.visao.adapter.DocumentoArrayAdapter;

/**
 * Created by matheus on 01/09/15.
 */
public class ValidarDocumentoTask extends AsyncTask<Void, Integer, Boolean> {


    static {
        //verifica se o provider já se encontra no sistema
        //se não encontrar, insere o provider no sistema
        if (Security.getProvider(Constantes.BOUNCY_CASTLE_PROVIDER) == null) {
            Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        }
    }

    private Activity activity;
    DocumentoArrayAdapter listViewAdapter;
    private final String TAG = ValidarDocumentoTask.class.getSimpleName();

    public ValidarDocumentoTask(Activity activity, DocumentoArrayAdapter listViewAdapter) {
        this.activity = activity;
        this.listViewAdapter = listViewAdapter;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        //verificando assinatura
        boolean valido = true;

        try {

            List<Documento> listaDocumentoSelecionados = listViewAdapter.obterListaDocumentosSelecionados();
            String mensagemErro = "As assinaturas dos seguintes documentos não são válidas:\\n";

            for (int i = 0; i < listaDocumentoSelecionados.size(); ++i) {

                final Documento doc = listaDocumentoSelecionados.get(i);

                byte[] digest = MessageDigest.getInstance(Constantes.ALG_HASH_SHA1).digest(doc.getArquivo());
                Map<String, byte[]> map = new HashMap<String, byte[]>();
                map.put(Constantes.OID_ALG_HASH_SHA1, digest);
                CMSSignedData signedData = new CMSSignedData(map, doc.getAssinatura());

                Store certStore = signedData.getCertificates();
                Store crlStore = signedData.getCRLs();

                SignerInformationStore signerStore = signedData.getSignerInfos();
                Collection c = signerStore.getSigners();

                Iterator it = c.iterator();
                while (it.hasNext()) {
                    SignerInformation signer = (SignerInformation) it.next();
                    Collection certCollection = certStore.getMatches(signer.getSID());

                    Iterator certIt = certCollection.iterator();
                    X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();

                    X509Certificate certFromSignedData = new JcaX509CertificateConverter().
                            setProvider(Constantes.BOUNCY_CASTLE_PROVIDER).getCertificate(certHolder);

                    if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().
                            setProvider(Constantes.BOUNCY_CASTLE_PROVIDER).build(certFromSignedData))) {
                        valido = valido && true;
                    } else {
                        valido = valido && false;
                        mensagemErro += "- "+ doc.getNome() + "\\n";
                    }
                }


                //TODO: validar certificado para ver se não está expirado (CRL)
                //TODO: validar cadeia do certificado
                //TODO: verificar se o CPF do certificado é do mesmo usuário que assinou

                 /*   X509Certificate[] cadeiaTotal = montarCadeiaOrdenadaECompleta(certCollection);

                    final X509ChainValidator cadeia = new X509ChainValidator(
                            cadeiaTotal, /* trustedAnchors /new HashSet(
                            FachadaDeCertificadosAC.getTrustAnchors()), null);
                    cadeia.checkCRL(verificarLCRs);
                    cadeia.validateChain(dtAssinatura);

                    String s2 = cert.getSubjectDN().getName();
                    s2 = obterNomeExibicao(s2);
                    if (sCN.length() != 0)
                        sCN += ", ";
                    sCN += s2;

                return sCN.length() == 0 ? null : sCN;*/
            }

            if(valido){
                Util.showToastOnUIThread(activity,"Todas as assinaturas dos documentos selecionados são válidas.",false);
            }else{
                Util.showToastOnUIThread(activity,mensagemErro,true);
            }
            return valido;
        }catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
            Util.showToastOnUIThread(activity, "Ocorreu um erro no processo de validação!", false);
            return false;
        }
    }
}
