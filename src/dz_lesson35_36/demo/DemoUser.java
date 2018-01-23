package dz_lesson35_36.demo;

import dz_lesson35_36.dao.UserDAO;
import dz_lesson35_36.model.User;
import dz_lesson35_36.model.UserType;

public class DemoUser {
    public static void main(String[] args)throws Exception {

        User user1 = new User("Nik", "1111", "Ukraine", UserType.USER);
        User user2 = new User("Andre", "2222", "Ukraine", UserType.ADMIN);
        User user3 = new User("Bob", "3333", "Ukraine", UserType.USER);
        User user4 = new User("Tad", "4444", "Germany", UserType.USER);
        User user5 = new User("Greg", "5555", "Italy", UserType.ADMIN);
        User user6 = new User("Ivan", "6666", "Russia", UserType.USER);
        User user7 = new User("Alex", "7777", "USA", UserType.ADMIN);
        User user8 = new User("Oleg", "8888", "Russia", UserType.USER);

        UserDAO.registerUser(user5);
        /*UserDAO.registerUser(user2);
        UserDAO.registerUser(user3);
        UserDAO.registerUser(user4);
        UserDAO.registerUser(user5);*/
    }
}
