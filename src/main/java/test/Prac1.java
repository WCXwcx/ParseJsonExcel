package test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wuchaoxiang on 2018/4/13.
 */
public class Prac1 {
    private static CountDownLatch latch = new CountDownLatch(1);

    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        }, "Thread-0");

        try {
            latch.await();
            System.out.println("---");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("++++");
    }

}
