import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;

// =====================================================================================================================

class Board {
    Draw draw;
    Bomb bomb[];

    int stone[][];
    int save[][];
    int player = 0;
    int winner;
    String player_color[] = {"空", "白", "黒", "赤", "青"};

    int x, y;
    int w, h;
    int size;
    int margin;

    // コメント
    boolean canNot_put = false;
    boolean bomb_txt = false;
    boolean can_pass = false;
    boolean canNot_pass = false;
    boolean back_txt = false;
    boolean kill_txt[] = {false, false, false, false};

    int bomb_num;

    int color_count[] = {0, 0, 0, 0, 0};

    boolean back = false;

    boolean finish = false;

    Board(int x, int y, int width, int height, Draw d) {
        this.x = x;
        this.y = y;
        w = width;
        h = height;
        draw = d;

        stone = new int[x][y];
        save = new int[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                stone[i][j] = 0;
            }
        }
        // 初期配置
        stone[x / 2][y / 2 - 1] = 1;
        stone[x / 2 - 1][y / 2 - 2] = 1;
        stone[x / 2 - 1][y / 2] = 2;
        stone[x / 2][y / 2 + 1] = 2;
        stone[x / 2 - 1][y / 2 - 1] = 3;
        stone[x / 2 - 2][y / 2] = 3;
        stone[x / 2][y / 2] = 4;
        stone[x / 2 + 1][y / 2 - 1] = 4;

        for (int i = 0; i < x; i++) {
            System.arraycopy(stone[i], 0, save[i], 0, stone[i].length);
        }

        size = width / (x + 4);
        margin = size * 2;

        draw.setup(this);

        count_stone();

