import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
// решатель сапера
public  class SolverMS extends JPanel {
    // какие цвета
    //0x0000FF - красный
    //0x008000 - темно-зеленый
    // 0xFF0000 - синий
    //0x800000 - темно-синий
    //0x0 - черный
    // далее повторяются
    final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0, 0x0000FF, 0x008000, 0xFF0000};
    int FIELD_SIZE;
    int BLOCK_SIZE = 30;
    int width, height;
    int NUMBER_OF_MINES;
    Cell1[][] field1;

    ArrayList<Cell1> list = new ArrayList<>(); // ячейки с посчитанными вероятностями
    ArrayList<Group> groups = new ArrayList<Group>(); // список групп для расчета где с нулевой вероятностью мина, где с сотой
    // узанем у сапера параметры и создаем новое поле
    SolverMS(){
        FIELD_SIZE = GameMines.getFieldSize();
        NUMBER_OF_MINES = GameMines.getNumberMines();
        field1 = new Cell1[FIELD_SIZE][FIELD_SIZE];
        width = FIELD_SIZE;
        height = FIELD_SIZE;

    }
    // рисуем поле
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int x = 0; x < FIELD_SIZE; x++)
            for (int y = 0; y < FIELD_SIZE; y++) field1[y][x].paint(g, x, y);
    }
    // возвращаем вероятность в клетке
    double getver(int x, int y) {
        return field1[y][x].ver;
    }
    // класс новой клетки
    class Cell1 {
        private int countBombNear;
        private boolean isOpen;
        double ver;
        int x, y;

        Cell1(int x, int y) {
            this.x = x;
            this.y = y;
        }
        // ставим клетке флаг
        public void setIsOpen(boolean b) {
            isOpen = b;
        }
        // toString для клетки (координаты клетки, значение вероятности в ней)
        @Override
        public String toString() {
            return "(" + Integer.toString(this.x) + "," + Integer.toString(this.y) + ")" + Double.toString(ver);
        }
        // ставим количество бомб рядом
        void setCountBomb(int count) {
            countBombNear = count;
        }
        // узнаем количество босб рядом
        int getCountBomb() {
            return countBombNear;
        }
        // узнаем закрыта ли клетка
        boolean isNotOpen() {
            return !isOpen;
        }
       // узнаем открыта ли клетка
        boolean isOpen() {
            return isOpen;
        }
        // ставим вероятность
        void setVer(double v) {
            ver = v;
        }
        // рисуем значение в клетке
        void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.drawString(str, x * BLOCK_SIZE + 8, y * BLOCK_SIZE + 26);
        }
        // рисуем клетку
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
    // иницилизируем поле
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


        for (int x = 0; x < FIELD_SIZE; x++) // идем по полю, ставим вероятности, добавляем их в список вероятностей list
            for (int y = 0; y < FIELD_SIZE; y++)
                if (field1[y][x].isNotOpen()) {
                    if (field1[y][x].ver != 0 && field1[y][x].ver != 100) {
                        field1[y][x].setVer((double) (NUMBER_OF_MINES - m100) / ((double) (countNotopen - m0 - m100)) * 100);
                    }
                    list.add(field1[y][x]);
                }
        // сортируем список с посчитанными вероятностями в порядке возрастания вероятности
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
    // берем рандомную клетку из клеток с одинаковыми вероятностями
    Point getRandomCell(double ver) {
        int k = 0;
        int newNumber = 0;
        for (Cell1 cell : list)
            if (cell.ver == ver) k++;
        Random random = new Random();
        newNumber = random.nextInt(k - 1);
        return new Point(list.get(newNumber).x, list.get(newNumber).y);
    }
    // создаем группу для клетки, добавляем в список групп groups
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

