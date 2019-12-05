import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Writer {
    public static void main(String[] args) throws Exception {
        String MACHINE = "localhost";
        int PORT = 9000;
        try (
                Socket s = new Socket(MACHINE, PORT);
                Scanner sc = new Scanner(s.getInputStream());
                PrintWriter pw = new PrintWriter(s.getOutputStream());
        ) 
        {
            pw.println(args[0]);
            pw.flush();
            int id = Integer.parseInt(sc.nextLine());
            if(id > 1){
                System.out.println(id);
            }

            Scanner fc = new Scanner(System.in);
            String line;
            System.out.println("Add meg a koordinatakat szokozzel elvalasztva:");
            while (fc.hasNextLine()) {
                line = fc.nextLine();
                if(line.equals("quit")){
                    break;
                }else {
                    pw.println(line);
                    pw.flush();
                    System.out.println("Sent: " + line);
                    if(sc.hasNextLine()) {
                        String result = sc.nextLine();
                        String[] tmp = result.split(" ");
                        if( tmp[1].equals("Win") || tmp[2].equals("Win") ) {
                            System.out.println("Erkezett Servertol Ellenseg Lovese: " + tmp[0]);
                            System.out.println("Erkezett Servertol Ennyi hajom van: " + tmp[1]);
                            System.out.println("Erkezett Servertol Ellenseg ennyi hajoja van: " + tmp[2]);
                            break;
                        }
                        System.out.println("Erkezett Servertol Ellenseg Lovese: " + tmp[0]);
                        System.out.println("Erkezett Servertol Ennyi hajom van: " + tmp[1]);
                        System.out.println("Erkezett Servertol Ellenseg ennyi hajoja van: " + tmp[2]);
                    }
                }
            }
        }
    }
}
