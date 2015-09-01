package br.uff.assinador.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.ActionMode;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;

/**
 * Created by matheus on 21/08/15.
 */
public class Util {

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public static void showToastOnUIThread(final Activity act, final String msg, final boolean longToast) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (longToast)
                    showLongToast(act, msg);
                else
                    showToast(act, msg);
            }
        });
    }

    public static void closeContextualActionBar(final Activity act, final ActionMode mode)
    {
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
