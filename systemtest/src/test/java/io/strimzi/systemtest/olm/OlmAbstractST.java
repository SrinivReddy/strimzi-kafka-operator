/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.systemtest.olm;

import io.strimzi.api.kafka.model.Kafka;
import io.strimzi.api.kafka.model.KafkaBridge;
import io.strimzi.api.kafka.model.KafkaConnect;
import io.strimzi.api.kafka.model.KafkaConnectS2I;
import io.strimzi.api.kafka.model.KafkaMirrorMaker;
import io.strimzi.api.kafka.model.KafkaMirrorMaker2;
import io.strimzi.api.kafka.model.KafkaTopic;
import io.strimzi.api.kafka.model.KafkaUser;
import io.strimzi.systemtest.AbstractST;
import io.strimzi.systemtest.resources.OlmResource;
import io.strimzi.systemtest.utils.kafkaUtils.KafkaBridgeUtils;
import io.strimzi.systemtest.utils.kafkaUtils.KafkaConnectS2IUtils;
import io.strimzi.systemtest.utils.kafkaUtils.KafkaConnectUtils;
import io.strimzi.systemtest.utils.kafkaUtils.KafkaMirrorMaker2Utils;
import io.strimzi.systemtest.utils.kafkaUtils.KafkaMirrorMakerUtils;
import io.strimzi.systemtest.utils.kafkaUtils.KafkaTopicUtils;
import io.strimzi.systemtest.utils.kafkaUtils.KafkaUserUtils;
import io.strimzi.systemtest.utils.kafkaUtils.KafkaUtils;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;

import java.util.List;
import java.util.Map;

import static io.strimzi.test.k8s.KubeClusterResource.cmdKubeClient;

public class OlmAbstractST extends AbstractST {
    private static final Logger LOGGER = LogManager.getLogger(OlmAbstractST.class);

    void doTestDeployExampleKafka() {
        JsonObject kafkaResource = OlmResource.getExampleResources().get(Kafka.RESOURCE_KIND);
        cmdKubeClient().applyContent(kafkaResource.toString());
        KafkaUtils.waitForKafkaReady(kafkaResource.getJsonObject("metadata").getString("name"));
    }

    void doTestDeployExampleKafkaUser() {
        JsonObject kafkaUserResource = OlmResource.getExampleResources().get(KafkaUser.RESOURCE_KIND);
        cmdKubeClient().applyContent(kafkaUserResource.toString());
        KafkaUserUtils.waitForKafkaUserCreation(kafkaUserResource.getJsonObject("metadata").getString("name"));
    }

    void doTestDeployExampleKafkaTopic() {
        JsonObject kafkaTopicResource = OlmResource.getExampleResources().get(KafkaTopic.RESOURCE_KIND);
        cmdKubeClient().applyContent(kafkaTopicResource.toString());
        KafkaTopicUtils.waitForKafkaTopicCreation(kafkaTopicResource.getJsonObject("metadata").getString("name"));
    }

    void doTestDeployExampleKafkaConnect() {
        JsonObject kafkaConnectResource = OlmResource.getExampleResources().get(KafkaConnect.RESOURCE_KIND);
        cmdKubeClient().applyContent(kafkaConnectResource.toString());
        KafkaConnectUtils.waitForConnectReady(kafkaConnectResource.getJsonObject("metadata").getString("name"));
    }

    void doTestDeployExampleKafkaConnectS2I() {
        Map<String, JsonObject> examples = OlmResource.getExampleResources();
        JsonObject kafkaConnectS2IResource = examples.get(KafkaConnectS2I.RESOURCE_KIND);
        kafkaConnectS2IResource.getJsonObject("metadata").put("name", "my-connect-s2i-cluster");
        kafkaConnectS2IResource.getJsonObject("spec").put("insecureSourceRepository", true);
        examples.put(KafkaConnectS2I.RESOURCE_KIND, kafkaConnectS2IResource);
        OlmResource.setExampleResources(examples);
        cmdKubeClient().applyContent(kafkaConnectS2IResource.toString());
        KafkaConnectS2IUtils.waitForConnectS2IReady(kafkaConnectS2IResource.getJsonObject("metadata").getString("name"));
    }

    void doTestDeployExampleKafkaBridge() {
        JsonObject kafkaBridgeResource = OlmResource.getExampleResources().get(KafkaBridge.RESOURCE_KIND);
        cmdKubeClient().applyContent(kafkaBridgeResource.toString());
        KafkaBridgeUtils.waitForKafkaBridgeReady(kafkaBridgeResource.getJsonObject("metadata").getString("name"));
    }

    void doTestDeployExampleKafkaMirrorMaker() {
        JsonObject kafkaMirrorMakerResource = OlmResource.getExampleResources().get(KafkaMirrorMaker.RESOURCE_KIND);
        cmdKubeClient().applyContent(kafkaMirrorMakerResource.toString()
                .replace("my-source-cluster-kafka-bootstrap", "my-cluster-kafka-bootstrap")
                .replace("my-target-cluster-kafka-bootstrap", "my-cluster-kafka-bootstrap"));
        KafkaMirrorMakerUtils.waitForKafkaMirrorMakerReady(kafkaMirrorMakerResource.getJsonObject("metadata").getString("name"));
    }

    void doTestDeployExampleKafkaMirrorMaker2() {
        JsonObject kafkaMirrorMaker2Resource = OlmResource.getExampleResources().get(KafkaMirrorMaker2.RESOURCE_KIND);
        cmdKubeClient().applyContent(kafkaMirrorMaker2Resource.toString()
                .replace("my-cluster-source-kafka-bootstrap", "my-cluster-kafka-bootstrap")
                .replace("my-cluster-target-kafka-bootstrap", "my-cluster-kafka-bootstrap"));
        KafkaMirrorMaker2Utils.waitForKafkaMirrorMaker2Ready(kafkaMirrorMaker2Resource.getJsonObject("metadata").getString("name"));
    }

    @AfterAll
    void teardown() {
        cmdKubeClient().deleteContent(OlmResource.getExampleResources().get(KafkaMirrorMaker2.RESOURCE_KIND).toString());
        cmdKubeClient().deleteContent(OlmResource.getExampleResources().get(KafkaMirrorMaker.RESOURCE_KIND).toString());
        cmdKubeClient().deleteContent(OlmResource.getExampleResources().get(KafkaBridge.RESOURCE_KIND).toString());
        cmdKubeClient().deleteContent(OlmResource.getExampleResources().get(KafkaConnectS2I.RESOURCE_KIND).toString());
        cmdKubeClient().deleteContent(OlmResource.getExampleResources().get(KafkaConnect.RESOURCE_KIND).toString());
        cmdKubeClient().deleteContent(OlmResource.getExampleResources().get(KafkaTopic.RESOURCE_KIND).toString());
        cmdKubeClient().deleteContent(OlmResource.getExampleResources().get(KafkaUser.RESOURCE_KIND).toString());
        cmdKubeClient().deleteContent(OlmResource.getExampleResources().get(Kafka.RESOURCE_KIND).toString());
    }

    @Override
    protected void recreateTestEnv(String coNamespace, List<String> bindingsNamespaces) {
    }
}
