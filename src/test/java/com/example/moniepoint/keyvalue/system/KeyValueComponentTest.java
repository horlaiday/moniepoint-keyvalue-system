package com.example.moniepoint.keyvalue.system;

import com.example.moniepoint.keyvalue.system.component.KeyValueComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KeyValueComponentTest {

    @Autowired
    private KeyValueComponent keyValueComponent;

    @Test
    void testPutAndRead() throws IOException {
        keyValueComponent.put("key1", "value1");
        String value = keyValueComponent.get("key1");

        assertNotNull(value);
        assertEquals("value1", value);
    }

    @Test
    void testReadNonExistentKey() throws IOException {
        String value = keyValueComponent.get("nonExistentKey");
        assertNull(value);
    }

    @Test
    void testDelete() throws IOException {
        keyValueComponent.put("key1", "value1");
        keyValueComponent.delete("key1");

        assertNull(keyValueComponent.get("key1"));
    }

    @Test
    void testBatchPutAndRead() throws IOException{
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        List<String> values = Arrays.asList("value1", "value2", "value3");

      //  keyValueComponent.batchPut(Map<String, String>);

        assertEquals("value1", keyValueComponent.get("key1"));
        assertEquals("value2", keyValueComponent.get("key2"));
        assertEquals("value3", keyValueComponent.get("key3"));
    }

    @Test
    void testReadKeyRange() throws IOException {
        keyValueComponent.put("key1", "value1");
        keyValueComponent.put("key2", "value2");
        keyValueComponent.put("key3", "value3");

        List<String> range = keyValueComponent.readKeyRange("key1", "key3");

        assertEquals(3, range.size());
    }
}

