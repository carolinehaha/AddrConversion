import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class C_Thread extends Thread {

    @Override
    public void run() {
        while (true) {
            String s = null;
            try {
                s = ConverseAddr.myQueue.poll(5000, TimeUnit.MILLISECONDS);
                if (s == null) {//当队列中没有元素时
                    ConverseAddr.cyclicBarrier.await();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Addr_Converse(s);
            //System.out.println("pop之后:myQueue.size()=" + ConverseAddr.myQueue.size());
        }
    }

    public void Addr_Converse(String string) {
        int dotIndex1 = string.indexOf(",");
        String addr_id = string.substring(1, dotIndex1 - 1);
        int dotIndex2 = string.indexOf(",", dotIndex1 + 1);
        String address = string.substring(dotIndex1 + 2, dotIndex2 - 1).trim();
        String location = ConverseAddr.httpURLConectionGET(address);
        String s = "\"" + addr_id + "\",\"" + address + "\",\"" + location + "\"";
        writeCsv("E:\\kx_kq_store1\\kx1000_output.csv",s);
    }

    public void writeCsv(String filepath, String addString) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true));
            writer.write(addString);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
