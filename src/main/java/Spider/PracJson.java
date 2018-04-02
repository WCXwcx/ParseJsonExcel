package Spider;

import com.google.gson.*;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by wuchaoxiang on 2018/3/18.
 */
public class PracJson {

    private static final String FILE_PATH = "/Users/wuchaoxiang/db/db_";

    private static final String FILE_SUFFIX = ".json";
    
    private static final String ONE_H = "100_";

    private static final String FIVE_H = "500_";

    private static final String ONE_T = "1000_";

    private static final String THREE_T = "3000_";

    private static final List<String> ONE_H_APP = Arrays.asList(
            "openqrcode",
            "bscancqrcode",
            "zcmqrcodebiz",
            "paycloud",
            "paypos",
            "zcfreeway"
    );

    private static final List<String> FIVE_H_APP = Arrays.asList(
            "qrcodestat",
            "zcmopenusercenter",
            "paycloudcksettle",
            "posopenpay",
            "posfollow",
            "poszcmcoop",
            "posclear",
            "possettle",
            "posmarketing",
            "poscustomer",
            "zcmcrm"
    );

    private static final List<String> THREE_T_APP = Arrays.asList(
            "zcmcrmetl"
    );

    private static final String SQLLIST = "sqlList";

    public static Map<String, List<Integer>> readJsonByDay(int day) {
        Map<String, List<Integer>> map = new HashMap<>();
        File file = null;
        //解析100ms文件
        try {
            file = new File(FILE_PATH + ONE_H + day + FILE_SUFFIX);
            parseJson(file, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //解析500ms文件
        try {
            file = new File(FILE_PATH + FIVE_H + day + FILE_SUFFIX);
            parseJson(file, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //解析1000ms文件
        try {
            file = new File(FILE_PATH + ONE_T + day + FILE_SUFFIX);
            parseJson(file, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //解析3000ms文件
        try {
            file = new File(FILE_PATH + THREE_T + day + FILE_SUFFIX);
            parseJson(file, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(map.toString());
        return map;
    }

    public static void parseJson(File file, Map<String, List<Integer>> map) throws Exception {
        if (!file.exists()) {
            return;
        }
        // 创建json解析器
        JsonParser parser = new JsonParser();
        // 使用解析器解析json数据，返回值是JsonElement，强制转化为其子类JsonObject类型
        JsonObject object = (JsonObject) parser.parse(new FileReader(file));

        JsonObject data = (JsonObject) object.get("data");
        JsonObject cat = new JsonObject();
        if ((!file.getName().contains("1000")) && file.getName().contains("100")) {
            JsonObject smartPay = (JsonObject) data.get("智能支付");
            cat = (JsonObject) smartPay.get("招财猫");
        } else {
            JsonObject eachBgSlowQueryInfo = (JsonObject) data.get("eachBgSlowQueryInfo");
            JsonObject smartPay = (JsonObject) eachBgSlowQueryInfo.get("智能支付");
            cat = (JsonObject) smartPay.get("招财猫");
        }
        for (Map.Entry<String, JsonElement> jsonElementEntry : cat.entrySet()) {
            String key = jsonElementEntry.getKey();
            if ((!file.getName().contains("1000")) && file.getName().contains("100")) {
                if (ONE_H_APP.contains(key)) {
                    JsonObject db = (JsonObject) jsonElementEntry.getValue();
                    JsonArray jsonArray = db.getAsJsonArray(SQLLIST);
                    int count = getSlowSqlCount(jsonArray);
                    int num = getNumber(jsonArray);

                    if (!map.containsKey(key)) {
                        List<Integer> counts = Arrays.asList(count, 0, num);
                        map.put(key, counts);
                    } else {
                        List<Integer> counts = map.get(key);
                        counts.set(0, count);
                        counts.set(2, num);
                        map.put(key, counts);
                    }

                }
            } else if (file.getName().contains("500")) {
                if (THREE_T_APP.contains(key)) {
                    continue;
                } else {
                    JsonObject db = (JsonObject) jsonElementEntry.getValue();
                    JsonArray jsonArray = db.getAsJsonArray(SQLLIST);
                    int count = getSlowSqlCount(jsonArray);
                    int num = getNumber(jsonArray);

                    if (!map.containsKey(key)) {
                        if (ONE_H_APP.contains(key)) {
                            List<Integer> counts = Arrays.asList(0, count, 0);
                            map.put(key, counts);
                        } else if (FIVE_H_APP.contains(key)){
                            List<Integer> counts = Arrays.asList(count, 0, num);
                            map.put(key, counts);
                        }
                    } else {
                        if (ONE_H_APP.contains(key)) {
                            List<Integer> counts = map.get(key);
                            counts.set(1, count);
                            map.put(key, counts);
                        } else if (FIVE_H_APP.contains(key)) {
                            List<Integer> counts = map.get(key);
                            counts.set(0, count);
                            counts.set(2, num);
                            map.put(key, counts);
                        }
                    }
                }
            } else if (file.getName().contains("1000")) {
                if (ONE_H_APP.contains(key)) {
                    continue;
                } else {
                    JsonObject db = (JsonObject) jsonElementEntry.getValue();
                    JsonArray jsonArray = db.getAsJsonArray(SQLLIST);
                    int count = getSlowSqlCount(jsonArray);
                    int num = getNumber(jsonArray);

                    if (!map.containsKey(key)) {
                        if (THREE_T_APP.contains(key)) {
                            List<Integer> counts = Arrays.asList(count, 0, num);
                            map.put(key, counts);
                        } else if (FIVE_H_APP.contains(key)) {
                            List<Integer> counts = Arrays.asList(0, count, 0);
                            map.put(key, counts);
                        }
                    } else {
                        if (THREE_T_APP.contains(key)) {
                            List<Integer> counts = map.get(key);
                            counts.set(0, count);
                            counts.set(2, num);
                            map.put(key, counts);
                        } else if (FIVE_H_APP.contains(key)) {
                            List<Integer> counts = map.get(key);
                            counts.set(1, count);
                            map.put(key, counts);
                        }
                    }
                }
            } else if (file.getName().contains("3000")) {
                if (!THREE_T_APP.contains(key)) {
                    continue;
                } else {
                    JsonObject db = (JsonObject) jsonElementEntry.getValue();
                    JsonArray jsonArray = db.getAsJsonArray(SQLLIST);
                    int count = getSlowSqlCount(jsonArray);

                    if (!map.containsKey(key)) {
                        List<Integer> counts = Arrays.asList(0, count, 0);
                        map.put(key, counts);
                    } else {
                        List<Integer> counts = map.get(key);
                        counts.set(1, count);
                        map.put(key, counts);
                    }
                }
            }
        }
    }

    //所有慢查询数量
    public static int getSlowSqlCount(JsonArray jsonArray) {
        int count = 0;
        for (JsonElement jsonElement : jsonArray) {
            JsonObject object = (JsonObject) jsonElement;
            JsonPrimitive jsonPrimitive = object.getAsJsonPrimitive("queryCount");
            jsonPrimitive.getAsInt();
            count += jsonPrimitive.getAsInt();
        }
        return count;
    }

    //频率大于10次的SQL数量
    public static int getNumber(JsonArray jsonArray) {
        int count = 0;
        for (JsonElement jsonElement : jsonArray) {
            JsonObject object = (JsonObject) jsonElement;
            JsonPrimitive jsonPrimitive = object.getAsJsonPrimitive("queryCount");
            if (jsonPrimitive.getAsInt() > 10) {
                count++;
            }
        }
        return count;
    }

    public static int addDate(int date, long day) {
        String dateStr = String.valueOf(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate formatted = LocalDate.parse(dateStr, formatter);
        LocalDate target = formatted.plusDays(day);
        return Integer.valueOf(target.format(formatter));
    }

    public static Map<Integer, Map<String, List<Integer>>> readJsons(int day) {
        Map<Integer, Map<String, List<Integer>>> mapMap = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            Map<String, List<Integer>> map = readJsonByDay(day);
            mapMap.put(day, map);
            day = addDate(day, 1);
        }
//        System.out.println(mapMap);
        return mapMap;
    }

    public static void main(String[] args) {
        readJsons(20180319);
    }

}
