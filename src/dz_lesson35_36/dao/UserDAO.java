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
    //private static final String PATH_USER_DB = "C:\\Users\\Skorodielov\\Desktop\\UserDB.txt";
    //считывание данных - считывание файла
    //обработка данных - маппинг данных
    public static User registerUser(User user)throws Exception{
        //проверить на уникальность имя пользователя
        //присвоить пользователю уникальный id
        //присвоить тип пользователя, пароль и страну
        //save user to DB (file)
        if (user == null)
            throw new BadRequestException("This user is not exist");

        if (checkValidLoginName(utils.getPathUserDB(), user.getUserName()))
            throw new BadRequestException("User with name " + user.getUserName() + " already exists");


        checkingReadFile(utils.getPathUserDB());

        Random random = new Random();
        user.setId(random.nextLong() / 1000000000000L);
        if (user.getId() < 0){
            user.setId(-1 * user.getId());
        }

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(utils.getPathUserDB(), true))){
            bufferedWriter.append(Long.toString(user.getId())).append(",");
            bufferedWriter.append(user.getUserName()).append(",");
            bufferedWriter.append(user.getPassword()).append(",");
            bufferedWriter.append(user.getCountry()).append(",");
            bufferedWriter.append(user.getUserType().toString());
            bufferedWriter.append("\n");
        }catch (IOException e){
            throw new IOException("Can not write to file " + utils.getPathUserDB());
        }
        return user;
    }

    public static void login(String userName, String password)throws Exception {
        if (userName == null || password == null)
            throw new BadRequestException("Username or password is not exists");

        String[] lines = readingFromFile(utils.getPathUserDB()).split(",");
        for (String el : lines) {
            if (el != null && el.contains(userName) && el.contains(password)) {

            }
        }
    }

    private static boolean checkValidLoginName(String path, String loginName)throws Exception{
        if (path == null || loginName == null)
            throw new BadRequestException("Invalid incoming data");

        int index = 0;
        for (User el : gettingListObjectsFromFile(path)) {
            if (el != null && el.getUserName().equals(loginName)){
                return true;
            }
            index++;
        }
        return false;
    }

    private static LinkedList<User> gettingListObjectsFromFile(String path)throws Exception{
        if(path == null)
            throw new BadRequestException("This path " + path + " is not exists");

        LinkedList<User> arrays = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;

            while ((line = br.readLine()) != null){
                String[] result = line.split("\n");
                int index = 0;
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
                    index++;
                }
            }
        }catch (FileNotFoundException e){
            throw new FileNotFoundException("File does not exist");
        } catch (IOException e) {
            throw new IOException("Reading from file " + path + " failed");
        }
        return arrays;
    }
}
