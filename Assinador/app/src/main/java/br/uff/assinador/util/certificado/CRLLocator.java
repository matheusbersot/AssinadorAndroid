package br.uff.assinador.util.certificado;

import android.app.Activity;
import android.util.Log;

import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.x509.CRLDistPoint;
import org.spongycastle.asn1.x509.DistributionPoint;
import org.spongycastle.asn1.x509.DistributionPointName;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import br.uff.assinador.asyncTask.DownloadCRLTask;
import br.uff.assinador.asyncTask.Resultado;
import br.uff.assinador.util.Constantes;
import br.uff.assinador.util.Util;

/**
 * Created by matheus on 03/09/15.
 * <p/>
 * Classe responsável por buscar a CRL de um certificado à partir da informação
 * contida na extensão Ponto de Distribuição de LCR (CRL em inglês).
 */
public class CRLLocator {

    private X509Certificate certificate;
    private List<X509CRL> crlList;
    private final String TAG = CRLLocator.class.getSimpleName();


    /**
     * Método construtor
     *
     * @param certificate Certificado de onde será extraído a URI para obter a CRL
     */
    public CRLLocator(X509Certificate certificate) {

        if (certificate == null)
            throw new NullPointerException("Certificado informado é nulo");

        this.certificate = certificate;
        this.crlList = new ArrayList<X509CRL>();
    }


    /**
     * Método utilizado para obter a CRL através da URI encontrada no certificado. O método
     * retorna um objeto X509CRL.
     *
     * @return X509CRL
     * @throws CRLException
     */
    public List<X509CRL> getCRL() throws CRLException {

        try {

            List<String> urlList = getCrlDistributionPoints(certificate);
            for(String url: urlList)
            {
                this.crlList.add(this.getRemoteCRL(url));
            }
            return this.crlList;
        } catch (Exception e) {
            Log.e(TAG, "Falha ao obter a CRL: " + e.getMessage());
            e.printStackTrace();
            throw new CRLException("Falha ao obter a CRL: " + e.getMessage());
        }
    }


    /**
     * Método utilitário usado para fazer a leitura do InputStream e transformar em um
     * objeto da classe X509CRL
     *
     * @param  crl
     * @throws IOException, CertificateException, NoSuchProviderException
     */
    private X509CRL createCRL(final byte[] crl)
            throws CertificateException, NoSuchProviderException, CRLException, IOException {

        InputStream is = new ByteArrayInputStream(crl);
        try{
            CertificateFactory cf = CertificateFactory.getInstance(Constantes.X509_CERT_TYPE, Constantes.BOUNCY_CASTLE_PROVIDER);
            return (X509CRL) cf.generateCRL(is);
        }finally {
            if(is != null){
                is.close();
            }

        }
    }

    /**
     * Método utilizado para buscar CRL em um local remoto, através da Internet.
     *
     * @throws Exception
     */
    private X509CRL getRemoteCRL(final String url)
            throws CertificateException, CRLException, NoSuchProviderException, IOException {

        byte[] file = Util.downloadFile(url);
        return createCRL(file);
    }


    /**
     * Extrai todas as URLs dos pontos de distribuição LCR à partir da extensão "CRL Distribution Point"
     * no certificado do tipo X.509. Se a extensão "CRL Distribution Point" não estiver
     * disponível, é retornado uma lista vazia.
     * Fonte: http://www.nakov.com/tag/crl-distribution-point/
     */
    private static List<String> getCrlDistributionPoints(X509Certificate cert)
            throws CertificateParsingException, IOException {

        byte[] crldpExt = cert.getExtensionValue(Extension.cRLDistributionPoints.getId());
        if (crldpExt == null) {
            List<String> emptyList = new ArrayList<String>();
            return emptyList;
        }

        ASN1InputStream oAsnInStream = new ASN1InputStream(new ByteArrayInputStream(crldpExt));
        ASN1Primitive derObjCrlDP = oAsnInStream.readObject();
        DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;

        byte[] crldpExtOctets = dosCrlDP.getOctets();
        ASN1InputStream oAsnInStream2 = new ASN1InputStream(new ByteArrayInputStream(crldpExtOctets));
        ASN1Primitive derObj2 = oAsnInStream2.readObject();
        CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);

        List<String> crlUrls = new ArrayList<String>();
        for (DistributionPoint dp : distPoint.getDistributionPoints()) {
            DistributionPointName dpn = dp.getDistributionPoint();
            // Look for URIs in fullName
            if (dpn != null) {
                if (dpn.getType() == DistributionPointName.FULL_NAME) {
                    GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
                    // Look for an URI
                    for (int j = 0; j < genNames.length; j++) {
                        if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                            String url = DERIA5String.getInstance(genNames[j].getName()).getString();
                            crlUrls.add(url);
                        }
                    }
                }
            }
        }
        return crlUrls;
    }
}
