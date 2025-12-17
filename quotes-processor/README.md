# quotes-processor

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw clean compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev-ui.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw clean package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw clean package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

According to your environment, you may want to customize:
- The **AMQP broker connection parameters** by adding the following run-time _system properties_:
    - `quarkus.qpid-jms.url`
    - `quarkus.qpid-jms.username`
    - `quarkus.qpid-jms.password`
- The Jaeger collector endpoint by adding the following run-time _system properties_:
    - `quarkus.otel.exporter.otlp.endpoint`

Example:
```shell
java -Dquarkus.qpid-jms.url="amqps://amq-ssl-broker-amqp-0-svc-rte-amq7-broker-cluster.apps.ocp4.jnyilimb.eu:443?transport.trustAll=true&transport.verifyHost=false&amqp.idleTimeout=120000" -Dquarkus.otel.exporter.otlp.endpoint="http://localhost:4317" -jar target/quarkus-app/quarkus-run.jar
```

## Creating a native executable

You can create a native executable using the following command:

```shell
./mvnw clean package -Pnative -Dquarkus.native.native-image-xmx=7g
```

>**NOTE** : The project is configured to use a container runtime for native builds. See `quarkus.native.container-build=true` in the [`application.properties`](./src/main/resources/application.properties). Also, adjust the `quarkus.native.native-image-xmx` value according to your container runtime available memory resources.

