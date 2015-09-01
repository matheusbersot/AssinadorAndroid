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
    public static final String TIPO_CHAVE_RSA = "RSA";
    public static final String TIPO_CHAVE_DSA = "DSA";

    //MIME TYPE
    public static final String TIPO_PKCS7 = "application/pkcs7-signature";

}
