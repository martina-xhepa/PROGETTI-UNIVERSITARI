package businesslogic.event;

import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KitchenShift extends Shift {
    private static Map<Integer, KitchenShift> loadedKitchenShifts = FXCollections.observableHashMap();
    private boolean full;
    private ObservableList<KitchenTask> assignedTasks;
    private ObservableList<User> cooksAvailable;

    public KitchenShift() {
        this.id = 0;
        this.assignedTasks = FXCollections.observableArrayList();
        this.cooksAvailable = FXCollections.observableArrayList();
    }
    public String testString() {
        String result = "id: " + this.id + ", start time: " + this.startTime + ", end time: " + this.endTime + ", full: " + full + ".";
        result = result + "\nCOOKS AVAILABLE ("+ this.cooksAvailable.size() +")\n";
        for (User cook : cooksAvailable)
            result = result + cook.toString() + "\n";
        result = result + "\nASSIGNED TASKS ("+ this.assignedTasks.size() +")\n";
        for (KitchenTask task : assignedTasks)
            result = result + task.toString() + "\n";

        return result + "\n";
    }

    public String toString() {
        return "start time: " + this.startTime +
                ", end time: " + this.endTime + ", full: " + full + ".";
    }
    public boolean hasCookAvailable(User cook) {
        return this.cooksAvailable.contains(cook);
    }
    public boolean isFull() { return this.full; }
    public void setFull(boolean full) { this.full = full; }

    public void assignKitchenTask(KitchenTask task) { this.assignedTasks.add(task); }
    public void unassignKitchenTask(KitchenTask task) {
        this.assignedTasks.remove(task);
    }

    // STATIC METHODS FOR PERSISTENCE

    /*public static KitchenShift loadKitchenShiftById(int shiftId) {
        if (loadedKitchenShifts.containsKey(shiftId)) return loadedKitchenShifts.get(shiftId);

        KitchenShift shift = new KitchenShift();
        String query = "SELECT * FROM kitchenshifts KS join assignedtasks AT on (KS.id=AT.shift_id) " +
                "join availablecooks AC on (KS.id=AC.shift_id) WHERE KS.id="+ shiftId;
        ArrayList<Integer> availableCookIds = new ArrayList<>(),
                assignedTaskIds = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                shift.id = shiftId;
                shift.full = rs.getBoolean("full");
                shift.startTime = rs.getTime("start_time");
                shift.endTime = rs.getTime("end_time");
                int cookId = rs.getInt("cook_id"),
                        taskId = rs.getInt("task_id");
                if (!availableCookIds.contains(cookId))
                    availableCookIds.add(cookId);
                if (!assignedTaskIds.contains(taskId))
                    assignedTaskIds.add(taskId);
            }
        });

        if (shift.id > 0) {
            loadedKitchenShifts.putIfAbsent(shift.id, shift);
            for (Integer cookId : availableCookIds) {
                shift.cooksAvailable.add(User.loadUserById(cookId));
            }
            for (Integer taskId : assignedTaskIds) {
                shift.assignedTasks.add(KitchenTask.loadKitchenTaskById(taskId));
            }
        }


        return shift;
    }*/

    public static KitchenShift loadKitchenShiftById(int shiftId) {
        if (loadedKitchenShifts.containsKey(shiftId)) return loadedKitchenShifts.get(shiftId);
        String query = "SELECT * FROM kitchenshifts WHERE id = " + shiftId;
        KitchenShift shift = new KitchenShift();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                shift.id = shiftId;
                shift.full = rs.getBoolean("full");
                shift.startTime = rs.getTime("start_time");
                shift.endTime = rs.getTime("end_time");

                String getCooks = "SELECT * FROM availablecooks WHERE shift_id = " + shift.id;
                PersistenceManager.executeQuery(getCooks, new ResultHandler() {
                    @Override
                    public void handle(ResultSet rs) throws SQLException {
                        int user_id = rs.getInt("cook_id");
                        User user = User.loadUserById(user_id);
                        shift.cooksAvailable.add(user);

                    }
                });
            }
        });

        return shift;
    }


    public static ObservableList<KitchenShift> loadShiftsBoard() {
        String query = "SELECT id FROM KitchenShifts";
        ArrayList<Integer> shiftIds = new ArrayList<>();
        ObservableList<KitchenShift> result = FXCollections.observableArrayList();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                shiftIds.add(rs.getInt("id"));
            }
        });

        for (Integer i : shiftIds) {
            result.add(loadKitchenShiftById(i));
        }

        return result;
    }

    public static void updateKitchenShift(KitchenShift shift) {
        String shiftUpdate = "UPDATE kitchenshifts SET " +
                "full = " + shift.full +
                " WHERE id = " + shift.id;
        PersistenceManager.executeUpdate(shiftUpdate);
    }
}

