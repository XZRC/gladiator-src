//package me.rigamortis.seppuku.impl.module.combat;
//
//import me.rigamortis.seppuku.api.module.Module;
//import me.zero.alpine.listener.EventHandler;
//import me.zeroeightsix.kami.event.KamiEvent;
//import me.zeroeightsix.kami.event.events.PacketEvent;
//import net.minecraft.item.ItemExpBottle;
//import net.minecraft.network.play.client.CPacketPlayer;
//import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
//@Module.Info(name = "FootEXP", category = Module.Category.MISC)
//public class FootEXP extends Module {
/**
 * Zim fix this you nigger
 */
//
//    @Override
//    protected void onEnable() {
//        super.onEnable();
//    }
//
//    @Override
//    protected void onDisable() {
//        super.onDisable();
//    }
//
//    @Override
//    public void onUpdate() {
//        super.onUpdate();
//        if(mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle){
//            if(mc.player.motionY == 0 || mc.player.motionX == 0 || mc.player.motionZ == 0){
//                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 90.0f, mc.player.onGround));
//            }
//        }
//    }
//
//    @EventHandler
//    public Listener<PacketEvent.Send> sendPacket = new Listener<>(event ->{
//        if(event.getEra() == KamiEvent.Era.PRE){
//            if(event.getPacket() instanceof CPacketPlayer.Rotation){
//                if(mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle){
//                    ((CPacketPlayer.Rotation) event.getPacket()).pitch = 90.0f;
//                }
//            }
//            if(event.getPacket() instanceof CPacketPlayer.PositionRotation){
//                if(mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle){
//                    ((CPacketPlayer.PositionRotation) event.getPacket()).pitch = 90.0f;
//                }
//            }
//        }
//    });
//}