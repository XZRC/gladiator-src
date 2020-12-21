package com.client.glowclient.utils.extra.pepsimod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface IConfigTranslator {
   void encode(JsonObject var1);

   void decode(String var1, JsonObject var2);

   String name();

   default int getInt(JsonObject object, String name, int def) {
      if (object.has(name)) {
         JsonElement element = object.get(name);
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().intValue();
         }
      }

      return def;
   }

   default short getShort(JsonObject object, String name, short def) {
      if (object.has(name)) {
         JsonElement element = object.get(name);
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().shortValue();
         }
      }

      return def;
   }

   default byte getByte(JsonObject object, String name, byte def) {
      if (object.has(name)) {
         JsonElement element = object.get(name);
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().byteValue();
         }
      }

      return def;
   }

   default long getLong(JsonObject object, String name, long def) {
      if (object.has(name)) {
         JsonElement element = object.get(name);
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().longValue();
         }
      }

      return def;
   }

   default float getFloat(JsonObject object, String name, float def) {
      if (object.has(name)) {
         JsonElement element = object.get(name);
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().floatValue();
         }
      }

      return def;
   }

   default double getDouble(JsonObject object, String name, double def) {
      if (object.has(name)) {
         JsonElement element = object.get(name);
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().doubleValue();
         }
      }

      return def;
   }

   default boolean getBoolean(JsonObject object, String name, boolean def) {
      if (object.has(name)) {
         JsonElement element = object.get(name);
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsJsonPrimitive().getAsBoolean();
         }
      }

      return def;
   }

   default String getString(JsonObject object, String name, String def) {
      if (object.has(name)) {
         JsonElement element = object.get(name);
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return element.getAsJsonPrimitive().getAsString();
         }
      }

      return def;
   }

   default JsonArray getArray(JsonObject object, String name, JsonArray def) {
      if (object.has(name)) {
         JsonElement element = object.get(name);
         if (element.isJsonArray()) {
            return element.getAsJsonArray();
         }
      }

      return def;
   }
}
