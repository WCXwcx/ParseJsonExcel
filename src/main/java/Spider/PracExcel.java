package Spider;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Created by wuchaoxiang on 2018/3/18.
 */
public class PracExcel {

    //每周开始的第一天，每周一
    private static final int START_DAY = 20180326;

    public static void main(String[] args) {
        File file = new File("/Users/wuchaoxiang/work.xls");
        readExcel(file, START_DAY);
    }

    public static void readExcel(File file, int day) {
        PracJson pracJson = new PracJson();
        WritableWorkbook book = null;
        FileInputStream fileInputStream = null;
        try {

            //获取Excel文件
            fileInputStream = new FileInputStream(file);
            Workbook wb = Workbook.getWorkbook(fileInputStream);
            // 打开一个文件的副本，并且指定数据写回到原文件
            book = Workbook.createWorkbook(new File("/Users/wuchaoxiang/work_2.xls"), wb);

            WritableSheet wsheet = book.getSheet(0);

            //得到第三列的数据 主要是为了获取key
            Cell[] cells = wsheet.getColumn(2);

            Map<Integer, Map<String, List<Integer>>> mapMap = PracJson.readJsons(day);
            System.out.println(mapMap);

            for (int i = 0; i <= 6; i++) {
                Map<String, List<Integer>> map = mapMap.get(addDate(day, i));
                System.out.println(map);
                for (int j = 1; j <= 18; j++) {
                    String content = cells[j].getContents();
                    if (map.containsKey(content)) {
                        wsheet.addCell(new Label(i+3, j, map.get(content).get(0)+","+map.get(content).get(1)+","+map.get(content).get(2)));
                    } else {
                        wsheet.addCell(new Label(i+3, j, "0,0,0"));
                    }
                }
            }

            book.write();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (book != null) {
                    book.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int addDate(int date, long day) {
        String dateStr = String.valueOf(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate formatted = LocalDate.parse(dateStr, formatter);
        LocalDate target = formatted.plusDays(day);
        return Integer.valueOf(target.format(formatter));
    }


}
