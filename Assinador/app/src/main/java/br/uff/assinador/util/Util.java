package br.uff.assinador.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by matheus on 21/08/15.
 */
public class Util {

    public static class Constantes{
        public static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    }

    public static class Armazenamento{

        /* Checks if external storage is available to at least read */
        public static boolean isExternalStorageReadable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return true;
            }
            return false;
        }

        public static byte[] obterArquivo(String filename) throws FileNotFoundException {

            System.out.println("isExternalStorageReadable: " + isExternalStorageReadable());

            // Get the absolute path of External Storage Directory
            File file = new File("/storage/extSdCard/"+filename);

            int fileSizeBytes = (int) file.length();
            byte[] buffer = new byte[fileSizeBytes];

            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                in.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return buffer;
        }
    }

}
