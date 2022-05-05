import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Useful {
    public static ConcurrentHashMap<String, LabWork> lhmSort(ConcurrentHashMap<String, LabWork> collection) {
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

        ConcurrentHashMap<String, LabWork> sortedMap = new ConcurrentHashMap<>();

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

    public static String generatePassword(String password, String add) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] data = (password + add).getBytes("UTF-8");
            byte[] hashbytes = md.digest(data);
            return Arrays.toString(hashbytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
