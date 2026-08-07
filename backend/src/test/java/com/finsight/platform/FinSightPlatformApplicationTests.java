package com.finsight.platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"incident.created.v1"})
class FinSightPlatformApplicationTests {

    @Test
    void contextLoads() {
    }
}
