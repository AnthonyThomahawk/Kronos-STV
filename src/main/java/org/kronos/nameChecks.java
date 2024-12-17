package org.kronos;

public class nameChecks {
    public static boolean isFileNameValid(String fileName) {
        return fileName.matches(".*[\"*:,|?<>/].*") || fileName.contains("\\");
    }

    public static boolean isPersonNameValid(String d) {
        return d.matches(".*[!@#$%^&*();'~`><?=-].*") || d.contains(",") || d.contains("\\") || d.contains("/") || d.contains("[") || d.contains("]");
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
