package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.User;
import dz_lesson35_36.model.UserType;

import java.io.*;
import java.util.LinkedList;

public class UserDAO extends GeneralDAO {

    //считывание данных - считывание файла
    //обработка данных - маппинг данных

    public static User registerUser(User user)throws Exception{
        //проверить на уникальность имя пользователя
        //присвоить пользователю уникальный id
        //присвоить тип пользователя, пароль и страну
        //save user to DB (file)
        if (user == null)
            throw new BadRequestException("User does not exist");

        if (checkValidLoginName(GeneralDAO.getPathUserDB(), user.getUserName()))
            throw new BadRequestException("User with name " + user.getUserName() + " already exists");

        assignmentObjectId(user);

        writerToFile(user);

        return user;
    }

    //методы входа и выхода из системы оставляем на самый конец
    /*public static void login(String userName, String password)throws Exception {
        if (userName == null || password == null)
            throw new BadRequestException("Username or password is not exists");

        String[] lines = readingFromFile(utils.getPathUserDB()).split(",");
        for (String el : lines) {
            if (el != null && el.contains(userName) && el.contains(password)) {

            }
        }
    }*/

    private static boolean checkValidLoginName(String path, String loginName)throws Exception{
        if (path == null || loginName == null)
            throw new BadRequestException("Invalid incoming data");

        for (User el : gettingListObjectsFromFileUserDB()) {
            if (el != null && el.getUserName().equals(loginName)){
                return true;
            }
        }
        return false;
    }

    public static LinkedList<User> gettingListObjectsFromFileUserDB()throws Exception{
        LinkedList<User> arrays = new LinkedList<>();

        int index = 0;
        for (String el : readFromFile(GeneralDAO.getPathUserDB())){
            if (el != null){
                arrays.add(mapUsers(readFromFile(GeneralDAO.getPathUserDB()).get(index)));
            }
            index++;
        }
        return arrays;
    }

    private static User mapUsers(String string)throws Exception{
        if (string == null)
            throw new BadRequestException("String does not exist");

        String[] fields = string.split(",");

        User user = new User();
        user.setId(Long.parseLong(fields[0]));
        user.setUserName(fields[1]);
        user.setPassword(fields[2]);
        user.setCountry(fields[3]);
        if (fields[4].equals("USER")){
            user.setUserType(UserType.USER);
        }else {
            user.setUserType(UserType.ADMIN);
        }
        return user;
    }

    private static void writerToFile(User user)throws Exception{
        if (user == null)
            throw new BadRequestException("User does not exist");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(GeneralDAO.getPathUserDB(), true))){
            bufferedWriter.append(Long.toString(user.getId()) + (","));
            bufferedWriter.append(user.getUserName() + (","));
            bufferedWriter.append(user.getPassword() + (","));
            bufferedWriter.append(user.getCountry() + (","));
            bufferedWriter.append(user.getUserType().toString() + ("\n"));
        }catch (IOException e){
            throw new IOException("Can not write to file " + GeneralDAO.getPathUserDB());
        }
    }
}
