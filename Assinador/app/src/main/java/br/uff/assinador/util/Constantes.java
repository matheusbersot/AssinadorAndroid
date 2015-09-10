package br.uff.assinador.util;

/**
 * Created by matheus on 01/09/15.
 */
public class Constantes {
    public static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    public static final String CAMINHO_ARQUIVOS = "/storage/extSdCard/";

    //Constantes relacionadas ao processo de assinatura digital
    public static final String BOUNCY_CASTLE_PROVIDER = "BC";
    public static final String ALG_SHA1_WITH_RSA = "SHA1withRSA";
    public static final String ALG_HASH_SHA1 = "SHA1";
    public static final String TIPO_CHAVE_RSA = "RSA";
    public static final String TIPO_CHAVE_DSA = "DSA";
    public static final String OID_ALG_HASH_SHA1 = "1.3.14.3.2.26";
    public static final String TIPO_PKCS7 = "application/pkcs7-signature"; //MIME TYPE

    //Constantes relacionadas ao processo de validação da cadeia de certificação
    public static final String X509_CERT_TYPE = "X.509";
    public static final String ANDROID_CA_STORE="AndroidCAStore";

    //Constantes relacionadas a conexão com a Internet
    public static final int READ_TIMEOUT = 10000; //ms
    public static final int CONNECT_TIMEOUT = 15000; //ms
    public static final int BUFFER_SIZE = 10 * 1024; //10kb

    public static final String HTTP_OP_GET = "GET";

}
