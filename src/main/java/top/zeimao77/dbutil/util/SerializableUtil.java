package top.zeimao77.dbutil.util;

import java.io.*;

public class SerializableUtil {

    private SerializableUtil() {}

    public static void serializeObject(Object obj, File file) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(obj);
    }

    public static  <T> T DeserializeObject(File file,Class<T> clazz) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        return (T) ois.readObject();
    }


}
