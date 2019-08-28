import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//读csv文件线程
public class P_Thread extends Thread {
    private File csv = new File("E:\\kx_kq_store1\\kx1000.csv");
    AtomicInteger count = new AtomicInteger(0);
    @Override
    public void run() {
        BufferedReader br = null;
        try {
            csv.setReadable(true);
            br = new BufferedReader(new FileReader(csv));
            String line ;
            while ((line = br.readLine()) != null) {
                ConverseAddr.readQueue.offer(line,1,TimeUnit.SECONDS);
                count.getAndIncrement();
            }
//            ConverseAddr.cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("读取数据"+count+"条");
        }

    }
}
