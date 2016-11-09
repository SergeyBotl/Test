package main;

import dao.*;
import entity.*;


import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        HotelDAO hotelDAO = new HotelDAO();
        RoomDAO roomDAO = new RoomDAO();
        Controller c = new Controller();


     /*   c.registerUser(new User("Sergey", "1111"));
        c.registerUser(new User("Nazar", "2222"));
        c.registerUser(new User("Taras", "3333"));*/
        User user1 = new User("Egor", "4444");
        User user2 = new User("Mariya", "4444");
        User user3 = new User("ew", "4444");
        c.registerUser(user1);
        c.registerUser(user2);
        c.registerUser(user3);

        c.bookRoom(25, 2, 3);
        c.bookRoom(9, 2, 0);
        c.cancelReservation(9, 1, 0);
        c.cancelReservation(9, 2, 0);

        c.findHotelByName("Redisson");
        c.findHotelByCity("Kiev");

        userDAO.delete(user1);
        userDAO.delete(user2);
        userDAO.delete(user3);


        Map<String, String> map = new HashMap<>();
        map.put("City", "Lviv");
       // map.put("Hotel", "Plasa");
        map.put("MaxPrice", "300");
       // map.put("Person", "3");
        c.findRoom(map);



    }
}

