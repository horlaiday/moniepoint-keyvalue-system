package com.example.moniepoint.keyvalue.system.controller;

import com.example.moniepoint.keyvalue.system.service.LeaderElectionService;
import com.example.moniepoint.keyvalue.system.util.Constant;
import com.example.moniepoint.keyvalue.system.util.KeyValueLogger;
import com.example.moniepoint.keyvalue.system.util.ResponseMessage;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/api")
public class ReplicaFailoverController {

    private final LeaderElectionService leaderElectionService;
    KeyValueLogger kvLogger = new KeyValueLogger(Constant.FAIL_OVER);


    public ReplicaFailoverController(LeaderElectionService leaderElectionService) {
        this.leaderElectionService = leaderElectionService;
    }

    @PostMapping("/election")
    public String participateInElection(@RequestBody String candidateId) {
        kvLogger.message("about to do election").info();
        if (leaderElectionService.isLeader()) {
            return ResponseMessage.NODE_LEADER;
        }
        leaderElectionService.electNewLeader();
        return ResponseMessage.ELECTION_COMPLETE;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return ResponseMessage.OK;
    }
}

