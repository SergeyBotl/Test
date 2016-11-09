package dao;

import DB.DBUtils;
import entity.Room;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO implements DAO<Room> {

    private static File file = new File(DBUtils.getDBpath() + "\\\\rooms");
    private static List<Room> list= new ArrayList<>();

    static {
        syncDBtoList();
    }

    private static void syncDBtoList() {
        if (!list.isEmpty())
            list.clear();

        List<List<String>> inputDBData = DBUtils.getDBtoList(file);

        for (int index = 0; index < inputDBData.size(); index++) {
            try {
//                convert String data from DB to java object
                long id = Long.valueOf(inputDBData.get(index).get(0));
                int person = Integer.valueOf(inputDBData.get(index).get(1));
                int price = Integer.valueOf(inputDBData.get(index).get(2));
                long userReservedId = Long.valueOf(inputDBData.get(index).get(3));

                Room room = new Room(person, price);
                room.setId(id);
                room.setUserReservedId(userReservedId);
                list.add(room);

            } catch (IndexOutOfBoundsException e) {
                System.out.println("Room data is't found in DB");
            } catch (ClassCastException e) {
                System.out.println("Incorrect type of input room's data");
            }
        }
    }

    @Override
    public  void syncListToDB() {
        if (list == null) {
            return;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, false));
            StringBuilder sb = new StringBuilder();

            for (Room room : list) {
                sb.append(room.getId() + " " + room.getPerson() + " " + room.getPrice()
                        + " " + room.getUserReservedId() + System.lineSeparator());
            }
            bw.write(sb.toString());

        } catch (IOException e) {
            System.out.println("Wright room's data in DB was canceled");
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
    public boolean save(Room room) {
        if (room == null)
            return false;
        if (list.isEmpty())
            room.setId(0);
        else room.setId(list.get(list.size() - 1).getId() + 1);
        room.setUserReservedId(-1);
        list.add(room);
        syncListToDB();
        return true;
    }


    @Override
    public boolean delete(Room room) {
        if (room == null)
            return false;
        if (list.remove(room)) {
            syncListToDB();
            return true;
        } else {
            System.out.println("Room not found");
            return false;
        }
    }

    @Override
    public Room findById(long id) {
        for (Room room : list) {
            if (room.getId() == id)
                return room;
        }
        return null;
    }

    @Override
    public List<Room> getAll() {
        return list;
    }
}