        // BOMB
        bomb_num = (int) (x / 2.5);
        bomb = new Bomb[bomb_num];
        for (int i = 0; i < bomb_num; i++) {
            bomb[i] = new Bomb(x, y, this);
        }
    }

    boolean pass() {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (stone[i][j] == 0) {
                    if (check(i, j, false)) {
                        return true;
                    }
                }
            }
        }

        player = (player + 1) % 4;

        return false;
    }

    void end() {
        int num[] = new int[4];
        int rank[] = {1, 2, 3, 4};
        int temp;
        int juni = 1;

        draw.repaint();
        count_stone();

        System.arraycopy(color_count, 1, num, 0, 4);

        for (int i = 0; i < 3; i++) {
            for (int j = 3; j > i; j--) {
                if (num[j] > num[i]) {
                    temp = num[i];
                    num[i] = num[j];
                    num[j] = temp;

                    temp = rank[i];
                    rank[i] = rank[j];
                    rank[j] = temp;
                }
            }
        }

        winner = rank[0];

        System.out.print("順位: <１位>: " + player_color[rank[0]] + " (" + num[0] + ")");
        for (int i = 1; i < 4; i++) {
            if (color_count[rank[i]] == color_count[rank[i - 1]]) {
                System.out.print(",  <" + juni + "位>: " + player_color[rank[i]] + " (" + num[i] + ")");
            } else {
                System.out.print(",  <" + (i + 1) + "位>: " + player_color[rank[i]] + " (" + num[i] + ")");
                juni++;
            }
        }

        finish = true;
    }

    boolean check(int x, int y, boolean change) {
        boolean ok = false;
        int py, px;
        int count;

        // 左 x-
        px = x - 1;
        py = y;
        count = 1;
        while (px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0 && stone[px][py] != player + 1 && stone[px][py] != 5) {
            px--;
            count++;
            if (!(px >= 0 && px < this.x && py < this.y && stone[px][py] != 0)) {
                break;
            }
            if (stone[px][py] == player + 1) {
                if (change) {
                    for (int i = 1; i < count; i++) {
                        stone[x - i][y] = player + 1;
                    }
                }
                ok = true;
                break;
            }
        }

        // 右 x+
        px = x + 1;
        py = y;
        count = 1;
        while (px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0 && stone[px][py] != player + 1 && stone[px][py] != 5) {
            px++;
            count++;
            if (!(px >= 0 && px < this.x && py < this.y && stone[px][py] != 0)) {
                break;
            }
            if (stone[px][py] == player + 1) {
                if (change) {
                    for (int i = 1; i < count; i++) {
                        stone[x + i][y] = player + 1;
                    }
                }
                ok = true;
                break;
            }
        }

        // 上 y-
        px = x;
        py = y - 1;
        count = 1;
        while (px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0 && stone[px][py] != player + 1 && stone[px][py] != 5) {
            py--;
            count++;
            if (!(px < this.x && py >= 0 && py < this.y && stone[px][py] != 0)) {
                break;
            }
            if (stone[px][py] == player + 1) {
                if (change) {
                    for (int i = 1; i < count; i++) {
                        stone[x][y - i] = player + 1;
                    }
                }
                ok = true;
                break;
            }
        }

        // 下 y+
        px = x;
        py = y + 1;
        count = 1;
        while (px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0 && stone[px][py] != player + 1 && stone[px][py] != 5) {
            py++;
            count++;
            if (!(px < this.x && py >= 0 && py < this.y && stone[px][py] != 0)) {
                break;
            }
            if (stone[px][py] == player + 1) {
                if (change) {
                    for (int i = 1; i < count; i++) {
                        stone[x][y + i] = player + 1;
                    }
                }
                ok = true;
                break;
            }
        }

        // 左上 x-, y-
        px = x - 1;
        py = y - 1;
        count = 1;
        while (px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0 && stone[px][py] != player + 1 && stone[px][py] != 5) {
            px--;
            py--;
            count++;
            if (!(px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0)) {
                break;
            }
            if (stone[px][py] == player + 1) {
                if (change) {
                    for (int i = 1; i < count; i++) {
                        stone[x - i][y - i] = player + 1;
                    }
                }
                ok = true;
                break;
            }
        }

        // 右下 x+, y+
        px = x + 1;
        py = y + 1;
        count = 1;
        while (px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0 && stone[px][py] != player + 1 && stone[px][py] != 5) {
            px++;
            py++;
            count++;
            if (!(px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0)) {
                break;
            }
            if (stone[px][py] == player + 1) {
                if (change) {
                    for (int i = 1; i < count; i++) {
                        stone[x + i][y + i] = player + 1;
                    }
                }
                ok = true;
                break;
            }
        }

        // 左下 x-, y+
        px = x - 1;
        py = y + 1;
        count = 1;
        while (px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0 && stone[px][py] != player + 1 && stone[px][py] != 5) {
            px--;
            py++;
            count++;
            if (!(px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0)) {
                break;
            }
            if (stone[px][py] == player + 1) {
                if (change) {
                    for (int i = 1; i < count; i++) {
                        stone[x - i][y + i] = player + 1;
                    }
                }
                ok = true;
                break;
            }
        }

        // 右上 x+, y-
        px = x + 1;
        py = y - 1;
        count = 1;
        while (px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0 && stone[px][py] != player + 1 && stone[px][py] != 5) {
            px++;
            py--;
            count++;
            if (!(px >= 0 && px < this.x && py >= 0 && py < this.y && stone[px][py] != 0)) {
                break;
            }
            if (stone[px][py] == player + 1) {
                if (change) {
                    for (int i = 1; i < count; i++) {
                        stone[x + i][y - i] = player + 1;
                    }
                }
                ok = true;
                break;
            }
        }

        return ok;
    }

    void count_stone() {
        for (int i = 0; i < 5; i++) {
            color_count[i] = 0;
        }
        for (int i = 0; i < this.x; i++) {
            for (int j = 0; j < this.y; j++) {
                switch (stone[i][j]) {
                    default:
                        color_count[stone[i][j]]++;
                        break;
                    case 5:
                        color_count[0]++;
                        break;
                }
            }
        }
    }
}

// =====================================================================================================================

class Bomb {
    Pos pos;
    private Board b;
    boolean active = true;

    class Pos {
        int x, y;

        Pos(int x, int y) {
            this.x = (int) (Math.random() * x);
            this.y = (int) (Math.random() * y);

            while ((this.x >= x / 2 - 1 && this.x <= x / 2 && this.y >= y / 2 - 1 && this.y <= y / 2) || (this.x == x / 2 - 1 && this.y == y / 2 - 2) || (this.x == x / 2 + 1 && this.y == y / 2 - 1) || (this.x == x / 2 && this.y == y / 2) || (this.x == x / 2 - 2 && this.y == y / 2)) {
                this.x = (int) (Math.random() * x);
                this.y = (int) (Math.random() * y);
            }
        }
    }

    Bomb(int x, int y, Board board) {
        pos = new Pos(x, y);
        b = board;
    }

    void burst() {
        System.out.println("! BOMB !");

        b.check(pos.x, pos.y, true);

        for (int i = 0; i < b.x; i++) {
            for (int j = 0; j < b.y; j++) {
                if (b.save[i][j] != b.stone[i][j]) {
                    b.stone[i][j] = 5;
                }
            }
        }

        b.bomb_num--;

        b.draw.repaint();

        active = false;
    }
}

