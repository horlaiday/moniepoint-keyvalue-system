package com.example.moniepoint.keyvalue.system.configuration;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@Data
public class ApplicationConfigs {

    @Value("${follower.nodes}")
    private List<String> followerNodes;

    @Value("${replicate.data}")
    private boolean replicateData;

    @Value("${node.id}")
    private String nodeId;

    @Value("${all.nodes}")
    private List<String> allNodes;

    @Value("${initial.leader}")
    private String initialLeader;

    /**
     * Read timeout duration. Values are provided in seconds
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration httpReadTimeout = Duration.ofSeconds(30);
    /**
     * Connection timeout duration. Values are provided in seconds
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration httpConnectTimeout = Duration.ofSeconds(30);
}
