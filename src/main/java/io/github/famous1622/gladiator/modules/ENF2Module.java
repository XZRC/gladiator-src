package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.InventoryUtil;
import me.rigamortis.seppuku.api.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.math.RayTraceResult;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class ENF2Module extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();

    private static int ogslot = -1;
    private State currentState = State.FALL_CHECK;
    private static Timer timer = new Timer();

    public ENF2Module() {
        super("ENF2", new String[]{"ENF"}, "NoFallBypass2", "NONE", -1, ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        ogslot = -1;
        currentState = State.FALL_CHECK;
        super.onEnable();
    }

    @Listener
    public void onSend(EventSendPacket e) {
        currentState = currentState.onSend(e);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate e) {
        currentState = currentState.onUpdate(e);
    }

    @Listener
    public void onReceive(EventReceivePacket e) {
        currentState = currentState.onReceive(e);
    }

    public enum State {
        FALL_CHECK {
            @Override
            public State onSend(EventSendPacket e) {
                RayTraceResult result = mc.world.rayTraceBlocks(mc.player.getPositionVector(), mc.player.getPositionVector().add(0.0, -3.0, 0.0), true, true, false);
                if (e.getStage() == EventStageable.EventStage.PRE && e.getPacket() instanceof CPacketPlayer && mc.player.fallDistance >= 3.0f && result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);
                    if (slot != -1) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, mc.player);
                        ogslot = slot;
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                        return WAIT_FOR_ELYTRA_DEQUIP;
                    }
                    return this;
                }
                return this;
            }
        },
        WAIT_FOR_ELYTRA_DEQUIP {
            @Override
            public State onReceive(EventReceivePacket e) {
                if (e.getStage() == EventStageable.EventStage.POST && (e.getPacket() instanceof SPacketWindowItems || e.getPacket() instanceof SPacketSetSlot)) {
                    return REEQUIP_ELYTRA;
                }
                return this;
            }
        },
        REEQUIP_ELYTRA {
            public State onUpdate(EventPlayerUpdate e) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, ogslot, ClickType.SWAP, mc.player);
                mc.playerController.updateController();
                int slot = InventoryUtil.findStackInventory(Items.ELYTRA, true);
                if (slot == -1) {
                    Seppuku.INSTANCE.errorChat("Elytra not found after regain?");
                    return WAIT_FOR_NEXT_REQUIP;
                } else {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, ogslot, ClickType.SWAP, mc.player);
                    mc.playerController.updateController();
                    timer.reset();
                    return RESET_TIME;
                }
            }
        },
        WAIT_FOR_NEXT_REQUIP {
            public State onUpdate(EventPlayerUpdate e) {
                if (timer.passed(250)) {
                    return REEQUIP_ELYTRA;
                }
                return this;
            }
        },
        RESET_TIME {
            public State onUpdate(EventPlayerUpdate e) {
                if (mc.player.onGround || timer.passed(250)) {
                    mc.player.connection.sendPacket(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.BEDROCK), (short) 1337));
                    return FALL_CHECK;
                }
                return this;
            }
        };
        private static Minecraft mc = Minecraft.getMinecraft();

        public State onSend(EventSendPacket e) { return this; };
        public State onReceive(EventReceivePacket e) { return this; }
        public State onUpdate(EventPlayerUpdate e) { return this; }
    }
}

