package org.chc.ezim.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinYinUtil {

    public static String getPinyinInitial(String name) {
        if (name == null || name.isEmpty()) {
            return "#";
        }
        char firstChar = name.charAt(0);
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(firstChar);
        if (pinyinArray != null && pinyinArray.length > 0) {
            char initial = pinyinArray[0].charAt(0);
            if (Character.isLetter(initial)) {
                return String.valueOf(initial).toUpperCase();
            }
        } else if (Character.isLetter(firstChar)) {
            return String.valueOf(firstChar).toUpperCase();
        }
        return "#";
    }
}