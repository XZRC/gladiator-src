package com.client.glowclient.utils.extra.pepsimod;

import com.google.gson.JsonObject;

public class TargettingTranslator implements IConfigTranslator {
   public static final TargettingTranslator INSTANCE = new TargettingTranslator();
   public boolean players = false;
   public boolean animals = false;
   public boolean monsters = false;
   public boolean golems = false;
   public boolean sleeping = false;
   public boolean invisible = false;
   public boolean teams = false;
   public boolean friends = false;
   public boolean through_walls = false;
   public boolean use_cooldown = false;
   public boolean silent = false;
   public boolean rotate = false;
   public TargettingTranslator.TargetBone targetBone;
   public float fov;
   public float reach;
   public int delay;

   private TargettingTranslator() {
      this.targetBone = TargettingTranslator.TargetBone.FEET;
      this.fov = 360.0F;
      this.reach = 4.25F;
      this.delay = 20;
   }

   public void encode(JsonObject json) {
      json.addProperty("players", this.players);
      json.addProperty("animals", this.animals);
      json.addProperty("monsters", this.monsters);
      json.addProperty("golems", this.golems);
      json.addProperty("sleeping", this.sleeping);
      json.addProperty("invisible", this.invisible);
      json.addProperty("teams", this.teams);
      json.addProperty("friends", this.friends);
      json.addProperty("through_walls", this.through_walls);
      json.addProperty("use_cooldown", this.use_cooldown);
      json.addProperty("silent", this.silent);
      json.addProperty("rotate", this.rotate);
      json.addProperty("bone", this.targetBone.ordinal());
      json.addProperty("fov", this.fov);
      json.addProperty("reach", this.reach);
      json.addProperty("glideSpeed", this.delay);
   }

   public void decode(String fieldName, JsonObject json) {
      this.players = this.getBoolean(json, "players", this.players);
      this.animals = this.getBoolean(json, "animals", this.animals);
      this.monsters = this.getBoolean(json, "monsters", this.monsters);
      this.golems = this.getBoolean(json, "golems", this.golems);
      this.sleeping = this.getBoolean(json, "sleeping", this.sleeping);
      this.invisible = this.getBoolean(json, "invisible", this.invisible);
      this.teams = this.getBoolean(json, "teams", this.teams);
      this.friends = this.getBoolean(json, "friends", this.friends);
      this.through_walls = this.getBoolean(json, "through_walls", this.through_walls);
      this.use_cooldown = this.getBoolean(json, "use_cooldown", this.use_cooldown);
      this.silent = this.getBoolean(json, "silent", this.silent);
      this.rotate = this.getBoolean(json, "rotate", this.rotate);
      this.targetBone = TargettingTranslator.TargetBone.getBone(this.getInt(json, "bone", this.targetBone.ordinal()));
      this.fov = this.getFloat(json, "fov", this.fov);
      this.reach = this.getFloat(json, "reach", this.reach);
      this.delay = this.getInt(json, "glideSpeed", this.delay);
   }

   public String name() {
      return "targetting";
   }

   public static enum TargetBone {
      HEAD,
      FEET,
      MIDDLE;

      public static TargettingTranslator.TargetBone getBone(int id) {
         return values()[id];
      }
   }
}