// =====================================================================================================================

class Draw extends JPanel {
    private Board b;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        Color bg = new Color(30, 150, 0);
        Color dark_green = new Color(30, 100, 0);
        Color ivory = new Color(255, 240, 200);
        Color white_s = new Color(180, 180, 180);
        Color black_s = new Color(80, 80, 80);
        Color red_s = new Color(150, 0, 0);
        Color blue_s = new Color(0, 0, 150);

        BasicStroke bs = new BasicStroke(1);
        g2.setStroke(bs);

        setBackground(bg);

        // 終了時
        if (b.finish) {
            switch (b.winner) {
                case 1:
                    setBackground(Color.white);
                    g2.setColor(white_s);
                    break;
                case 2:
                    setBackground(Color.black);
                    g2.setColor(black_s);
                    break;
                case 3:
                    setBackground(Color.red);
                    g2.setColor(red_s);
                    break;
                case 4:
                    setBackground(Color.blue);
                    g2.setColor(blue_s);
                    break;
                default:
                    setBackground(Color.yellow);
                    g2.setColor(Color.orange);
                    break;
            }
            g2.setColor(bg);
            g2.fillRect(b.margin - b.size / 8, b.margin - b.size / 8, b.size * b.x + b.size / 4, b.size * b.y + b.size / 4);
        }

        g2.setColor(ivory);

        for (int px = b.margin; px <= b.size * b.x + b.margin; px += b.size) {
            g2.drawLine(px, b.margin, px, b.size * b.y + b.margin);
        }
        for (int py = b.margin; py <= b.size * b.y + b.margin; py += b.size) {
            g2.drawLine(b.margin, py, b.size * b.x + b.margin, py);
        }

        // プレーヤーラベル
        if (!b.finish) {
            g2.setFont(new Font("ＭＳ　ゴシック", Font.BOLD, (int) (b.size * 0.2)));
            g2.setColor(dark_green);
            g2.drawString("PLAYER", b.size / 8, b.h - (b.size * 6 / 11));
            switch (b.player) {
                case 0:
                    g2.setColor(Color.white);
                    g2.fillRect(0, b.h - (b.size / 2), b.w, b.size);
                    g2.setColor(white_s);
                    break;
                case 1:
                    g2.setColor(Color.black);
                    g2.fillRect(0, b.h - (b.size / 2), b.w, b.size);
                    g2.setColor(black_s);
                    break;
                case 2:
                    g2.setColor(Color.red);
                    g2.fillRect(0, b.h - (b.size / 2), b.w, b.size);
                    g2.setColor(red_s);
                    break;
                case 3:
                    g2.setColor(Color.blue);
                    g2.fillRect(0, b.h - (b.size / 2), b.w, b.size);
                    g2.setColor(blue_s);
                    break;
                default:
                    break;
            }
        }

        // パスボタン
        if (!b.finish) {
            g2.setFont(new Font("ＭＳ　ゴシック", Font.BOLD, (int) (b.size * 0.7)));
            g2.setColor(dark_green);
            g2.drawRect(b.w / 2 - b.margin, 0, b.margin * 2, (int) (b.size * 1.3));
            drawStringCenter(g2, "パス", b.w / 2, (int) (b.size * 0.7));
        }

        // 戻るボタン
        if (!b.finish) {
            if (b.back) {
                g2.setColor(dark_green);
                g2.drawRect(0, 0, b.size, b.size * 7 / 8);
                int shape_x[] = {b.size / 8, b.size * 2 / 5, b.size * 2 / 5, b.size * 3 / 5, b.size * 3 / 5, b.size * 5 / 6, b.size * 5 / 6, b.size * 2 / 5, b.size * 2 / 5, b.size / 8};
                int shape_y[] = {b.size / 2, b.size / 5, b.size / 3, b.size / 3, b.size / 8, b.size / 8, b.size * 2 / 3, b.size * 2 / 3, b.size * 4 / 5, b.size / 2};
                Polygon back = new Polygon(shape_x, shape_y, shape_x.length);
                g2.fill(back);
            }
        }

        // BOMB_COUNT
        if (!b.finish) {
            g2.setColor(dark_green);
            g2.setFont(new Font("ＭＳ　ゴシック", Font.BOLD, b.size / 4));
            g2.drawString("BOMB", b.w - b.margin + (b.size / 2), b.size * 2 / 3);
            g2.setFont(new Font("ＭＳ　ゴシック", Font.BOLD, b.size));
            g2.drawString("" + b.bomb_num, b.w - b.margin + (b.size / 2), b.size * 3 / 2);
        }

