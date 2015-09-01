package br.uff.assinador.asyncTask;

/**
 * Created by matheus on 27/08/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.security.KeyChain;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;

import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.cms.CMSProcessableByteArray;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.CMSSignedDataGenerator;
import org.spongycastle.cms.CMSTypedData;
import org.spongycastle.cms.SignerInformation;
import org.spongycastle.cms.SignerInformationStore;
import org.spongycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;
import org.spongycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.spongycastle.util.Store;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.uff.assinador.modelo.Documento;
import br.uff.assinador.util.Constantes;
import br.uff.assinador.util.Util;
import br.uff.assinador.visao.adapter.DocumentoArrayAdapter;


public class AssinarDocumentoTask extends AsyncTask<Object, Integer, Boolean> {

    static {
        //verifica se o provider já se encontra no sistema
        //se não encontrar, insere o provider no sistema
        if (Security.getProvider(Constantes.BOUNCY_CASTLE_PROVIDER) == null) {
            Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        }
    }

    private Activity activity;
    DocumentoArrayAdapter listViewAdapter;
    //private ProgressDialog progressDialog;
    private Exception error;
    private final String TAG = AssinarDocumentoTask.class.getSimpleName();

    public AssinarDocumentoTask(Activity activity, DocumentoArrayAdapter listViewAdapter) {
        this.activity = activity;
        this.listViewAdapter = listViewAdapter;
        //this.progressDialog = new ProgressDialog(activity);
    }

    /*protected void onPreExecute() {

        progressDialog.setTitle("Assinatura de Documentos");
        progressDialog.setMessage("Assinando documentos...");
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(listViewAdapter.obterNumeroItensSelecionados());
        progressDialog.show();
    }*/

    @Override
    protected Boolean doInBackground(Object... params) {

        try {
            //obtendo parâmetros
            Context ctx = (Activity) params[0];
            String alias = (String) params[1];
            List<Documento> listaDocumentoSelecionados = listViewAdapter.obterListaDocumentosSelecionados();

            //recuperando chave privada e cadeia de certificados
            PrivateKey pk = KeyChain.getPrivateKey(ctx, alias);
            X509Certificate[] chain = KeyChain.getCertificateChain(ctx, alias);

            //certificado usado para codificar dados usando a chave privada
            X509CertificateHolder x509CertHolder = new X509CertificateHolder(chain[0].getEncoded());

            // algoritmo usado para assinar dados
            // foi usado o JCA provider default, visto que não descobri um JCA provider no Android de onde saiu a PrivateKey acima
            ContentSigner sha1Signer = new JcaContentSignerBuilder(Constantes.ALG_SHA1_WITH_RSA).build(pk);

            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

            gen.addSignerInfoGenerator(
                    new JcaSignerInfoGeneratorBuilder(
                            new JcaDigestCalculatorProviderBuilder().setProvider(Constantes.BOUNCY_CASTLE_PROVIDER).build())
                            .build(sha1Signer, x509CertHolder));

            gen.addCertificate(x509CertHolder);


            for (int i = 0; i < listaDocumentoSelecionados.size(); ++i) {
                Documento doc = listaDocumentoSelecionados.get(i);
                CMSTypedData inf = new CMSProcessableByteArray(doc.getArquivo());

                //criando uma assinatura pkcs7 detached
                CMSSignedData signedData = gen.generate(inf, false);
                doc.setAssinatura(signedData.getEncoded());
                doc.setTipo_assinatura(Constantes.TIPO_PKCS7);
                //publishProgress(i + 1);
            }

            return true;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    /*protected void onProgressUpdate(Integer... progress) {
        this.progressDialog.setProgress(progress[0]);
    }*/

    @Override
    protected void onPostExecute(final Boolean success) {
        /*if (this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }*/
        if(success) {
            listViewAdapter.notifyDataSetChanged();
        }
    }
}
