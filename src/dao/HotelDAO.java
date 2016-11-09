package dao;

import DB.DBUtils;
import entity.Hotel;
import entity.Room;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HotelDAO implements DAO<Hotel> {

    private static File file = new File(DBUtils.getDBpath() + "\\\\hotels");
    private static List<Hotel> list = new ArrayList<>();
    private static List<Long> hotelsRoomsId = new ArrayList<>();
    private static RoomDAO roomDAO = new RoomDAO();

    static {
        syncDBtoList();
    }

    private static void syncDBtoList() {
        if (!list.isEmpty()) {
            list.clear();
            hotelsRoomsId.clear();
        }

        List<List<String>> inputDBData = DBUtils.getDBtoList(file);

        for (int index = 0; index < inputDBData.size(); index++) {
            try {
//                convert String data from DB to java object
                long id = Long.valueOf(inputDBData.get(index).get(0));
                String hotelName = inputDBData.get(index).get(1);
                String cityName = inputDBData.get(index).get(2);

                List<String> roomsIdFromDB = inputDBData.get(index);
                hotelsRoomsId.clear();
                for (long roomId = 3; roomId < roomsIdFromDB.size(); roomId++)
                    hotelsRoomsId.add(Long.valueOf(roomsIdFromDB.get((int) roomId)));


                Hotel hotel = new Hotel(hotelName, cityName, hotelsRoomsId.stream().map(hID ->
                        roomDAO.findById(hID)).collect(Collectors.toList()));
                hotel.setId(id);
                list.add(hotel);

            } catch (IndexOutOfBoundsException e) {
                System.out.println("Hotel's data is't found in DB");
            } catch (ClassCastException e) {
                System.out.println("Incorrect type of input hotel's data");
            }
        }
    }


    @Override
    public void syncListToDB() {
        if (list == null) {
            return;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, false));
            StringBuilder sb = new StringBuilder();

            for (Hotel hotel : list) {
                StringBuilder roomIds = new StringBuilder();
                for (Room room : hotel.getRooms()) {
                    roomIds.append(" " + room.getId());
                }
                sb.append(hotel.getId() + " " + hotel.getHotelName() + " " + hotel.getCityName()
                        + roomIds + System.lineSeparator());
            }
            bw.write(sb.toString());

        } catch (IOException e) {
            System.out.println("Wright hotel's data in DB was canceled");
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                    System.out.println("Can't close DB connection");
                }
        }
    }

    @Override
    public boolean save(Hotel hotel) {

        if (hotel == null)
            return false;

        if (list.isEmpty())
            hotel.setId(0);
        else hotel.setId(list.get(list.size() - 1).getId() + 1);

        hotel.getRooms().forEach(roomDAO::save);

        list.add(hotel);
        syncListToDB();
        return true;
    }


    @Override
    public boolean delete(Hotel hotel) {
        if (hotel == null)
            return false;
        if (list.remove(hotel)) {
            syncListToDB();
            return true;
        } else {
            System.out.println("Hotel not found");
            return false;
        }
    }

    @Override
    public Hotel findById(long id) {
        for (Hotel hotel : list) {
            if (hotel.getId() == id)
                return hotel;
        }
        return null;
    }

    @Override
    public List<Hotel> getAll() {
        return list;
    }

}
