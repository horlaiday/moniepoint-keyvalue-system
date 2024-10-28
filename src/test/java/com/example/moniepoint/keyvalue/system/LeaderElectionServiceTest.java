package com.example.moniepoint.keyvalue.system;

import com.example.moniepoint.keyvalue.system.configuration.ApplicationConfigs;
import com.example.moniepoint.keyvalue.system.service.LeaderElectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class LeaderElectionServiceTest {

    @InjectMocks
    private LeaderElectionService leaderElectionService;

    @Mock
    private RestTemplate restTemplate;

    private final String nodeId = "node-1";
    private final String initialLeader = "http://localhost:8080";
    @Autowired
    private  ApplicationConfigs applicationConfigs;
    private final List<String> allNodes = Arrays.asList("http://node-1:8080", "http://node-2:8080", "http://node-3:8080");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        leaderElectionService = new LeaderElectionService(applicationConfigs);
    }

    @Test
    public void testInitialLeaderIsSetCorrectly() {
        leaderElectionService.monitorLeader();
        assertEquals(initialLeader, leaderElectionService.getCurrentLeader(), "Initial leader should be set to node-1");
    }

    @Test
    public void testLeaderHealthCheck_whenLeaderIsUp() {
        leaderElectionService.monitorLeader();
        assertEquals(initialLeader, leaderElectionService.getCurrentLeader(), "Leader should remain as node-1 if health check is successful");
    }

    @Disabled
    @Test
    public void testLeaderHealthCheck_whenLeaderIsDown() {

        leaderElectionService.monitorLeader();
        String newLeader = leaderElectionService.getCurrentLeader();
        assertNotEquals(initialLeader, newLeader, "New leader should be elected if the current leader is down");
    }

    @Disabled
    @Test
    public void testElectNewLeader_whenNodeIsCandidate() {
        when(restTemplate.getForObject(initialLeader + "/api/health", String.class)).thenThrow(new RuntimeException("Leader down"));
        when(restTemplate.postForObject(anyString(), anyString(), eq(String.class))).thenReturn("Election complete");

        leaderElectionService.electNewLeader();
        String newLeader = leaderElectionService.getCurrentLeader();
        assertNotNull(newLeader, "A new leader should be elected");
        assertNotEquals(initialLeader, newLeader, "The new leader should not be the initial leader if it has failed");
    }

    @Disabled
    @Test
    public void testFailover_whenCurrentNodeBecomesLeader() {
        leaderElectionService.electNewLeader();
        when(restTemplate.getForObject(anyString() + "/api/health", String.class)).thenReturn("OK");
        assertTrue(leaderElectionService.isLeader(), "The node should be the leader after election");
    }

    @Test
    public void testElectionFails_whenAllNodesAreUnreachable() {
        for (String node : allNodes) {
        }

        leaderElectionService.electNewLeader();
        String currentLeader = leaderElectionService.getCurrentLeader();
        assertNotNull(currentLeader, "If no new leader can be elected, the leader should remain as the initial value");
    }
}

