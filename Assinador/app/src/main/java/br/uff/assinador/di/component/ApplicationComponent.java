package br.uff.assinador.di.component;

import android.app.Activity;

import javax.inject.Singleton;

import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.daoservice.UsuarioDaoService;
import br.uff.assinador.di.module.ApplicationModule;
import br.uff.assinador.visao.BaseActivity;
import br.uff.assinador.visao.MainActivity;
import dagger.Component;

/**
 * Created by matheus on 19/08/15.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    //método usado para definir classes que possuem campos anotados com @Inject para serem injetados
    void inject(MainActivity activity);

    UsuarioDaoService getUsuarioDaoService();
    DocumentoDaoService getDocumentoDaoService();
}
