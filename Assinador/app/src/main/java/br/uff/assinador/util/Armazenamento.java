package br.uff.assinador.util;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by matheus on 01/09/15.
 */
public class Armazenamento {

    /* Verifica se o armazenamento externo está disponível pelo menos para leitura*/
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /* Verifica se o armazenamento externo está disponível para escrita*/
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static Uri obterUriArquivo(File caminho, String nomeArquivo) {
        File arquivo = new File(caminho, nomeArquivo);
        Uri uri = Uri.fromFile(arquivo);
        return uri;
    }

    public static void criarArquivo(File caminho, String nomeArquivo, byte[] dados) {
        System.out.println("isExternalStorageWritable: " + isExternalStorageWritable());

        if (!isExternalStorageWritable()) {
            //TODO: Abrir um dialog informando que o storage externo não está montado.
            //pensar melhor e ver se vale a pena armazenar no storage interno e ver os cuidados a serem tomados
        }

        File arquivo = new File(caminho, nomeArquivo);

        // se arquivo não existir, então cria ele
        FileOutputStream out = null;
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
                out = new FileOutputStream(arquivo);
                out.write(dados);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static byte[] obterArquivo(String nomeArquivo) throws FileNotFoundException {

        System.out.println("isExternalStorageReadable: " + isExternalStorageReadable());
        if (!isExternalStorageReadable()) {
            //TODO: Abrir um dialog informando que o storage externo não está montado.
            //pensar melhor e ver se vale a pena armazenar no storage interno e ver os cuidados a serem tomados
        }

        // Get the absolute path of External Storage Directory
        File arquivo = new File(Constantes.CAMINHO_ARQUIVOS + nomeArquivo);

        int tamanhoArquivoBytes = (int) arquivo.length();
        byte[] buffer = new byte[tamanhoArquivoBytes];

        FileInputStream in = null;
        try {
            in = new FileInputStream(arquivo);
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
