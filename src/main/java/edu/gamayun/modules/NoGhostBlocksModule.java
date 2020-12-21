package edu.gamayun.modules;

import me.rigamortis.seppuku.api.event.player.EventPlayerDamageBlock;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class NoGhostBlocksModule extends Module {
    Minecraft mc = Minecraft.getMinecraft();

    public NoGhostBlocksModule(){
        super("NoGhostBlocks", new String[]{"noghost"},"Prevents ghost blocks when placing", "NONE", -1, ModuleType.RENDER);
    }

    public void onEnable(){
        super.onEnable();
    }

    public void onDisable(){
        super.onDisable();
    }

    @Listener
    public void onBlockDamage(EventPlayerDamageBlock event){
        if(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock){
            mc.player.connection.sendPacket(new CPacketPlayerDigging());
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.fromAngle(-1.0)));
        }
    }
}
