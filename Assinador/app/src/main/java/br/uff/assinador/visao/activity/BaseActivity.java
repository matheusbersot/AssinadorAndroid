package br.uff.assinador.visao.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import br.uff.assinador.AndroidApp;
import br.uff.assinador.di.component.ApplicationComponent;

/**
 * Created by matheus on 20/08/15.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApp)getApplication()).getApplicationComponent();
    }

}
