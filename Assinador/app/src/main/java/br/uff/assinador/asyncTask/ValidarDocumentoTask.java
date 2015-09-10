package br.uff.assinador.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.SignerInformation;
import org.spongycastle.cms.SignerInformationStore;
import org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.spongycastle.util.Store;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.uff.assinador.modelo.Documento;
import br.uff.assinador.util.Constantes;
import br.uff.assinador.util.certificado.CRLLocator;
import br.uff.assinador.visao.adapter.DocumentoArrayAdapter;

/**
 * Created by matheus on 01/09/15.
 */
public class ValidarDocumentoTask extends AsyncTask<Void, Integer, Resultado<Boolean>> {


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
    protected Resultado<Boolean> doInBackground(Void... params) {

        Resultado<Boolean> resultado = new Resultado<Boolean>();
        boolean valido = true;

        try {

            List<Documento> listaDocumentoSelecionados = listViewAdapter.obterListaDocumentosSelecionados();

            for (int i = 0; i < listaDocumentoSelecionados.size(); ++i) {

                final Documento doc = listaDocumentoSelecionados.get(i);

                byte[] digest = MessageDigest.getInstance(Constantes.ALG_HASH_SHA1).digest(doc.getArquivo());
                Map<String, byte[]> map = new HashMap<String, byte[]>();
                map.put(Constantes.OID_ALG_HASH_SHA1, digest);
                CMSSignedData signedData = new CMSSignedData(map, doc.getAssinatura());

                Store certStore = signedData.getCertificates();

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

                    //validar assinatura para o documento corrente
                    if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().
                            setProvider(Constantes.BOUNCY_CASTLE_PROVIDER).build(certFromSignedData))) {
                        valido = valido && true;
                    } else {
                        valido = valido && false;
                    }

                    //validar a cadeia de certificados
                    //nesse processo, já se verifica se o certificado não foi revogado
                    valido = valido && verifyCertificateChain(certFromSignedData);

                    // verifica se o certificado do usuário foi revogado
                    // note que não é feita verificação na cadeia de certificados,
                    // pois algumas ACS não possuem extensão CRL
                    valido = valido && verifyCertificateInCRL(certFromSignedData);
                }
            }
            resultado.set(valido);

        } catch (Exception e) {
            resultado.setException(e);
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    private boolean verifyCertificateInCRL(X509Certificate cert)
            throws CRLException {

        boolean isValid = true;

        CRLLocator crlLocator = new CRLLocator(cert);
        List<X509CRL> x509CRLList = crlLocator.getCRL();
        for(X509CRL x509CRL: x509CRLList){
            X509CRLEntry x509CRLEntry = x509CRL.getRevokedCertificate(cert);
            if (x509CRLEntry != null) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    private boolean verifyCertificateChain(X509Certificate cert)
            throws CertificateException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, NoSuchProviderException,
            KeyStoreException, IOException, CertPathValidatorException {

        boolean isValid = false;

        CertificateFactory cf = CertificateFactory.getInstance(Constantes.X509_CERT_TYPE, Constantes.BOUNCY_CASTLE_PROVIDER);

        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(cert);
        CertPath certPath = cf.generateCertPath(certList);

        KeyStore keystore = KeyStore.getInstance(Constantes.ANDROID_CA_STORE);
        keystore.load(null);
        PKIXParameters params = new PKIXParameters(keystore);
        params.setRevocationEnabled(false);

        CertPathValidator certPathValidator =
                CertPathValidator.getInstance(CertPathValidator.getDefaultType(), Constantes.BOUNCY_CASTLE_PROVIDER);


        PKIXCertPathValidatorResult pkixCertPathValidatorResult =
                (PKIXCertPathValidatorResult) certPathValidator.validate(certPath, params);
        PublicKey subjectPublicKey = pkixCertPathValidatorResult.getPublicKey();
        isValid = true;

        return isValid;
    }
}
