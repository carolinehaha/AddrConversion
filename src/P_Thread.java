import java.io.*;
import java.util.concurrent.TimeUnit;

public class P_Thread extends Thread {
    @Override
    public void run() {
        try {
            File csv = new File("E:\\kx_kq_store1\\kx1000.csv");
            csv.setReadable(true);
            csv.setWritable(true);
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(csv));
            String line = "";
            while ((line = br.readLine()) != null) {
                ConverseAddr.myQueue.offer(line, 1000,TimeUnit.MILLISECONDS );
                //System.out.println("push" + line + "之后:myQueue.size()=" + ConverseAddr.myQueue.size());
            }
            ConverseAddr.cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
