import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class PlayerClient implements AutoCloseable {

    Socket s;
    Scanner sc;
    PrintWriter pw;
    String name;
    static int clients = 0;

    public PlayerClient(ServerSocket ss) throws IOException {
        s = ss.accept();
        sc = new Scanner(s.getInputStream());
        pw = new PrintWriter(s.getOutputStream());
        setName(sc.nextLine());
        pw.println(++clients);
        pw.flush();
    }

    public boolean canHit() {
        return sc.hasNextLine();
    }

    public String read() {
        return sc.nextLine();
    }

    public void print(String s) {
        pw.println(s);
        pw.flush();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void close() throws IOException {
        sc.close();
        pw.close();
        s.close();
    }
}

class TorpedoMain {

    enum Type {
        WATER, SHIP_INTACT, SHIP_HIT, MISS
    }

    private final int boardSize;
    private Type[][] map;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: <ships_file> <ships_file2>");
            return;
        }
        TorpedoMain game1;
        TorpedoMain game2;
        try (Scanner ships = new Scanner(new File(args[0])); Scanner ships2 = new Scanner(new File(args[1]));) {
            game1 = new TorpedoMain(10, ships);
            game2 = new TorpedoMain(10, ships2);
        } catch (IOException e) {
            System.err.println("Error while reading ships!");
            e.printStackTrace();
            return;
        }

        int PORT = 9000;
        try (ServerSocket ss = new ServerSocket(PORT);) {
            try (PlayerClient cc1 = new PlayerClient(ss); PlayerClient cc2 = new PlayerClient(ss);) {
                while (true) {
                    String line;
                    if (cc1.canHit()) {
                        line = cc1.read();
                        String[] tmp = line.split(" ");
                        try {
                            System.out.println("Player2's table:(Player1 hits)");
                            game2.play(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));

                            cc2.print(game2.get(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1])).toString() + " "
                                    + game2.okShipsParts() + " " + game1.okShipsParts());
                        } catch (NumberFormatException e) {
                            System.out.println("Error");
                        }
                    }
                    if (cc2.canHit()) {
                        line = cc2.read();
                        String[] tmp = line.split(" ");
                        try {
                            System.out.println("Player1's table:(Player2 hits)");
                            game1.play(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));

                            cc1.print(game1.get(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1])).toString() + " "
                                    + game1.okShipsParts() + " " + game2.okShipsParts());
                        } catch (NumberFormatException e) {
                            System.out.println("Error");
                        }
                    }
                }
            }
        }
    }

    public TorpedoMain(int boardSize, Scanner ships) {
        this.boardSize = boardSize;
        map = new Type[boardSize][boardSize];
        for (int iy = 0; iy < boardSize; ++iy) {
            for (int ix = 0; ix < boardSize; ++ix) {
                set(ix, iy, Type.WATER);
            }
        }

        while (ships.hasNextLine()) {
            String line = ships.nextLine();
            Scanner lineScanner = new Scanner(line);
            String shipType = lineScanner.next();
            int x = lineScanner.nextInt();
            int y = lineScanner.nextInt();
            switch (shipType) {
            case "X":
                placeShip(x, y);
                break;
            case "I":
                placeShip(x, y - 1);
                placeShip(x, y);
                placeShip(x, y + 1);
                break;
            case "-":
                placeShip(x - 1, y);
                placeShip(x, y);
                placeShip(x + 1, y);
                break;
            }
        }
    }

    private void play(int x, int y) {
        hit(x, y);
        print(System.out);
        System.out.println();

    }

    private void print(PrintStream out) {
        for (int iy = 0; iy < boardSize; ++iy) {
            for (int ix = 0; ix < boardSize; ++ix) {
                out.print(visualise(get(ix, iy)));
            }
            out.println();
        }
    }

    private String okShipsParts() {
        int oknum = 0;
        for (int iy = 0; iy < boardSize; ++iy) {
            for (int ix = 0; ix < boardSize; ++ix) {
                if (visualise(get(ix, iy)) == 'X') {
                    oknum++;
                }
            }
        }
        if (oknum != 0)
            return Integer.toString(oknum);
        else
            return "Win";
    }

    private char visualise(Type type) {
        switch (type) {
        case WATER:
            return '.';
        case MISS:
            return '*';
        case SHIP_INTACT:
            return 'X';
        case SHIP_HIT:
            return '!';
        }
        return '?';
    }

    boolean onMap(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    Type get(int x, int y) {
        return map[y][x];
    }

    void set(int x, int y, Type t) {
        map[y][x] = t;
    }

    private void placeShip(int x, int y) {
        if (onMap(x, y)) {
            set(x, y, Type.SHIP_INTACT);
        }
    }

    private void hit(int x, int y) {
        if (onMap(x, y)) {
            switch (get(x, y)) {
            case SHIP_INTACT:
                set(x, y, Type.SHIP_HIT);
                break;
            case WATER:
                set(x, y, Type.MISS);
                break;
            }
        }
    }
}