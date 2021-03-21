package com.jbyerline.stats.utils

class StringUtils {

    static int lineCount(String str){
        String[] lines = str.split("\r\n|\r|\n")
        return  lines.length
    }
}
