package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.User;
import dz_lesson35_36.model.UserType;
import dz_lesson35_36.model.Utils;

import java.io.*;
import java.util.LinkedList;
import java.util.Random;

public class UserDAO extends GeneralDAO{

    private static Utils utils = new Utils();
    //считывание данных - считывание файла
    //обработка данных - маппинг данных

    public static User registerUser(User user)throws Exception{
        //проверить на уникальность имя пользователя
        //присвоить пользователю уникальный id
        //присвоить тип пользователя, пароль и страну
        //save user to DB (file)
        if (user == null)
            throw new BadRequestException("User does not exist");

        if (checkValidLoginName(utils.getPathUserDB(), user.getUserName()))
            throw new BadRequestException("User with name " + user.getUserName() + " already exists");

        Random random = new Random();
        user.setId(random.nextLong() / 1000000000000L);
        if (user.getId() < 0){
            user.setId(-1 * user.getId());
        }

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(utils.getPathUserDB(), true))){
            bufferedWriter.append(Long.toString(user.getId()) + (","));
            bufferedWriter.append(user.getUserName() + (","));
            bufferedWriter.append(user.getPassword() + (","));
            bufferedWriter.append(user.getCountry() + (","));
            bufferedWriter.append(user.getUserType().toString() + ("\n"));
        }catch (IOException e){
            throw new IOException("Can not write to file " + utils.getPathUserDB());
        }
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

        for (User el : gettingListObjectsFromFileUserDB(path)) {
            if (el != null && el.getUserName().equals(loginName)){
                return true;
            }
        }
        return false;
    }

    private static LinkedList<User> gettingListObjectsFromFileUserDB(String path)throws Exception{
        if(path == null)
            throw new BadRequestException("This path " + path + " does not exists");

        LinkedList<User> arrays = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;

            while ((line = br.readLine()) != null){
                String[] result = line.split("\n");
                for (String el : result){
                    if (el != null){
                        String[] fields = el.split(",");
                        User user = new User();
                        user.setId(Long.parseLong(fields[0]));
                        user.setUserName(fields[1]);
                        user.setPassword(fields[2]);
                        user.setCountry(fields[3]);
                        if (fields[4].equals("USER")){
                            user.setUserType(UserType.USER);
                        }else
                            user.setUserType(UserType.ADMIN);
                        arrays.add(user);
                    }
                }
            }
        }catch (FileNotFoundException e){
            throw new FileNotFoundException("File does not exist");
        } catch (IOException e) {
            throw new IOException("Reading from file " + path + " failed");
        }
        return arrays;
    }

    //проверка на цифровые символы... можно её не использовать, так как id в fields[0] генерируется рандомом и содержит только цифровые символы

    /*private static String gettingOnlyNumericCharacters(String[] arrayLine) {
        String id = "";
        for (Character ch : arrayLine[0].toCharArray()) {
            if (ch != null && Character.isDigit(ch)) {
                id += ch;
            }
        }
        return id;
    }*/
}
