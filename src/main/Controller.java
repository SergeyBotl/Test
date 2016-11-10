package main;

import dao.*;
import entity.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Controller {
    private DAO<Hotel> hotelDAO = new HotelDAO();
    private DAO<User> userDAO = new UserDAO();
    private DAO<Room> roomDAO = new RoomDAO();

    public List<Hotel> findHotelByName(String name) {
        if (name == null || name.equals("")) {
            System.out.println("Hotel's name don't be empty");
            return null;
        }

        List<Hotel> foundHotels = hotelDAO.getAll().stream()
                .filter(hotel -> hotel.getHotelName().equals(name))
                .collect(Collectors.toList());

        if (!foundHotels.isEmpty()) {
            System.out.println("\n  Found hotels list:");
            for (Hotel hotel : foundHotels)
                System.out.println(hotel.toString());
        } else {
            System.out.println("\n  No found any hotel");
        }
        return foundHotels;
    }

    public List<Hotel> findHotelByCity(String city) {
        if (city == null || city.equals("")) {
            System.out.println("City's name don't be empty");
            return null;
        }

        List<Hotel> hotels = hotelDAO.getAll()
                .stream()
                .filter(hotel -> hotel.getCityName().equals(city))
                .collect(Collectors.toList());

        if (!hotels.isEmpty()) {
            System.out.println("\n  Hotels of " + city + ":");
            for (Hotel hotel : hotels)
                System.out.println(hotel.toString());
        } else {
            System.out.println("\n  No found hotel in this city");
        }
        return hotels;

    }

    private boolean checkValidIDs(long roomId, long userId, long hotelId) {
        User user = userDAO.findById(userId);
        Hotel hotel = hotelDAO.findById(hotelId);
        Room room = roomDAO.findById(roomId);

        if (user == null) {
            System.out.println("\n  User with id=" + userId + " not found. Please, register user");
            return false;
        }
        if (hotel == null) {
            System.out.println("\n  Hotel with id=" + hotelId + " not found. Please, enter another id");
            return false;
        }
        if (room == null) {
            System.out.println("\n  Room with id=" + roomId + " not found. Please, enter another id");
            return false;
        }
        if (!hotel.getRooms().contains(room)) {
            System.out.println("\n  Room with id=" + roomId + " not found in hotel with id=" + hotelId);
            return false;
        }
        return true;
    }


    public void bookRoom(long roomId, long userId, long hotelId) {
        if (!checkValidIDs(roomId, userId, hotelId))
            return;

        Room room = roomDAO.findById(roomId);
        if (room.getUserReservedId() != -1)
            System.out.println("\n Unfortunately, room with id=" + userId + " is booking");
        else {
            room.setUserReservedId(userId);
            roomDAO.syncListToDB();
            System.out.println("\n You booked room with id=" + roomId);
        }

    }


    public void cancelReservation(long roomId, long userId, long hotelId) {
        if (!checkValidIDs(roomId, userId, hotelId))
            return;

        Room room = roomDAO.findById(roomId);
        long reservedId = room.getUserReservedId();
        if (reservedId == -1) {
            System.out.println("\n Room with id=" + userId + " is NOT booking yet");
            return;
        }
        if (reservedId != userId)
            System.out.println("\n You can't cancel not your's reservation." +
                    "\n     Reserved for User with id=" + reservedId +
                    "\n     Your's id=" + userId);
        else {
            room.setUserReservedId(-1);
            roomDAO.syncListToDB();
            System.out.println("\n You canceled booking room");
        }

    }

    Collection<Hotel> findRoom(Map<String, String> params) {
        System.out.println("\n  Search rooms in the parameters");
        int person;
        int price;
        Function<String, Integer> toInteger = Integer::valueOf;
        try {
            person = toInteger.apply(params.get("Person"));
        } catch (NumberFormatException e) {
            person = 0;
        }
        try {
            price = toInteger.apply(params.get("MaxPrice"));
        } catch (NumberFormatException e) {
            price = 0;
        }
        String cityFind = params.get("City");
        String hotelFind = params.get("Hotel");
        List<Hotel> hotels = hotelDAO.getAll().stream().collect(Collectors.toList());

        if (cityFind != null) {
            hotels = hotels.stream().filter(h -> h.getCityName().equals(cityFind)).collect(Collectors.toList());
        }
        if (hotelFind != null) {
            hotels = hotels.stream().filter(h -> h.getHotelName().equals(hotelFind)).collect(Collectors.toList());
        }

        if (price != 0) {
            int finalPrice = price;
            for (Hotel hotel : hotels) {
                List<Room> rooms = hotel.getRooms();
                rooms = rooms.stream().filter(r -> r.getPrice() < finalPrice).collect(Collectors.toList());
                hotel.setRooms(rooms);
            }
        }

        if (person > 0) {
            int finalPerson = person;
            for (Hotel hotel : hotels) {
                List<Room> rooms = hotel.getRooms();
                rooms = rooms.stream().filter(r -> r.getPerson() == finalPerson).collect(Collectors.toList());
                hotel.setRooms(rooms);
            }

        }

        for (Hotel hotel : hotels) {
            System.out.println(hotel);
            for (Room room : hotel.getRooms()) {
                System.out.println(room);
            }
        }
     /*   Room findRooms = new Room(person, price);

        String text = "";

        for (Hotel hotel : hotels) {
            System.out.println("\n" + hotel);
            if (person != 0 || price != 0) {
                if (hotel.getRooms() != null) {
                    for (Room room : hotel.getRooms()) {
                        if (room.equals(findRooms)) {
                            System.out.println("   " + room);
                        }
                    }
                } else {
                    System.out.println("\nThere is no information about the rooms ");
                }
            } else {
                text = "All info";
                hotel.getRooms().forEach(room -> System.out.println("   " + room));
            }
        }*/

        System.out.println("\n Search options || " // + text
                + "\nCity:  " + params.get("City")
                + "\nHotel: " + params.get("Hotel")
                + "\nMaxPrice: " + price
                + "\nPerson: " + person);

        return hotels;
    }


    public void registerUser(User user) {
        if (user == null) return;
        for (char c : (user.getUserName().toCharArray())) {
            if (c == ' ') {
                System.out.println("User name can't contain 'space'");
                return;
            }
        }
        for (char c : (user.getPassword().toCharArray())) {
            if (c == ' ') {
                System.out.println("User password can't contain 'space'");
                return;
            }
        }

        if (userDAO.getAll().contains(user)) {
            System.out.println("User " + user.getUserName() + " already registered");
            return;
        }

        if (userDAO.save(user))
            System.out.println("Created new User: " + user.getUserName());
        else
            System.out.println();
    }

}

