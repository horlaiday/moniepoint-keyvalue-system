package com.example.moniepoint.keyvalue.system.component;


import com.example.moniepoint.keyvalue.system.configuration.ApplicationConfigs;
import com.example.moniepoint.keyvalue.system.exception.KeyValueException;
import com.example.moniepoint.keyvalue.system.util.Constant;
import com.example.moniepoint.keyvalue.system.util.KeyValueLogger;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class KeyValueComponent {

    private final Path dataDir = Paths.get(Constant.DATA_DIR);
    private final ConcurrentHashMap<String, Long> index = new ConcurrentHashMap<>();
    private final ApplicationConfigs applicationConfigs;
    private final RestTemplate restTemplate = new RestTemplate();
    private RandomAccessFile dataFile;
    private long writeOffset = 0;
    KeyValueLogger kvLogger = new KeyValueLogger(Constant.COMPONENT);


    /**
     * @Constructor.
     *
     * @param applicationConfigs this contains variables (system settings)
     */
    public KeyValueComponent(ApplicationConfigs applicationConfigs) throws IOException {
        this.applicationConfigs = applicationConfigs;
        Files.createDirectories(dataDir);
        this.dataFile = new RandomAccessFile(dataDir.resolve(Constant.DATA_STORE).toString(), Constant.DATA_STORE_MODE);
        loadIndex();
    }

    private void loadIndex() throws IOException {
        dataFile.seek(0);
        long offset = 0;
        while (dataFile.getFilePointer() < dataFile.length()) {
            int keyLength = dataFile.readInt();
            int valueLength = dataFile.readInt();
            byte[] keyBytes = new byte[keyLength];
            dataFile.readFully(keyBytes);
            String key = new String(keyBytes);
            index.put(key, offset);
            dataFile.seek(dataFile.getFilePointer() + valueLength);
            offset = dataFile.getFilePointer();
        }
        writeOffset = offset;
    }

    /**
     * Adds an entry to this datafile.
     * @param key the key with which the specified value is to be associated
     * @param value a value to be associated with the specified key
     */
    public void put(String key, String value) throws IOException {
        byte[] keyBytes = key.getBytes();
        byte[] valueBytes = value.getBytes();
        dataFile.seek(writeOffset);
        dataFile.writeInt(keyBytes.length);
        dataFile.writeInt(valueBytes.length);
        dataFile.write(keyBytes);
        dataFile.write(valueBytes);
        index.put(key, writeOffset);
        kvLogger.message(String.valueOf(index)).info();
        writeOffset = dataFile.getFilePointer();

       if(applicationConfigs.isReplicateData()) // default value value is false
            replicateDataAsync(key, value);
    }

    /**
     * get file data stored in key-value on the datafile
     * @param key the key with which the specified value is to be associated
     * @return string of stored file
     */
    public String get(String key) throws KeyValueException, IOException {
        Long offset = index.get(key);
        if (offset == null) {
            return null;
        }
        dataFile.seek(offset);
        int keyLength = dataFile.readInt();
        int valueLength = dataFile.readInt();
        dataFile.skipBytes(keyLength);
        byte[] valueBytes = new byte[valueLength];
        dataFile.readFully(valueBytes);
        return new String(valueBytes);
    }


    /**
     * Removes a particular value from this datafile.
     * @param key key with which the specified value is to be removed
     */
    public void delete(String key) {
        kvLogger.key(key).info();
        index.remove(key);
        kvLogger.message(String.valueOf(index)).info();
    }

    /**
     * retrieve values for a range of keys.
     * @param startKey the 0th position values to be read
     * @param endKey the nth position values to end the range
     * @return values of file as a String
     * @throws IOException if data not present
     */
    public List<String> readKeyRange(String startKey, String endKey) throws IOException {
        kvLogger.message("readKeyRange from : "+startKey+" to "+endKey).info();
        List<String> values = new ArrayList<>();
        for (String key : index.keySet()) {
            kvLogger.key(key).info();
            if (key.compareTo(startKey) >= 0 && key.compareTo(endKey) <= 0) {
                values.add(get(key));
            }
        }
        return values;
    }

    public void batchPut(Map<String, String> entries) throws IOException {
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            kvLogger.key(entry.getKey()).value(entry.getValue()).info();
            put(entry.getKey(), entry.getValue());
        }
    }

    @Async
    public CompletableFuture<Void> replicateDataAsync(String key, String value) {
        for (String node : applicationConfigs.getFollowerNodes()) {
            replicateToNode(node, key, value);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * if replicate is replicateData on the application config is set to true .
     * @param node is the location address of replica server
     * @param key the key with which the specified value is to be associated
     * @param value a value to be associated with the specified key
     *
     * @throws IOException if connection fails
     */
    private void replicateToNode(String node, String key, String value) {
        try {
            restTemplate.postForObject(node + "/api/kv/store/replica/" + key, value, String.class);
            kvLogger.message("Data replicated to " + node + " for key: " + key).debug();
        } catch (Exception e) {
            kvLogger.message("Failed to replicate to node: " + node + " for key: " + key + " - Retrying...").error();
            retryReplication(node, key, value);
        }
    }


    /**
     * this is to retry replication process that failed previously.
     * @param node is the location address of replica server
     * @param key the key with which the specified value is to be associated
     * @param value a value to be associated with the specified key
     *
     * @throws IOException if connection fails
     */
    private void retryReplication(String node, String key, String value) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                restTemplate.postForObject(node + "/api/kv/store/replica/" + key, value, String.class);
                kvLogger.message("Retry successful to " + node + " for key: " + key).debug();
                return;
            } catch (Exception e) {
                kvLogger.message("Retry " + (i + 1) + " failed for node: " + node + " key: " + key).error();
            }
        }
    }

    @PreDestroy
    public void close() throws IOException {
        dataFile.close();
    }
}

