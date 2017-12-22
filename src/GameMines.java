import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

class GameMines extends JFrame { // сапер
    final String TITLE_OF_PROGRAM = "Minesweepeer";
    final String SIGN_OF_FLAG = "!";
    final int BLOCK_SIZE = 30; // размер одного блока
    static int FIELD_SIZE; // размер поля
    final int FIELD_DX = 6;
    final int FIELD_DY = 28 + 17;
    final int START_LOCATION = 100;
    final int MOUSE_BUTTON_LEFT = 1; // для mouse listener
    final int MOUSE_BUTTON_RIGHT = 3;
    static int NUMBER_OF_MINES; // количество бомб
    final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0, 0x0000FF, 0x008000, 0xFF0000};
    static Cell[][] field;
    Random random = new Random();
    int countOpenedCells; // количество открытых клеток
    static boolean youWon, bangMine; // флаги победителя и взорвавшегося
    int bangX, bangY; // for fix the coordinates of the explosion

    GameMines(int field_size, int numberOfmines) {
        FIELD_SIZE = field_size;
        NUMBER_OF_MINES = numberOfmines;
        field = new Cell[FIELD_SIZE][FIELD_SIZE];
        setTitle(TITLE_OF_PROGRAM);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(START_LOCATION, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_DX, 100 + FIELD_SIZE * BLOCK_SIZE + FIELD_DY);
        setResizable(false);

        JButton B0 = new JButton("Help"); // создание кнопок
        JButton B1 = new JButton("Auto");
        JButton B2 = new JButton("AutoAll");
        JPanel pNorth = new JPanel();
        pNorth.add(B0); // добавление на JPanel
        pNorth.add(B1);
        pNorth.add(B2);
        add(pNorth, BorderLayout.NORTH);
        SolverMS solverMS = new SolverMS();
        JFrame fcanvas1 = new JFrame(); // создание новой JFrame для Help

        fcanvas1.setTitle("Help");

        fcanvas1.setBounds(START_LOCATION + 400, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_DX, 100 + FIELD_SIZE * BLOCK_SIZE + FIELD_DY);
        fcanvas1.setResizable(false); // пользователь не сможет изменить
        fcanvas1.add(solverMS);

        // добавление слушателей на кнопки
        B0.addActionListener(e -> {
            solverMS.initField1();
            solverMS.count();
            solverMS.repaint();
            fcanvas1.setVisible(true);
        });
        B2.addActionListener(e -> {
            solverMS.initField1();
            solverMS.count();
            solverMS.repaint();
            while (!youWon && !bangMine) {
                Point p = solverMS.getFirstCell();
                if (solverMS.getver(p.x, p.y) == 100) break;
                else {
                    if (solverMS.getver(p.x, p.y) != 0)
                        p = solverMS.getRandomCell(solverMS.getver(p.x, p.y));
                    System.out.println(p.toString());
                    openCells(p.x, p.y);
                    repaint();
                    solverMS.initField1();
                    solverMS.count();
                    youWon = countOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_OF_MINES;

                    solverMS.repaint();
                }
            }
        });
        B1.addActionListener(e -> {
            solverMS.initField1();
            solverMS.count();
            solverMS.repaint();
            while (!youWon && !bangMine) {
                Point p = solverMS.getFirstCell();
                if (solverMS.getver(p.x, p.y) != 0) break;
                else {
                    openCells(p.x, p.y);
                    repaint();
                    solverMS.initField1();
                    solverMS.count();
                    youWon = countOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_OF_MINES;

                    solverMS.repaint();
                }
            }
        });
        KeyListener Kl = new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
            }
            public void keyReleased(KeyEvent e) {
            }
            public void keyTyped(KeyEvent e) {
            }
        };
        fcanvas1.addKeyListener(Kl);



        final Canvas canvas = new Canvas();
        canvas.setBackground(Color.white);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int x = e.getX() / BLOCK_SIZE;
                int y = e.getY() / BLOCK_SIZE;
                if (!bangMine && !youWon) {
                    if (e.getButton() == MOUSE_BUTTON_LEFT) // левая клавиша
                        if (field[y][x].isNotOpen()) {
                            openCells(x, y);
                            youWon = countOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_OF_MINES;
                            if (bangMine) {
                                bangX = x;
                                bangY = y;
                            }
                        }
                    if (e.getButton() == MOUSE_BUTTON_RIGHT) field[y][x].inverseFlag(); // правая клавиша
                    canvas.repaint(); // перерисовка сапера
                    solverMS.initField1(); // пересоздание поле
                    solverMS.count(); // расчет поля
                    solverMS.repaint(); //перерисовка решателя
                }
            }
        });
        add(BorderLayout.CENTER, canvas);
        setVisible(true);
        initField();
    }

    void openCells(int x, int y) {
        if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return;
        if (!field[y][x].isNotOpen()) return;
        field[y][x].open();
        if (field[y][x].getCountBomb() > 0 || bangMine) return;
        for (int dx = -1; dx < 2; dx++)
            for (int dy = -1; dy < 2; dy++) openCells(x + dx, y + dy);
    }

    void initField() {
        int x, y, countMines = 0;

        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                field[y][x] = new Cell();

        while (countMines < NUMBER_OF_MINES) {
            do {
                x = random.nextInt(FIELD_SIZE);
                y = random.nextInt(FIELD_SIZE);
            } while (field[y][x].isMined());
            field[y][x].mine();
            countMines++;
        }
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                if (!field[y][x].isMined()) {
                    int count = 0;
                    for (int dx = -1; dx < 2; dx++)
                        for (int dy = -1; dy < 2; dy++) {
                            int nX = x + dx;
                            int nY = y + dy;
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                nX = x;
                                nY = y;
                            }
                            count += (field[nY][nX].isMined()) ? 1 : 0;
                        }
                    field[y][x].setCountBomb(count);
                }
    }

    static boolean getCellisopen(int y, int x) {
        return field[y][x].isOpen;
    }

    static int getBombNear(int y, int x) {
        return field[y][x].countBombNear;
    }

    static int getFieldSize() {
        return FIELD_SIZE;
    }

    static int getNumberMines() {
        return NUMBER_OF_MINES;
    }


    class Cell {
        private int countBombNear;
        private boolean isOpen, isMine, isFlag;

        public void setIsOpen(boolean b) {
            isOpen = b;
        }

        void open() {
            isOpen = true;
            bangMine = isMine;
            if (!isMine) countOpenedCells++;
        }

        void mine() {
            isMine = true;
        }

        void setCountBomb(int count) {
            countBombNear = count;
        }

        int getCountBomb() {
            return countBombNear;
        }

        boolean isNotOpen() {
            return !isOpen;
        }

        boolean isOpen() {
            return isOpen;
        }

        boolean isMined() {
            return isMine;
        }

        void inverseFlag() {
            isFlag = !isFlag;
        }

        void paintBomb(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * BLOCK_SIZE + 7, y * BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x * BLOCK_SIZE + 11, y * BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x * BLOCK_SIZE + 9, y * BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x * BLOCK_SIZE + 11, y * BLOCK_SIZE + 10, 4, 4);
        }

        void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x * BLOCK_SIZE + 8, y * BLOCK_SIZE + 26);
        }

        void paint(Graphics g, int x, int y) {
            g.setColor(Color.lightGray);
            g.drawRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            if (!isOpen) {
                if ((bangMine || youWon) && isMine) paintBomb(g, x, y, Color.black);
                else {
                    g.setColor(Color.lightGray);
                    g.fill3DRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) paintString(g, SIGN_OF_FLAG, x, y, Color.red);
                }
            } else if (isMine) paintBomb(g, x, y, bangMine ? Color.red : Color.black);
            else if (countBombNear > 0)
                paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));
        }
    }


    class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int x = 0; x < FIELD_SIZE; x++)
                for (int y = 0; y < FIELD_SIZE; y++) field[y][x].paint(g, x, y);
        }
    }
    // решатель сапера
    public static class SolverMS extends JPanel {
        final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0, 0x0000FF, 0x008000, 0xFF0000};
        int FIELD_SIZE;
        int BLOCK_SIZE = 30;
        int width, height;
        int NUMBER_OF_MINES;
        Cell1[][] field1;

        ArrayList<Cell1> list = new ArrayList<>(); // ячейки с посчитанными вероятностями
        ArrayList<Group> groups = new ArrayList<Group>(); // список групп для расчета где с нулевой вероятностью мина, где с сотой

        SolverMS(){
            FIELD_SIZE = GameMines.getFieldSize();
            NUMBER_OF_MINES = GameMines.getNumberMines();
            field1 = new Cell1[FIELD_SIZE][FIELD_SIZE];
            width = FIELD_SIZE;
            height = FIELD_SIZE;
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int x = 0; x < FIELD_SIZE; x++)
                for (int y = 0; y < FIELD_SIZE; y++) field1[y][x].paint(g, x, y);
        }

        double getver(int x, int y) {
            return field1[y][x].ver;
        }

        class Cell1 {
            private int countBombNear;
            private boolean isOpen;
            double ver;
            int x, y;

            Cell1(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public void setIsOpen(boolean b) {
                isOpen = b;
            }

            @Override
            public String toString() {
                return "(" + Integer.toString(this.x) + "," + Integer.toString(this.y) + ")" + Double.toString(ver);
            }

            void setCountBomb(int count) {
                countBombNear = count;
            }

            int getCountBomb() {
                return countBombNear;
            }

            boolean isNotOpen() {
                return !isOpen;
            }

            boolean isOpen() {
                return isOpen;
            }

            void setVer(double v) {
                ver = v;
            }

            void paintString(Graphics g, String str, int x, int y, Color color) {
                g.setColor(color);
                g.drawString(str, x * BLOCK_SIZE + 8, y * BLOCK_SIZE + 26);
            }

            void paint(Graphics g, int x, int y) {
                g.setColor(Color.lightGray);
                g.drawRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                if (isOpen) {
                    if (countBombNear > 0) {
                        g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
                        paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));
                    }
                } else {
                    g.setColor(Color.lightGray);
                    g.setFont(new Font("", Font.BOLD, BLOCK_SIZE / 2));

                    g.fill3DRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    paintString(g, Integer.toString((int) ver), x, y, Color.GRAY);
                }
            }
        }

        public void initField1() {
            int x, y;
            int countNotopen = 0;
            for (x = 0; x < FIELD_SIZE; x++)
                for (y = 0; y < FIELD_SIZE; y++) {
                    field1[y][x] = new Cell1(x, y);
                    field1[y][x].isOpen = GameMines.getCellisopen(y, x); // узнаем у сапера, открыта ли клетка
                    if (field1[y][x].isOpen) {
                        field1[y][x].countBombNear = GameMines.getBombNear(y, x); // узнаем у сапера, сколько бомб рядом
                        field1[y][x].setVer(0);
                    } else countNotopen++;
                }
            for (x = 0; x < FIELD_SIZE; x++)
                for (y = 0; y < FIELD_SIZE; y++)
                    if (!field1[y][x].isOpen)
                        field1[y][x].setVer((double) NUMBER_OF_MINES * 100 / (double) countNotopen);
        }

        // создает группы, вычитает, считает вероятности 0 и 100, в list заносит закрытые клетки с посчитанными вероятностями
        void count() {
            setGroups();
            list.clear();
            int m100 = 0;
            int m0 = 0;

            for (Group g : groups) {
                ArrayList<Cell1> l = (ArrayList<Cell1>) g.getList();
                if (g.minnear == l.size() && g.minnear > 0) // если в группе столько элементов, сколько бомб, то в этих ячейках вероятность 100
                    for (Cell1 p : l) {
                        field1[p.y][p.x].setVer(100);
                        m100++;
                    }
                if (g.minnear <= 0)// если в группе 0 бомб, то вероятность во всех клетках 0
                    for (Cell1 p : l) {
                        field1[p.y][p.x].setVer(0);
                        m0++;
                    }

            }
            int countNotopen = 0; // количество закрытых ячеек
            for (int x = 0; x < FIELD_SIZE; x++)
                for (int y = 0; y < FIELD_SIZE; y++)
                    if (field1[y][x].isNotOpen()) countNotopen++;


            for (int x = 0; x < FIELD_SIZE; x++)
                for (int y = 0; y < FIELD_SIZE; y++)
                    if (field1[y][x].isNotOpen()) {
                        if (field1[y][x].ver != 0 && field1[y][x].ver != 100) {
                            field1[y][x].setVer((double) (NUMBER_OF_MINES - m100) / ((double) (countNotopen - m0 - m100)) * 100);
                        }
                        list.add(field1[y][x]);
                    }

            Collections.sort(list, new Comparator<Cell1>() {
                public int compare(Cell1 o1, Cell1 o2) {
                    return (int) Double.compare(o1.ver, o2.ver);
                }
            });
        }
        // для AvtoAll
        Point getFirstCell() {
            return new Point(list.get(0).x, list.get(0).y);
        }

        Point getRandomCell(double ver) {
            int k = 0;
            int newNumber = 0;
            for (Cell1 cell : list)
                if (cell.ver == ver) k++;
            Random random = new Random();
            newNumber = random.nextInt(k - 1);
            return new Point(list.get(newNumber).x, list.get(newNumber).y);
        }

        class Group {
            ArrayList<Cell1> cellsList;
            int minnear;

            Group(ArrayList<Cell1> c, int m) {
                minnear = m;
                cellsList = c;
            }

            boolean equals(Group g) {
                ArrayList<Cell1> l = g.getList();
                if (l.containsAll(cellsList) &&
                        cellsList.containsAll(l) &&
                        g.getMines() == this.getMines())
                    return true;
                else return false;
            }

            int size() {
                return cellsList.size();
            }

            boolean contains(Group g) {
                ArrayList<Cell1> l = (ArrayList<Cell1>) g.getList();
                if (cellsList.containsAll(l))
                    return true;
                else return false;
            }

            void subtraction(Group g) {
                ArrayList<Cell1> l = g.getList();
                for (int i = 0; i < l.size(); i++) {
                    if (cellsList.contains(l.get(i))) {
                        int ind = cellsList.indexOf(l.get(i));
                        cellsList.remove(ind);
                    }
                }
                minnear = getMines() - g.getMines();
                if (minnear < 0) minnear = 0;
            }

            ArrayList<Cell1> getList() {
                return cellsList;
            }

            int getMines() {
                return minnear;
            }

            @Override
            public String toString() {
                return String.valueOf(minnear) + "–" + this.cellsList.toString();
            }
        }
        // создает одну группу для клетки (x, y) и добавляет в groups
        public void setGroup(int x, int y, ArrayList<Group> groups) {
            ArrayList<Cell1> pl = new ArrayList<>();
            if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return; // не попал в поле
            if (field1[y][x].isNotOpen()) return; // закрытая клетка
            if (field1[y][x].getCountBomb() == 0) return; // клетка, рядом с которой нет бомб
            for (int dx = -1; dx < 2; dx++)
                for (int dy = -1; dy < 2; dy++) {
                    if (!(dx == 0 && dy == 0))
                        if (x + dx >= 0 && x + dx < FIELD_SIZE && y + dy >= 0 && y + dy < FIELD_SIZE)
                            if (field1[y + dy][x + dx].isNotOpen())
                                pl.add(field1[y + dy][x + dx]);
                }
            if (pl.size() != 0) {
                Group g = new Group(pl, field1[y][x].getCountBomb());
                groups.add(g);
            }
        }

        //          Создает список групп ячеек, связанных одним значением открытого поля, а
       //          также разбивает их на более мелкие, удаляет повторяющиеся
        public void setGroups() {
            groups.clear();
            for (int x = 0; x < width; x++) for (int y = 0; y < height; y++) setGroup(x, y, groups); // создание групп
            boolean repeat;
            do {
                repeat = false;
                for (int i = 0; i < groups.size() - 1; i++) {  // проходим по списку групп
                    Group groupI = groups.get(i);
                    for (int j = i + 1; j < groups.size(); j++) {   // сравниваем ее с остальными меньшими группами
                        Group groupJ = groups.get(j);
                        if (groupI.equals(groupJ))                  // удаляем одинаковые группы
                        {
                            groups.remove(j--);
                            break;
                        }

                        Group parent;                               // большая группа
                        Group child;                                // меньшая группа
                        if (groupI.size() > groupJ.size())            // определяем большую и меньшую группы по кол-ву ячеек
                        {
                            parent = groupI;
                            child = groupJ;
                        } else {
                            child = groupI;
                            parent = groupJ;
                        }
                        if (parent.contains(child)) {               // если большая содержит меньшую
                            parent.subtraction(child);              //  то вычитаем меньшую из большей
                            repeat = true;
                            //  фиксируем факт изменения групп
                        }
                    }
                }
            }
            while (repeat);
        }
    }
}

