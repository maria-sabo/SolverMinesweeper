import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
//    0 1 2 3
// 0  - 1 0 0
// 1  - 2 1 0
// 2  - - 2 1
// 3  - - - -
public class GameMinesTest {
    @Test
    // создаем группы по одной методом setGroup(x, y, groups)
    public void askGroups() {

        ArrayList<Group> groups = new ArrayList<>();
        GameMines gameMines = new GameMines(4, 4);
        SolverMS solverMS = new SolverMS();

        gameMines.field[0][0].setIsOpen(false);
        gameMines.field[0][1].setIsOpen(true);
        gameMines.field[0][1].setCountBomb(1);
        gameMines.field[0][2].setIsOpen(true);
        gameMines.field[0][2].setCountBomb(0);
        gameMines.field[0][3].setIsOpen(true);
        gameMines.field[0][3].setCountBomb(0);
        gameMines.field[1][0].setIsOpen(false);
        gameMines.field[1][1].setIsOpen(true);
        gameMines.field[1][1].setCountBomb(2);
        gameMines.field[1][2].setIsOpen(true);
        gameMines.field[1][2].setCountBomb(1);
        gameMines.field[1][3].setIsOpen(true);
        gameMines.field[1][3].setCountBomb(0);
        gameMines.field[2][0].setIsOpen(false);
        gameMines.field[2][1].setIsOpen(false);
        gameMines.field[2][2].setIsOpen(true);
        gameMines.field[2][2].setCountBomb(2);
        gameMines.field[2][3].setIsOpen(true);
        gameMines.field[2][3].setCountBomb(1);
        gameMines.field[3][0].setIsOpen(false);
        gameMines.field[3][1].setIsOpen(false);
        gameMines.field[3][2].setIsOpen(false);
        gameMines.field[3][3].setIsOpen(false);

        solverMS.initField1();

        solverMS.setGroup(1, 0, groups);
        solverMS.setGroup(1, 1, groups);
        solverMS.setGroup(2, 2, groups);
        solverMS.setGroup(3, 2, groups);
        solverMS.setGroup(2, 1, groups);
        System.out.println("Созданные по одной группы: \n" + groups.toString());

        assertEquals("[1–[(0,0)50.0, (0,1)50.0]," +
                " 2–[(0,0)50.0, (0,1)50.0, (0,2)50.0, (1,2)50.0], " +
                "2–[(1,2)50.0, (1,3)50.0, (2,3)50.0, (3,3)50.0]," +
                " 1–[(2,3)50.0, (3,3)50.0]," +
                " 1–[(1,2)50.0]]", groups.toString());
    }
    // создаем группы по одной и вычитаем методом subtraction(group)
    @Test
    public void askGroupsbySubtraction() {
        ArrayList<Group> groups = new ArrayList<>();
        GameMines gameMines = new GameMines(4, 4);
        SolverMS solverMS = new SolverMS();

        gameMines.field[0][0].setIsOpen(false);
        gameMines.field[0][1].setIsOpen(true);
        gameMines.field[0][1].setCountBomb(1);
        gameMines.field[0][2].setIsOpen(true);
        gameMines.field[0][2].setCountBomb(0);
        gameMines.field[0][3].setIsOpen(true);
        gameMines.field[0][3].setCountBomb(0);
        gameMines.field[1][0].setIsOpen(false);
        gameMines.field[1][1].setIsOpen(true);
        gameMines.field[1][1].setCountBomb(2);
        gameMines.field[1][2].setIsOpen(true);
        gameMines.field[1][2].setCountBomb(1);
        gameMines.field[1][3].setIsOpen(true);
        gameMines.field[1][3].setCountBomb(3);
        gameMines.field[2][0].setIsOpen(false);
        gameMines.field[2][1].setIsOpen(false);
        gameMines.field[2][2].setIsOpen(true);
        gameMines.field[2][2].setCountBomb(2);
        gameMines.field[2][3].setIsOpen(true);
        gameMines.field[2][3].setCountBomb(1);
        gameMines.field[3][0].setIsOpen(false);
        gameMines.field[3][1].setIsOpen(false);
        gameMines.field[3][2].setIsOpen(false);
        gameMines.field[3][3].setIsOpen(false);

        solverMS.initField1();

        solverMS.setGroup(1, 0, groups);
        solverMS.setGroup(1, 1, groups);
        solverMS.setGroup(2, 2, groups);
        solverMS.setGroup(3, 2, groups);
        solverMS.setGroup(2, 1, groups);
        System.out.println("Созданные по одной группы: \n" + groups.toString());


        groups.get(1).subtraction(groups.get(0));
        groups.get(2).subtraction(groups.get(3));
        groups.get(2).subtraction(groups.get(4));
        System.out.println("Грцппы после трех вычитаний: \n" + groups.toString());
        assertEquals("[1–[(0,0)50.0, (0,1)50.0]," +
                " 1–[(0,2)50.0, (1,2)50.0], " +
                "0–[(1,3)50.0], " +
                "1–[(2,3)50.0, (3,3)50.0], " +
                "1–[(1,2)50.0]]", groups.toString());
        groups.get(1).subtraction(groups.get(4)); // довычтем
        System.out.println("Группы после 'довычитания': \n" + groups.toString());
        assertEquals("[1–[(0,0)50.0, (0,1)50.0]," +
                " 0–[(0,2)50.0], " +
                "0–[(1,3)50.0], " +
                "1–[(2,3)50.0, (3,3)50.0], " +
                "1–[(1,2)50.0]]", groups.toString());

    }
    // создаем все группы и вычитаем общим методом setGroups(groups)
    @Test
    public void askGroupsbysetGroups() {
        ArrayList<Group> groups = new ArrayList<>();
        GameMines gameMines = new GameMines(4, 4);
        SolverMS solverMS = new SolverMS();

        gameMines.field[0][0].setIsOpen(false);
        gameMines.field[0][1].setIsOpen(true);
        gameMines.field[0][1].setCountBomb(1);
        gameMines.field[0][2].setIsOpen(true);
        gameMines.field[0][2].setCountBomb(0);
        gameMines.field[0][3].setIsOpen(true);
        gameMines.field[0][3].setCountBomb(0);
        gameMines.field[1][0].setIsOpen(false);
        gameMines.field[1][1].setIsOpen(true);
        gameMines.field[1][1].setCountBomb(2);
        gameMines.field[1][2].setIsOpen(true);
        gameMines.field[1][2].setCountBomb(1);
        gameMines.field[1][3].setIsOpen(true);
        gameMines.field[1][3].setCountBomb(3);
        gameMines.field[2][0].setIsOpen(false);
        gameMines.field[2][1].setIsOpen(false);
        gameMines.field[2][2].setIsOpen(true);
        gameMines.field[2][2].setCountBomb(2);
        gameMines.field[2][3].setIsOpen(true);
        gameMines.field[2][3].setCountBomb(1);
        gameMines.field[3][0].setIsOpen(false);
        gameMines.field[3][1].setIsOpen(false);
        gameMines.field[3][2].setIsOpen(false);
        gameMines.field[3][3].setIsOpen(false);

        solverMS.initField1();
        solverMS.setGroups(); // создает группы в своем поле (groups)
        System.out.println("Созданные методом setGroups, включающим вычитания, группы: \n" + solverMS.groups.toString());
        assertEquals("[1–[(0,0)50.0, (0,1)50.0], " +
                "0–[(0,2)50.0]," +
                " 1–[(1,2)50.0]," +
                " 0–[(1,3)50.0], " +
               "1–[(2,3)50.0, (3,3)50.0]]", solverMS.groups.toString());
    }
    @Test
    public void changeVer() {
        ArrayList<Group> groups = new ArrayList<>();
        GameMines gameMines = new GameMines(4, 4);
        SolverMS solverMS = new SolverMS();

        gameMines.field[0][0].setIsOpen(false);
        gameMines.field[0][1].setIsOpen(true);
        gameMines.field[0][1].setCountBomb(1);
        gameMines.field[0][2].setIsOpen(true);
        gameMines.field[0][2].setCountBomb(0);
        gameMines.field[0][3].setIsOpen(true);
        gameMines.field[0][3].setCountBomb(0);
        gameMines.field[1][0].setIsOpen(false);
        gameMines.field[1][1].setIsOpen(true);
        gameMines.field[1][1].setCountBomb(2);
        gameMines.field[1][2].setIsOpen(true);
        gameMines.field[1][2].setCountBomb(1);
        gameMines.field[1][3].setIsOpen(true);
        gameMines.field[1][3].setCountBomb(3);
        gameMines.field[2][0].setIsOpen(false);
        gameMines.field[2][1].setIsOpen(false);
        gameMines.field[2][2].setIsOpen(true);
        gameMines.field[2][2].setCountBomb(2);
        gameMines.field[2][3].setIsOpen(true);
        gameMines.field[2][3].setCountBomb(1);
        gameMines.field[3][0].setIsOpen(false);
        gameMines.field[3][1].setIsOpen(false);
        gameMines.field[3][2].setIsOpen(false);
        gameMines.field[3][3].setIsOpen(false);

        solverMS.initField1();
        solverMS.count(); // после вычитания групп меняет вероятность в кажой группе, в которой одна клетка
        System.out.println("Группы после вычитания: \n"+ solverMS.groups.toString());
        assertEquals("[1–[(0,0)60.0, (0,1)60.0]," +
                " 0–[(0,2)0.0], 1–[(1,2)100.0]," +
                " 0–[(1,3)0.0], " +
                "1–[(2,3)60.0, (3,3)60.0]]", solverMS.groups.toString());
        System.out.println("Список клеток в порядке увеличения вероятности: \n" + solverMS.list.toString());
        assertEquals("[(0,2)0.0," +
                " (1,3)0.0," +
                " (0,0)60.0," +
                " (0,1)60.0," +
                " (0,3)60.0," +
                " (2,3)60.0," +
                " (3,3)60.0," +
                " (1,2)100.0]",solverMS.list.toString());
    }


}