import java.util.*;

public class Useful {
    public static LinkedHashMap<String, LabWork> lhmSort(LinkedHashMap<String, LabWork> collection) {
        if (collection.isEmpty()) {
            return collection;
        }

        List<Map.Entry<String, LabWork>> entries =
                new ArrayList<>(collection.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, LabWork>>() {
            public int compare(Map.Entry<String, LabWork> a, Map.Entry<String, LabWork> b){
                return a.getValue().compareTo(b.getValue());
            }
        });

        LinkedHashMap<String, LabWork> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<String, LabWork> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isOnlyTab(String str) {
        return str.matches("[\\s]+");
    }

    public static byte[] convertToByte(int in) {
        byte[] res = new byte[MyConstant.SIZE];

        for (int i = 0; i < res.length; i++) {
            res[res.length - i - 1] = (byte) (in % 10);
            in /= 10;
        }
        return res;
    }

    public static int convertToInt(byte[] in) {
        int res = 0;

        for (byte u: in) {
            res = 10 * res + u;
        }
        return res;
    }
}