        // COLOR_COUNT
        if (!b.finish) {
            g2.setColor(dark_green);
            g2.setFont(new Font("ＭＳ　ゴシック", Font.BOLD, b.size / 2));
            g2.drawString("残: " + b.color_count[0], b.size / 6, b.size * 3);
        }

        g2.setFont(new Font("ＭＳ　ゴシック", Font.BOLD, b.size / 2));
        g2.setColor(white_s);
        g2.drawString("白: " + b.color_count[1], b.size / 6, b.size * 5);
        g2.setColor(black_s);
        g2.drawString("黒: " + b.color_count[2], b.size / 6, b.size * 6);
        g2.setColor(red_s);
        g2.drawString("赤: " + b.color_count[3], b.size / 6, b.size * 7);
        g2.setColor(blue_s);
        g2.drawString("青: " + b.color_count[4], b.size / 6, b.size * 8);

        if (!b.finish) {
            g2.setColor(dark_green);
            g2.setFont(new Font("ＭＳ　ゴシック", Font.BOLD, b.size / 2));
            if (b.canNot_put) {
                drawStringCenter(g2, "×置けません×", b.w / 2, b.h - b.size * 4 / 3);
            }
            if (b.bomb_txt) {
                drawStringCenter(g2, "！BOMB！", b.w / 2, b.h - b.size * 4 / 3);
            }
            if (b.can_pass) {
                drawStringCenter(g2, "パスしました", b.w / 2, b.h - b.size * 4 / 3);
            }
            if (b.canNot_pass) {
                drawStringCenter(g2, "置ける場所があります！", b.w / 2, b.h - b.size * 4 / 3);
            }
            if (b.back_txt) {
                drawStringCenter(g2, "１人前に戻りました", b.w / 2, b.h - b.size * 4 / 3);
            }
            for (int i = 0; i < 4; i++) {
                if (b.kill_txt[i]) {
                    drawStringCenter(g2, b.player_color[i + 1] + "の石が無くなってしまいます", b.w / 2, b.h - b.size * 4 / 3);
                }
            }
        }