You can then execute your native executable with: `./target/quotes-processor-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

>**NOTE** : If your are on Apple Silicon and built the native image inside a Linux container (-Dquarkus.native.container-build=true), the result is a Linux ELF binary. macOS can’t execute Linux binaries, so launching it on macOS yields “exec format error”. Follow the steps below to run your Linux native binary.

1. Build the container image of your Linux native binary:
    ```shell
    podman build -f src/main/docker/Dockerfile.native -t quotes-processor .
    ```
2. Run the container:
    ```shell
    podman run --rm --name quotes-processor \
    -p 8081:8080,9877:9876 \
    -e QUARKUS_QPID-JMS_URL="amqps://amq-ssl-broker-amqp-0-svc-rte-amq7-broker-cluster.apps.ocp4.jnyilimb.eu:443?transport.trustAll=true&transport.verifyHost=false&amqp.idleTimeout=120000" \
    -e QUARKUS_QPID-JMS_USERNAME=quotes-processor \
    -e QUARKUS_QPID-JMS_PASSWORD=openshift \
    -e QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT=http://host.containers.internal:4317 \
    quotes-processor 
    ```

## Start-up time comparison in the same environment

Used environment:
- **Laptop**: MacBook PRO
- **CPU**: Apple M2 PRO
- **RAM**: 32Gb
- **Container runtime for native builds**: podman v5.7.0

### JVM mode -> _started in **1.636s**_

```shell
# java -Dquarkus.qpid-jms.url="amqps://amq-ssl-broker-amqp-0-svc-rte-amq7-broker-cluster.apps.ocp4.jnyilimb.eu:443?transport.trustAll=true&transport.verifyHost=false&amqp.idleTimeout=120000" -Dquarkus.otel.exporter.otlp.endpoint="http://localhost:4317" -Dquarkus.http.port=8081 -Dquarkus.management.port=9877 -jar target/quarkus-app/quarkus-run.jar
[...]
2025-12-10 18:40:45,453 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.MainSupport] (main) Apache Camel (Main) 4.14.0.redhat-00009 is starting
2025-12-10 18:40:45,480 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.BaseMainSupport] (main) Auto-configuration summary
2025-12-10 18:40:45,480 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.BaseMainSupport] (main)     [MicroProfilePropertiesSource] camel.context.name = quotes-processor
2025-12-10 18:40:45,626 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Apache Camel 4.14.0.redhat-00009 (quotes-processor) is starting
2025-12-10 18:40:45,710 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.op.OpenTelemetryTracer] (main) OpenTelemetryTracer enabled using instrumentation-name: camel
2025-12-10 18:40:45,710 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Using ThreadPoolFactory: org.apache.camel.opentelemetry.OpenTelemetryInstrumentedThreadPoolFactory@21d5c1a0
2025-12-10 18:40:46,245 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.qp.jm.JmsConnection] (AmqpProvider :(1):[amqps://amq-ssl-broker-amqp-0-svc-rte-amq7-broker-cluster.apps.ocp4.jnyilimb.eu:443]) Connection ID:e8191044-e13d-438e-88a0-71570f49747f:1 connected to server: amqps://amq-ssl-broker-amqp-0-svc-rte-amq7-broker-cluster.apps.ocp4.jnyilimb.eu:443
2025-12-10 18:40:46,246 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Routes startup (total:1)
2025-12-10 18:40:46,246 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main)     Started consume-quote-requests-route (amqp://queue:quote-requests)
2025-12-10 18:40:46,247 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Apache Camel 4.14.0.redhat-00009 (quotes-processor) started in 741ms (build:0ms init:121ms start:620ms)
2025-12-10 18:40:46,314 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) quotes-processor 1.0.0-SNAPSHOT on JVM (powered by Quarkus 3.27.0.redhat-00001) started in 1.636s. Listening on: http://0.0.0.0:8081. Management interface listening on http://0.0.0.0:9877.
2025-12-10 18:40:46,316 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) Profile prod activated. 
2025-12-10 18:40:46,316 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) Installed features: [camel-amqp, camel-bean, camel-core, camel-direct, camel-jackson, camel-jms, camel-jolokia, camel-management, camel-micrometer, camel-microprofile-health, camel-observability-services, camel-opentelemetry, camel-xml-io-dsl, cdi, kubernetes, messaginghub-pooled-jms, micrometer, opentelemetry, qpid-jms, smallrye-context-propagation, smallrye-health, vertx]
```

### Native mode -> _started in **0.425s**_

```shell
# podman run --rm --name quotes-processor -p 8081:8080,9877:9876 -e QUARKUS_QPID-JMS_URL="amqps://amq-ssl-broker-amqp-0-svc-rte-amq7-broker-cluster.apps.ocp4.jnyilimb.eu:443?transport.trustAll=true&transport.verifyHost=false&amqp.idleTimeout=120000" -e QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT=http://host.containers.internal:4317 quotes-processor
[...]
2025-12-10 17:41:14,272 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.MainSupport] (main) Apache Camel (Main) 4.14.0.redhat-00009 is starting
2025-12-10 17:41:14,278 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.BaseMainSupport] (main) Auto-configuration summary
2025-12-10 17:41:14,278 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.BaseMainSupport] (main)     [MicroProfilePropertiesSource] camel.context.name = quotes-processor
2025-12-10 17:41:14,286 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Apache Camel 4.14.0.redhat-00009 (quotes-processor) is starting
2025-12-10 17:41:14,368 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.op.OpenTelemetryTracer] (main) OpenTelemetryTracer enabled using instrumentation-name: camel
2025-12-10 17:41:14,368 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Using ThreadPoolFactory: org.apache.camel.opentelemetry.OpenTelemetryInstrumentedThreadPoolFactory@76ffc17c
2025-12-10 17:41:14,633 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.qp.jm.JmsConnection] (AmqpProvider :(1):[amqps://amq-ssl-broker-amqp-0-svc-rte-amq7-broker-cluster.apps.ocp4.jnyilimb.eu:443]) Connection ID:069808de-e605-4521-a58b-2f3886cb92a1:1 connected to server: amqps://amq-ssl-broker-amqp-0-svc-rte-amq7-broker-cluster.apps.ocp4.jnyilimb.eu:443
2025-12-10 17:41:14,633 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Routes startup (total:1)
2025-12-10 17:41:14,633 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main)     Started consume-quote-requests-route (amqp://queue:quote-requests)
2025-12-10 17:41:14,633 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Apache Camel 4.14.0.redhat-00009 (quotes-processor) started in 353ms (build:0ms init:6ms start:347ms)
2025-12-10 17:41:14,637 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) quotes-processor 1.0.0-SNAPSHOT native (powered by Quarkus 3.27.0.redhat-00001) started in 0.425s. Listening on: http://0.0.0.0:8080. Management interface listening on http://0.0.0.0:9876.
2025-12-10 17:41:14,637 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) Profile prod activated. 
2025-12-10 17:41:14,637 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) Installed features: [camel-amqp, camel-bean, camel-core, camel-direct, camel-jackson, camel-jms, camel-jolokia, camel-management, camel-micrometer, camel-microprofile-health, camel-observability-services, camel-opentelemetry, camel-xml-io-dsl, cdi, kubernetes, messaginghub-pooled-jms, micrometer, opentelemetry, qpid-jms, smallrye-context-propagation, smallrye-health, vertx]
```