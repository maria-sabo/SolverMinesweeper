import java.util.ArrayList;
// группа - закрытые ячейки, окружающие данную нам клетку и значение, стоящее в чейке, обозначающее количество бомб вокруг
class Group {
    ArrayList<SolverMS.Cell1> cellsList; // список ячеек, принадлежащих группе
    int minnear; // количество мин в группе

    Group(ArrayList<SolverMS.Cell1> c, int m) {
        minnear = m;
        cellsList = c;
    }
// когда две группы равны
    boolean equals(Group g) {
        ArrayList<SolverMS.Cell1> l = g.getList();
        if (l.containsAll(cellsList) &&
                cellsList.containsAll(l) &&
                g.getMines() == this.getMines())
            return true;
        else return false;
    }

    int size() {
        return cellsList.size();
    }
    // содержит ли одна группа другую
    boolean contains(Group g) {
        ArrayList<SolverMS.Cell1> l = (ArrayList<SolverMS.Cell1>) g.getList();
        if (cellsList.containsAll(l))
            return true;
        else return false;
    }
   // вычитает группы
    void subtraction(Group g) {
        ArrayList<SolverMS.Cell1> l = g.getList();
        for (int i = 0; i < l.size(); i++) {
            if (cellsList.contains(l.get(i))) {
                int ind = cellsList.indexOf(l.get(i));
                cellsList.remove(ind);
            }
        }
        minnear = getMines() - g.getMines();
        if (minnear < 0) minnear = 0;
    }
    // возвращает список
    ArrayList<SolverMS.Cell1> getList() {
        return cellsList;
    }
   // возвращает количество мин в группе
    int getMines() {
        return minnear;
    }
   // метод toString для группы: печатает количество мин в группе и координаты ячеек, входящих в группу
    @Override
    public String toString() {
        return String.valueOf(minnear) + "–" + this.cellsList.toString();
    }
}