        for (int x = 0; x < b.x; x++) {
            for (int y = 0; y < b.y; y++) {
                if (b.stone[x][y] != 0) {
                    switch (b.stone[x][y]) {
                        case 1:
                            g2.setColor(Color.white);
                            g2.fillOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            g2.setColor(white_s);
                            g2.drawOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            break;
                        case 2:
                            g2.setColor(Color.black);
                            g2.fillOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            g2.setColor(black_s);
                            g2.drawOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            break;
                        case 3:
                            g2.setColor(Color.red);
                            g2.fillOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            g2.setColor(red_s);
                            g2.drawOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            break;
                        case 4:
                            g2.setColor(Color.blue);
                            g2.fillOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            g2.setColor(blue_s);
                            g2.drawOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            break;
                        case 5:
                            g2.setColor(bg);
                            g2.fillOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            g2.setColor(red_s);
                            g2.drawOval(b.margin + x * b.size + 2, b.margin + y * b.size + 2, b.size - 4, b.size - 4);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void drawStringCenter(Graphics g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle rectText = fm.getStringBounds(text, g).getBounds();
        x = x - rectText.width / 2;
        y = y - rectText.height / 2 + fm.getMaxAscent();

        g.drawString(text, x, y);
    }

    void setup(Board b) {
        this.b = b;
    }
}

// =====================================================================================================================

class MainFrame extends JFrame {
    MainFrame(String title, int x, int y) {
        super(title);

        Draw draw = new Draw();
        Board b;

        int w = (int)(Toolkit.getDefaultToolkit().getScreenSize().height * 0.8);
        int h = (int)(Toolkit.getDefaultToolkit().getScreenSize().height * 0.8);

        b = new Board(x, y, w, h, draw);

        Container cp = getContentPane();
        cp.add(draw);

        // マウスリスナー
        MouseCheck ms = new MouseCheck(b);
        draw.addMouseListener(ms);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dim = new Dimension(w, h);
        cp.setPreferredSize(dim);
        pack();
        setVisible(true);

        //確認用出力
        System.out.println(w + " x " + h + " pixelのウインドウを作りました.");
    }
}

// =====================================================================================================================

class SoundPlay implements Runnable {
    private Clip clip = null;

    SoundPlay(String filename) {
        File file = new File(filename);
        AudioFormat format;
        DataLine.Info info;

        try {
            format = AudioSystem.getAudioFileFormat(file).getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(AudioSystem.getAudioInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        clip.setFramePosition(0);
        clip.start();
    }
}

// =====================================================================================================================

class MouseCheck implements MouseListener {
    private Board b;

    private SoundPlay put_sound = new SoundPlay("put.wav");
    private SoundPlay bomb_sound = new SoundPlay("bomb.wav");

    private int pass_count = 0;

    MouseCheck(Board board) {
        b = board;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX(), y = e.getY(), button = e.getButton();
        int px, py;
        int color;
        boolean can;

        b.canNot_put = false;
        b.bomb_txt = false;
        b.can_pass = false;
        b.canNot_pass = false;
        b.back_txt = false;
        for (int i = 0; i < 4; i++) {
            b.kill_txt[i] = false;
        }

        if (button != MouseEvent.BUTTON1) {
            return;
        }

        if (b.finish) {
            return;
        }

        // パス
        if (x >= b.w / 2 - b.margin && x <= b.w / 2 + b.margin && y > 0 && y <= (int) (b.size * 1.3)) {
            can = b.pass();
            if (!can) {
                b.can_pass = true;
                System.out.println("パスしました");
                b.back = false;
                pass_count++;
                if (pass_count >= 4) {
                    b.end();
                }
                b.draw.repaint();
                return;
            } else {
                b.canNot_pass = true;
                System.out.println("置ける場所があります");
                b.draw.repaint();
            }
        }

        // 戻る
        if (x > 0 && x < b.size && y > 0 && y < b.size * 7 / 8) {
            if (b.back) {
                for (int i = 0; i < b.x; i++) {
                    System.arraycopy(b.save[i], 0, b.stone[i], 0, b.save[i].length);
                }
                b.player = (b.player + 3) % 4;
                b.back_txt = true;
                System.out.println("一人前に戻りました");

                b.count_stone();
                b.back = false;
                b.draw.repaint();
            }
        }

        //マージンを引く
        x -= b.margin;
        y -= b.margin;

        if (x < 0 || y < 0) {
            return;
        }

        if (x == 0 || y == 0) {
            return;
        }

        //マス目に変換する
        px = x / b.size;
        py = y / b.size;

        if (px < 0 || px >= b.x || py < 0 || py >= b.y) {
            return;
        }

        if (b.stone[px][py] != 0 && b.stone[px][py] != 5) {
            return;
        }

        color = b.stone[px][py];

        System.out.print("横" + (px + 1) + "マス目,");
        System.out.println("縦" + (py + 1) + "マス目です");

        for (int i = 0; i < b.x; i++) {
            System.arraycopy(b.stone[i], 0, b.save[i], 0, b.stone[i].length);
        }

        b.stone[px][py] = b.player + 1;

        if (!b.check(px, py, false)) {
            b.canNot_put = true;
            System.out.println("×置けません×");
            b.stone[px][py] = color;
            b.draw.repaint();
            return;
        }

        // kill
        b.check(px, py, true);
        b.count_stone();

        for (int i = 1; i <= 4; i++) {
            if (b.color_count[i] == 0) {
                b.kill_txt[i - 1] = true;
                System.out.println(b.player_color[i] + "の石が無くなってしまいます！");
                b.draw.repaint();

                for (int j = 0; j < b.x; j++) {
                    System.arraycopy(b.save[j], 0, b.stone[j], 0, b.save[j].length);
                }

                return;
            }
        }

        pass_count = 0;

        // BOMB
        for (int i = 0; i < (int) (b.x / 2.5); i++) {
            if (px == b.bomb[i].pos.x && py == b.bomb[i].pos.y && b.bomb[i].active) {
                b.bomb[i].burst();

                b.back = false;
                b.player = (b.player + 1) % 4;

                bomb_sound.run();

                b.bomb_txt = true;

                return;
            }
        }

        b.check(px, py, true);
        b.count_stone();

        System.out.println("◯置きました◯");
        put_sound.run();

        b.back = true;

        if (b.color_count[0] == 0) {
            b.end();
        }

        b.draw.repaint();

        b.player = (b.player + 1) % 4;
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }
}

// =====================================================================================================================

public class Othello {
    public static void main(String[] args) {

        int scale = 8;

        new MainFrame("オセロ", scale, scale);
    }
}

