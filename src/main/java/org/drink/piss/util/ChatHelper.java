package org.drink.piss.util;

import java.util.Random;

/*
 * Exception performing whole class analysis ignored.
 */
public class ChatHelper {

    private static Random rand = new Random();

    public static String appendChatSuffix(String message, String suffix) {
        message = ChatHelper.cropMaxLengthMessage((String)message, (int)suffix.length());
        message = message + suffix;
        return ChatHelper.cropMaxLengthMessage((String)message);
    }

    public static String generateRandomHexSuffix(int n) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append(Integer.toHexString((rand.nextInt() + 11) * rand.nextInt()), 0, n);
        sb.append(']');
        return sb.toString();
    }

    public static String cropMaxLengthMessage(String s, int i) {
        if (s.length() <= 255 - i) return s;
        return s.substring(0, 255 - i);
    }

    public static String cropMaxLengthMessage(String s) {
        return ChatHelper.cropMaxLengthMessage((String)s, (int)0);
    }
}
