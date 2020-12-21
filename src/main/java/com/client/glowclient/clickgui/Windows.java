package com.client.glowclient.clickgui;


import me.rigamortis.seppuku.api.module.Module;

public class Windows {
   public static Window windowOther;
   public static Window windowWorld;
   public static Window windowRender;
   public static Window windowMovement;
   public static Window windowPlayer;
   public static Window windowCombat;
   public static Window windowExploit;
   public static Window windowChat;

   static {
      windowChat = new Window(1, 2, "Chat", Module.ModuleType.CHAT);
      windowWorld = new Window(104, 2, "World", Module.ModuleType.WORLD);
      windowMovement = new Window(206, 2, "Movement", Module.ModuleType.MOVEMENT);
      windowPlayer = new Window(308, 2, "Player", Module.ModuleType.PLAYER);
      windowRender = new Window(410, 2, "Render", Module.ModuleType.RENDER);
      windowOther = new Window(512, 2, "Misc", Module.ModuleType.MISC);
      windowCombat = new Window(614, 2, "Combat", Module.ModuleType.COMBAT);
      windowExploit = new Window(716, 2, "Exploit", Module.ModuleType.EXPLOIT);
   }
}
