package Spider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuchaoxiang on 2018/3/18.
 */
public class PracShell {
    public static void main(String[] args) {
        Process process = null;
        List<String> processList = new ArrayList<>();
        String shell = "";
        try {
            process = Runtime.getRuntime().exec(shell);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                processList.add(line);
                System.out.print(line);
            }
            System.out.println();

            BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "gb2312"));
            String errline = null;
            while ((errline = brError.readLine()) != null) {
                System.out.println(errline);
            }

            br.close();
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }

        for (String s : processList) {
            System.out.println(s);
        }
    }

    public static void main1(String[] args) {
        execShell();
    }

    public static void execShell() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ps -ef | grep java");
            System.out.println(process);
            int waitFor = process.waitFor();
            if (waitFor != 0) {
                System.out.println("exec shell error");
            } else {
                System.out.println("exec shell success");
            }
        } catch (Exception e) {
            System.out.println("exec shell failed");
        }
    }
}
