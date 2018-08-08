package cn.vpfinance.vpjr.util;

import com.google.gson.Gson;

/**
 * gson解析工具类
 */
public class GsonUtil {
    /**
     * gson 单例
     */
    private static Gson gson;

    /**
     * 获取Gson
     *
     * @return
     */
    public static Gson getGson() {
        if (null != gson) return gson;
        gson = new Gson();
        return gson;
    }

    /**
     * json解析工具方法
     *
     * @param json  json数据
     * @param clazz javabean
     * @param <T>   javabean 泛型
     * @return
     */
    public static <T> T modelParser(String json, Class<T> clazz) {
        try {
            T model = getGson().fromJson(json, clazz);
            return model;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
