/*
 * Copyright 2009-2012, Strategic Gains, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.d4games.dzix;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.ConfigurationManager;
import com.d4games.dzix.cmd.MonitorStreamController;
import com.d4games.dzix.cmd.VoidController;

/**
 * Primary entry point to create a RestExpress service. All that's required is a
 * RouteDeclaration. By default: port is 8081, serialization format is JSON,
 * supported formats are JSON and XML.
 * 
 * @author toddf
 */
public class Dzix {
    private static final Logger log = LoggerFactory.getLogger(Dzix.class);

    private List<Service> services = new ArrayList<Service>();
    private static int DEFAULT_MONITOR_PORT = 10070;

    public Dzix() {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.forceClosed", true);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.maxQueueSize", 1000);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.queueSizeRejectionThreshold", 1000);
        DzixChannels.startChannelPruning();
    }

    public Dzix(int useMonitorPort) {
        this();

        if (useMonitorPort <= 0) {
            useMonitorPort = DEFAULT_MONITOR_PORT;
        }
        createServiceMonitor(useMonitorPort).bind();
    }

    public Dzix(int useMonitorPort, int defaultQueueSize) {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.forceClosed", true);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.maxQueueSize", defaultQueueSize);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.queueSizeRejectionThreshold", defaultQueueSize);
        DzixChannels.startChannelPruning();

        if (useMonitorPort <= 0) {
            useMonitorPort = DEFAULT_MONITOR_PORT;
        }

        createServiceMonitor(useMonitorPort).bind();
    }

    public ServiceHttp createServiceHttp() {
        ServiceHttp svc = new ServiceHttp();
        svc.setExecutorThreadCount(0);
        services.add(svc);
        return svc;
    }

    public ServiceWebsocket createServiceWebsocket() {
        ServiceWebsocket svc = new ServiceWebsocket();
        services.add(svc);
        return svc;
    }

    public ServiceHttp createServiceMonitor(int port) {
        ServiceHttp svc = new ServiceHttp();

        svc.setName("monitor").setPort(port).setReuseAddress(false).setSoLinger(0).setUseTcpNoDelay(true).setIoThreadCount(1);

        svc.uri("/favicon.ico", new VoidController());
        svc.uri("/monitor.stream", new MonitorStreamController());

        services.add(svc);
        return svc;
    }

    /**
     * Used in main() to install a default JVM shutdown hook and shut down the
     * server cleanly. Calls shutdown() when JVM termination detected. To
     * utilize your own shutdown hook(s), install your own shutdown hook(s) and
     * call shutdown() instead of awaitShutdown().
     */
    public void awaitShutdown() {
        awaitShutdown(new DzixShutdownHook(this));
    }

    public void awaitShutdown(DzixShutdownHook hook) {
        Runtime.getRuntime().addShutdownHook(hook);
        boolean interrupted = false;

        do {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        } while (!interrupted);
    }

    /**
     * Releases all resources associated with this server so the JVM can
     * shutdown cleanly. Call this method to finish using the server. To utilize
     * the default shutdown hook in main() provided by RestExpress, call
     * awaitShutdown() instead.
     */
    public void shutdown() {
        log.error("shutdown started..");
        for (Service service : services) {
            log.error("{} shutdown started..", service);
            service.shutdown();
            log.error("{} shutdown completed..", service);
        }
        log.error("shutdown completed..");
    }
}
