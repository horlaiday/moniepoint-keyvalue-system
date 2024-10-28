package com.example.moniepoint.keyvalue.system.controller;


import com.example.moniepoint.keyvalue.system.component.KeyValueComponent;
import com.example.moniepoint.keyvalue.system.exception.KeyValueException;
import com.example.moniepoint.keyvalue.system.util.Constant;
import com.example.moniepoint.keyvalue.system.util.KeyValueLogger;
import com.example.moniepoint.keyvalue.system.util.ResponseMessage;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/store")
public class KeyValueController {
    KeyValueLogger kvLogger = new KeyValueLogger(Constant.CONTROLLER);
    private final KeyValueComponent keyValueComponent;

    public KeyValueController(KeyValueComponent keyValueComponent) {
        this.keyValueComponent = keyValueComponent;
    }

    @PostMapping("/{key}")
    public String put(@PathVariable String key, @RequestBody String value) throws IOException {
        kvLogger.key(key).value(value).info();
        keyValueComponent.put(key, value);
        kvLogger.message("Stored successfully").info();
        return ResponseMessage.STORED;
    }

    @GetMapping("/{key}")
    public String read(@PathVariable String key) throws KeyValueException, IOException {
        kvLogger.key(key).info();
        String value = keyValueComponent.get(key);
        kvLogger.message(value).info();
        return value != null ? value : "Key not found";
    }

    @GetMapping("/range")
    public List<String> readKeyRange(@RequestParam String startKey, @RequestParam String endKey) throws KeyValueException, IOException {
        return keyValueComponent.readKeyRange(startKey, endKey);
    }

    @PostMapping("/batchPut")
    public String batchPut(@RequestBody Map<String, String> entries) throws KeyValueException, IOException {
        keyValueComponent.batchPut(entries);
        kvLogger.message("Batch insert successful").info();
        return ResponseMessage.BATCH_INSERTED;
    }

    @DeleteMapping("/{key}")
    public String delete(@PathVariable String key) throws KeyValueException, IOException {
        kvLogger.key(key).info();
        keyValueComponent.delete(key);
        kvLogger.message("Deleted successfully").info();
        return ResponseMessage.DELETED;
    }
}

