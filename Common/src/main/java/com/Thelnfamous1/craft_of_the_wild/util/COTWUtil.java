package com.Thelnfamous1.craft_of_the_wild.util;

import net.minecraft.util.Mth;

public class COTWUtil {

    public static int secondsToTicks(float seconds){
        return Mth.ceil(seconds * 20);
    }
}
