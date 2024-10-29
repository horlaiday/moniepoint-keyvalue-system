package com.example.moniepoint.keyvalue.system.service;


import com.example.moniepoint.keyvalue.system.configuration.ApplicationConfigs;
import com.example.moniepoint.keyvalue.system.util.Constant;
import com.example.moniepoint.keyvalue.system.util.KeyValueLogger;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

@Service

public class LeaderElectionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ApplicationConfigs applicationConfigs;

    @Getter
    private volatile String currentLeader;
    private final AtomicBoolean isLeader = new AtomicBoolean(false);
    KeyValueLogger kvLogger = new KeyValueLogger(Constant.LEADER_ELECTION);


    public LeaderElectionService(ApplicationConfigs applicationConfigs) {
        this.applicationConfigs = applicationConfigs;
    }


    // Heart Beat which periodically(5-secs) check if the current leader is up
    @Scheduled(fixedRate = 5000)
    public void monitorLeader() {
        if (isLeader.get()) {
            // If this node is the leader, broadcast this status
            kvLogger.message("Node " + applicationConfigs.getNodeId() + " is the leader.").info();
        } else {
            try {
                restTemplate.getForObject(applicationConfigs.getInitialLeader()+ "/api/health", String.class);
            } catch (Exception e) {
                kvLogger.message("Leader is down. Starting election.").debug();
                electNewLeader();
            }
        }
    }

    // Elect a new leader
    public synchronized void electNewLeader() {
        currentLeader = applicationConfigs.getInitialLeader();
        for (String node : applicationConfigs.getAllNodes()) {
            if (!node.equals(applicationConfigs.getInitialLeader())) {
                try {
                    restTemplate.postForObject(node + "/api/election", applicationConfigs.getNodeId(), String.class);
                    currentLeader = node;
                    if (node.equals(applicationConfigs.getNodeId())) {
                        isLeader.set(true);
                        kvLogger.message("Node " + applicationConfigs.getNodeId() + " is now the leader.").info();
                    }
                    return;
                } catch (Exception e) {
                    kvLogger.message("Node " + node + " failed election response.").error();
                }
            }
        }
    }

    public boolean isLeader() {
        return isLeader.get();
    }

}


