package br.uff.assinador.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ActionMode;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Principal;

import br.uff.assinador.asyncTask.Resultado;
import br.uff.assinador.util.certificado.CertificadoACEnum;

/**
 * Created by matheus on 21/08/15.
 */
public class Util {

    private static final String TAG = Util.class.getSimpleName();


    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public static void showToastOnUIThread(final Activity activity, final String msg, final boolean longToast) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (longToast)
                    showLongToast(activity, msg);
                else
                    showToast(activity, msg);
            }
        });
    }

    public static void showErrorAlertDialogOnUiThread(final Activity activity, final String msg) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

                // define título
                alertDialogBuilder.setTitle("Erro");

                // set dialog message
                alertDialogBuilder
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //activity.finish();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }

    public static byte[] downloadFile(final String uri) throws IOException {

        InputStream response = null;
        byte[] responseInBytes = null;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(uri);

            //abrir conexão
            conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(Constantes.READ_TIMEOUT);
            conn.setConnectTimeout(Constantes.CONNECT_TIMEOUT);
            conn.setRequestMethod(Constantes.HTTP_OP_GET);
            conn.setDoInput(true);

            //efetuar a requisição
            conn.connect();

            //obtendo resposta da requisição
            response = conn.getInputStream();
            responseInBytes = Util.copy(response, Constantes.BUFFER_SIZE);

            //retorna resultado
            return responseInBytes;

        } finally {

            //fechar stream
            if (response != null) {
                response.close();
            }

            //fechar a conexão
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static byte[] copy(InputStream is, int bufferSize) throws IOException {

        ByteArrayOutputStream baos = null;

        if(bufferSize > Integer.MAX_VALUE)
            throw new IOException("Buffer size greater than maximum value of integer type");

        try {
            baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[bufferSize];

            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer);
            }

            return baos.toByteArray();

        } finally {
            if (baos != null) {
                baos.close();
            }
        }
    }

    public static void closeContextualActionBar(final Activity act, final ActionMode mode) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mode.finish();  // Fecha o CAB (contextual action bar)
            }
        });
    }

    public static class Certificado {

        private static Principal[] listaEmissoresPermitidos;

        public static Principal[] obterListaEmissoresPermitidos() {

            if (listaEmissoresPermitidos == null) {

                listaEmissoresPermitidos = new Principal[3];
                listaEmissoresPermitidos[0] = CertificadoACEnum.AC_RAIZ_ICPEDU_V2.getDonoCertificado();
                listaEmissoresPermitidos[1] = CertificadoACEnum.AC_PESSOAS_P1.getDonoCertificado();
                listaEmissoresPermitidos[2] = CertificadoACEnum.AC_PESSOAS_P5.getDonoCertificado();
            }

            return listaEmissoresPermitidos;
        }
    }

}
