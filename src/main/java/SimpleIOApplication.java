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

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.UUID;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.base.Stopwatch;

import it.unimi.dsi.bits.TransformationStrategies;
import it.unimi.dsi.sux4j.mph.GOVMinimalPerfectHashFunction;
import it.unimi.dsi.sux4j.mph.GOVMinimalPerfectHashFunction.Builder;

public class SimpleIOApplication extends Application<Configuration> {
  public static void main(String[] args) throws Exception {
    BloomFilter<String> bf = BloomFilter.create(
      Funnels.unencodedCharsFunnel(),
      1000000,
      0.01);
    ArrayList<String> l = new ArrayList<String>();
    ArrayList<String> randoms = new ArrayList<String>();
    String uuid = "";
    for (int i = 0; i < 1000000; i++) {
      uuid = UUID.randomUUID().toString().replace("-", "");
      bf.put(uuid);
      l.add(uuid);
      //System.out.println("uuid = " + uuid);
      if (i == 1245 || i == 45346 || i == 148935 || i == 2) {
        randoms.add(uuid);
      }
    }
    
    System.out.println(bf.approximateElementCount());

    Stopwatch stopwatch = Stopwatch.createStarted();
    for (int i = 0; i < 3; i++) {
      System.out.println(bf.mightContain(randoms.get(i)));
    }
    stopwatch.stop();
    System.out.println("bf get: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    stopwatch.reset();
    stopwatch.start();
    GOVMinimalPerfectHashFunction<String> mph = new Builder<String>().keys(l)
      .transform(TransformationStrategies.utf16()).signed(24).build();
        // utf-16 24 bit string
        // not sure if this is correct?
    stopwatch.stop();
    System.out.println("mph create: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    System.out.println(mph.size());

    stopwatch.reset();
    stopwatch.start();
    for (int i = 0; i < 3; i++) {
      System.out.println(mph.containsKey(randoms.get(i)));
    }
    stopwatch.stop();
    System.out.println("mph get: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
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
