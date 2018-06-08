package Spider;

import jxl.Cell;
import jxl.Workbook;
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
 * 主要的工作是根据work.xls配置的格式；
 * 从shell生成的各个json文件取数据并且组装，填入到work_3.xls中
 * 主要的是操作excel表格类
 */
public class PracExcel {

    //每周开始的第一天，每周一
    private static final int START_DAY = 20180528;

    public static void main(String[] args) {
        /**
         * 本地必须现有work.xls文件，配置好需要的内容格式
         */
        File file = new File("/Users/wuchaoxiang/work.xls");
        readExcel(file, START_DAY,  true);
        File file1 = new File("/Users/wuchaoxiang/work_2.xls");
        readExcel(file1, START_DAY,  false);
    }

    public static void readExcel(File file, int day, boolean flag) {
        PracJson pracJson = new PracJson();
        WritableWorkbook book = null;
        FileInputStream fileInputStream = null;
        try {

            //获取Excel文件
            fileInputStream = new FileInputStream(file);
            Workbook wb = Workbook.getWorkbook(fileInputStream);
            // 打开一个文件的副本，并且指定数据写回到原文件
            if (flag) {
                book = Workbook.createWorkbook(new File("/Users/wuchaoxiang/work_2.xls"), wb);
            } else {
                book = Workbook.createWorkbook(new File("/Users/wuchaoxiang/work_3.xls"), wb);
            }
            WritableSheet wsheet = book.getSheet(0);

            //得到第三列的数据 主要是为了获取key
            Cell[] cells = wsheet.getColumn(2);

            Map<Integer, Map<String, List<Integer>>> mapMap = PracJson.readJsons(day, flag);
            System.out.println(mapMap);

            for (int i = 0; i <= 6; i++) {
                Map<String, List<Integer>> map = mapMap.get(addDate(day, i));
                System.out.println(map);
                for (int j = 1; j <= 22; j++) { //原来是18个库，新添加了4个分库
                    String content = cells[j].getContents();
                    if (map.containsKey(content)) {
                        wsheet.addCell(new Label(i+3, j, map.get(content).get(0)+","+map.get(content).get(1)+","+map.get(content).get(2)));
                    } else {
                        if (flag) {
                            wsheet.addCell(new Label(i+3, j, "0,0,0"));
                        }
                    }
                }
            }
//            System.out.println(mapMap);
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
