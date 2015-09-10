package br.uff.assinador.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import br.uff.assinador.util.Constantes;

/**
 * Created by matheus on 04/09/15.
 */
public class DownloadCRLTask extends AsyncTask<String, Integer, Resultado> {

    private final String TAG = DownloadCRLTask.class.getSimpleName();

    @Override
    protected Resultado doInBackground(String... params) {

        InputStream response = null;
        Resultado<InputStream> resultado = new Resultado<InputStream>();

        try {

            String uri = params[0];
            URL url = new URL(uri);

            //abrir conexão
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(Constantes.READ_TIMEOUT);
            conn.setConnectTimeout(Constantes.CONNECT_TIMEOUT);
            conn.setRequestMethod(Constantes.HTTP_OP_GET);
            conn.setDoInput(true);

            //efetuar a requisição
            conn.connect();

            //obtendo resposta da requisição
            response = conn.getInputStream();
            resultado.set(response);

            //fechar a conexão
            conn.disconnect();

        } catch (MalformedURLException e) {
            resultado.setException(e);
            Log.e(TAG, e.getMessage());
        } catch (ProtocolException e){
            resultado.setException(e);
            Log.e(TAG, e.getMessage());
        } catch (IOException e){
            resultado.setException(e);
            Log.e(TAG, e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    resultado.setException(e);
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        return resultado;
    }
}
