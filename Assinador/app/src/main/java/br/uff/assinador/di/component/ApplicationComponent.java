package br.uff.assinador.di.component;

import javax.inject.Singleton;

import br.uff.assinador.daoservice.DocumentoDaoService;
import br.uff.assinador.daoservice.UsuarioDaoService;
import br.uff.assinador.di.module.ApplicationModule;
import dagger.Component;

/**
 * Created by matheus on 19/08/15.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    UsuarioDaoService getUsuarioDaoService();
    DocumentoDaoService getDocumentoDaoService();
}
