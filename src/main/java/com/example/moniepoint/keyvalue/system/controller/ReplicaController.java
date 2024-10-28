package com.example.moniepoint.keyvalue.system.controller;

import com.example.moniepoint.keyvalue.system.component.KeyValueComponent;
import com.example.moniepoint.keyvalue.system.util.Constant;
import com.example.moniepoint.keyvalue.system.util.KeyValueLogger;
import com.example.moniepoint.keyvalue.system.util.ResponseMessage;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Hidden
@RestController
@RequestMapping("/api/kv/store")
public class ReplicaController {
    private final KeyValueComponent keyValueComponent;
    KeyValueLogger kvLogger = new KeyValueLogger(Constant.REPLICA);

    public ReplicaController(KeyValueComponent keyValueComponent) {
        this.keyValueComponent = keyValueComponent;
    }


    @PutMapping("/replica/{key}")
    public String putReplica(@PathVariable String key, @RequestBody String value) throws IOException {
        kvLogger.key(key).value(value).message("/replica/...").info();
        keyValueComponent.put(key, value);
        kvLogger.message(ResponseMessage.REPLICA_SUCCESS).info();
        return ResponseMessage.REPLICA_SUCCESS;
    }

}
