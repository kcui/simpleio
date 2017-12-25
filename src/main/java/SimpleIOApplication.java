import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Environment;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.simpleio.basicauth.AppAuthenticator;
import com.simpleio.basicauth.AppAuthorizer;
import com.simpleio.basicauth.User;

public class SimpleIOApplication extends Application<Configuration> {
  public static void main(String[] args) throws Exception {
    new SimpleIOApplication().run(args);
  }

  @Override
  public void run(Configuration configuration, Environment e) {
    e.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator(new AppAuthenticator())
            .setAuthorizer(new AppAuthorizer())
            .setRealm("App Security")
            .buildAuthFilter()));
        e.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        e.jersey().register(RolesAllowedDynamicFeature.class);
    e.jersey().register(new Resource());
  }
}
