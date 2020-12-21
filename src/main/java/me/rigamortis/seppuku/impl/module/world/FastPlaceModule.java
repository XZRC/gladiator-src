package me.rigamortis.seppuku.impl.module.world;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author Seth
 * 4/23/2019 @ 12:58 PM.
 */
public final class FastPlaceModule extends Module {

    public final Value<Boolean> XP = new Value<Boolean>("Exp", new String[]{"EXP"}, "Only activate while holding XP bottles.", false);
    public final Value<Boolean> Crystal = new Value<Boolean>("Crystal", new String[]{"FASTCRYSTAL"}, "Only activate while placing crystals.", false);
    public final Value<Boolean> Bow = new Value<Boolean>("Bow", new String[]{"BOW"}, "Bow spam", false);
    public final Value<Boolean> Eggs = new Value<Boolean>("Eggs", new String[]{"EGGS"}, "Only activate while throwing eggs", false);



    public FastPlaceModule() {
        super("FastUse", new String[]{"FU"}, "Removes place/use delay", "NONE", -1, ModuleType.PLAYER);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Minecraft.getMinecraft().rightClickDelayTimer = 6;
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            Minecraft.getMinecraft().rightClickDelayTimer = 0;
            if (this.XP.getValue()) {
                if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemExpBottle || Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() instanceof ItemExpBottle) {
                    Minecraft.getMinecraft().rightClickDelayTimer = 0;
                }
            }
            if (event.getStage() == EventStageable.EventStage.PRE) {
                if (this.Crystal.getValue()) {
                    if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal || Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
                        Minecraft.getMinecraft().rightClickDelayTimer = 0;
                    }
                }
                if (event.getStage() == EventStageable.EventStage.PRE) {
                    if (this.Eggs.getValue()) {
                        if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemEgg || Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() instanceof ItemEgg) {
                            Minecraft.getMinecraft().rightClickDelayTimer = 0;
                        }
                    }
                    if (this.Bow.getValue()) {
                        if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) {
                            if (mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
                                mc.player.stopActiveHand();
                            } else {
                                Minecraft.getMinecraft().rightClickDelayTimer = 0;
                            }
                        }
                    }
                }
            }
        }}}