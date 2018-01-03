package com.google.common.hash;

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

public class Temp {
  public static void printBitSize() {
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
      if (i == 1245 || i == 45346 || i == 148935 || i == 2) {
        randoms.add(uuid);
      }
    }
    
    System.out.println(bf.bitSize());
  }
}
