package com.client.glowclient.utils;

public class SimpleTimer {
   private long timeStarted;

   public SimpleTimer(boolean startOnInit) {
      this.timeStarted = -1L;
      if (startOnInit) {
         this.start();
      }

   }

   public SimpleTimer() {
      this(false);
   }

   public void start() {
      this.timeStarted = System.currentTimeMillis();
   }

   public void stop() {
      this.timeStarted = -1L;
   }

   public boolean isStarted() {
      return this.timeStarted > -1L;
   }

   public boolean hasTimeElapsed(double time) {
      return this.isStarted() && time < (double)(System.currentTimeMillis() - this.timeStarted);
   }

   public boolean doesTimeEqual(double time) {
      return this.isStarted() && (time == (double)(System.currentTimeMillis() - this.timeStarted) || time == (double)(System.currentTimeMillis() - this.timeStarted + 50L));
   }
}
