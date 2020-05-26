
public class Program{

    public static void main(String[] args) {
        int i = 0;
        while (true) {
            System.out.println(i);
            i += 1;
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
