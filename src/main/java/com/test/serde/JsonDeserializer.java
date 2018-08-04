package com.test.serde;


import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {

  private Gson gson = new Gson();
  private Class<T> deserializedClass;

  private Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

  public JsonDeserializer(Class<T> deserializedClass) {
    this.deserializedClass = deserializedClass;
  }

  public JsonDeserializer() {
  }

  @Override
  @SuppressWarnings("unchecked")
  public void configure(Map<String, ?> map, boolean b) {
    if (deserializedClass == null) {
      deserializedClass = (Class<T>) map.get("serializedClass");
    }
  }

  @Override
  public T deserialize(String s, byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    logger.debug("deserialize: " + new String(bytes));
    return gson.fromJson(new String(bytes), deserializedClass);
  }

  @Override
  public void close() {

  }
}