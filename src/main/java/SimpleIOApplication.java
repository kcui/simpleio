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

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import com.google.common.hash.Temp;

public class SimpleIOApplication extends Application<Configuration> {
  public static final int NUM_REPS = 10;
  public static final int NUM_RETRIEVALS = 10000;
  public static final int NUM_ELEMENTS = 1000000;

  public static void main(String[] args) throws Exception {
    System.out.println("Measuring BF metrics...");
    System.out.println("Measuring BF build time...");
    measureBFBuildTime();
    System.out.println("Measuring BF retrieve time...");
    measureBFRetrievalTime();
    System.out.println("Measuring BF memory...");
    measureBFMemory();

    System.out.println("Measuring MPH metrics...");
    System.out.println("Measuring MPH build time...");
    measureMPHBuildTime();
    System.out.println("Measuring MPH retrieve time...");
    measureMPHRetrievalTime();
    System.out.println("Measuring MPH memory...");
    measureMPHMemory();
  }

  public static void measureBFBuildTime() {
    MetricRegistry mr = new MetricRegistry();
    for (int i = 0; i < NUM_REPS; i++) {
      Timer timer = mr.timer("timer");
      Timer.Context context = timer.time();
      try {
        BloomFilter<String> bf = BloomFilter.create(
          Funnels.unencodedCharsFunnel(),
          NUM_ELEMENTS,
          0.01);
        String uuid = "";
        for (int j = 0; j < NUM_ELEMENTS; j++) {
          uuid = UUID.randomUUID().toString().replace("-", "");
          bf.put(uuid);
        }
      } finally {
        context.stop();
      }
    }
    ConsoleReporter reporter = ConsoleReporter.forRegistry(mr).build();
    reporter.report();
  }

  public static void measureBFRetrievalTime() throws Exception {
    BloomFilter<String> bf = BloomFilter.create(
      Funnels.unencodedCharsFunnel(),
      NUM_ELEMENTS,
      0.01);
    ArrayList<String> randoms = new ArrayList<String>();
    String uuid = "";
    for (int i = 0; i < NUM_ELEMENTS; i++) {
      uuid = UUID.randomUUID().toString().replace("-", "");
      bf.put(uuid);
      randoms.add(uuid);
    }
    MetricRegistry mr = new MetricRegistry();
    for (int i = 0; i < NUM_RETRIEVALS; i++) {
      Timer timer = mr.timer("timer");
      Timer.Context context = timer.time();
      try {
        bf.mightContain(randoms.get(i));
      } finally {
        context.stop();
      }
    }
    ConsoleReporter reporter = ConsoleReporter.forRegistry(mr).build();
    reporter.report();
  }

  public static void measureBFMemory() throws Exception {
    System.out.print("memory, in bits: ");
    Temp.printBitSize();
  }

  public static void measureMPHBuildTime() throws Exception {
    MetricRegistry mr = new MetricRegistry();
    for (int i = 0; i < NUM_REPS; i++) {
      Timer timer = mr.timer("timer");
      Timer.Context context = timer.time();
      try {
        ArrayList<String> l = new ArrayList<String>();
        String uuid = "";
        for (int j = 0; j < NUM_ELEMENTS; j++) {
          uuid = UUID.randomUUID().toString().replace("-", "");
          l.add(uuid);
        }
        GOVMinimalPerfectHashFunction<String> mph = new Builder<String>().keys(l)
          .transform(TransformationStrategies.utf16()).signed(24).build();
      } finally {
        context.stop();
      }
    }
    ConsoleReporter reporter = ConsoleReporter.forRegistry(mr).build();
    reporter.report();
  }

  public static void measureMPHRetrievalTime() throws Exception {
    ArrayList<String> l = new ArrayList<String>();
    ArrayList<String> randoms = new ArrayList<String>();
    String uuid = "";
    for (int i = 0; i < NUM_ELEMENTS; i++) {
      uuid = UUID.randomUUID().toString().replace("-", "");
      l.add(uuid);
      randoms.add(uuid);
    }
    GOVMinimalPerfectHashFunction<String> mph = new Builder<String>().keys(l)
      .transform(TransformationStrategies.utf16()).signed(24).build();

    MetricRegistry mr = new MetricRegistry();
    for (int i = 0; i < NUM_RETRIEVALS; i++) {
      Timer timer = mr.timer("timer");
      Timer.Context context = timer.time();
      try {
        mph.get(randoms.get(i));
      } finally {
        context.stop();
      }
    }
    ConsoleReporter reporter = ConsoleReporter.forRegistry(mr).build();
    reporter.report();
  }

  public static void measureMPHMemory() throws Exception {
    int totalbits = 0;
    ArrayList<String> l = new ArrayList<String>();
    String uuid = "";
    for (int i = 0; i < NUM_ELEMENTS; i++) {
      uuid = UUID.randomUUID().toString().replace("-", "");
      l.add(uuid);
    }
    GOVMinimalPerfectHashFunction<String> mph = new Builder<String>().keys(l)
      .transform(TransformationStrategies.utf16()).signed(24).build();
    totalbits += mph.numBits();
    System.out.println("avg memory, in bits: " + (double) totalbits / NUM_REPS);
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
