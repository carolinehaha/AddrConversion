import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//写csv文件线程
public class C_Thread extends Thread {

    private File file = new File("E:\\kx_kq_store1\\kx1000_output.csv");
    private  BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
    private long start = System.currentTimeMillis();
    AtomicInteger count = new AtomicInteger(0);

    public C_Thread() throws IOException {
        file.setWritable(true);
    }

    @Override
    public void run() {
        String s;
        try {
            while (true) {
                s = ConverseAddr.writeQueue.poll(1,TimeUnit.SECONDS);
                if (s == null) {//当队列中没有元素时
//                    ConverseAddr.cyclicBarrier.await();
                    break;
                }
                writer.write(s);
                count.getAndIncrement();
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("写入"+count.get()+"条数据。耗时：" + (end - start) + "ms");
        }

    }

}
