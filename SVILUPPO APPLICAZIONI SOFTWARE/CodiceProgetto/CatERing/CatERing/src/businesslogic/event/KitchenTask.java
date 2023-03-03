package businesslogic.event;

import businesslogic.menu.Menu;
import businesslogic.recipe.Procedure;
import businesslogic.recipe.Recipe;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class KitchenTask {
    private int id;
    private Procedure procedure;
    private String quantity;
    private boolean completed;
    private int esteemedTimeRequired;
    private KitchenShift shift;
    private User cook;
    private static Map<Integer, KitchenTask> loadedKitchenTasks = FXCollections.observableHashMap();

    public KitchenTask() { this(null); }
    public KitchenTask(Procedure procedure) {
        this.id = 0;
        this.procedure = procedure;
        this.quantity = "";
        this.esteemedTimeRequired = 0;
        /*this.completed = false;*/
    }

    public String toString() {
        return procedure.getName();
    }

    public String testString() {
        return procedure + ": " + "quantity " + quantity + ", time required " + esteemedTimeRequired + " minutes" +
                "\ncook: " + ((this.cook != null)? this.cook.toString() : "") +
                "\nshift: " + ((this.shift != null)? this.shift.getId() : "") + "\n";
    }

    public int getId() {
        return id;
    }
    public int getTimeRequired() {
        return esteemedTimeRequired;
    }
    public boolean isPrepared() {
        return completed;
    }
    public String getQuantity() {
        return quantity;
    }
    public KitchenShift getKitchenShift() { return this.shift; }
    public Procedure getKitchenProcedure() { return this.procedure; }
    public User getCook() { return this.cook; }
    public void setId(int id) {
        this.id = id;
    }
    public void setTimeRequired(int timeRequired) {
        this.esteemedTimeRequired = timeRequired;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public void setKitchenProcedure(Procedure procedure) {
        this.procedure = procedure;
    }
    public void setKitchenShift(KitchenShift shift) {
        this.shift = shift;
    }
    public void setCook(User cook) {
        this.cook = cook;
    }
    public static void addLoadedKitchenTask(KitchenTask task) { loadedKitchenTasks.putIfAbsent(task.id, task); }


    // STATIC METHODS FOR PERSISTENCE
    public static void saveNewKitchenTask(int sheetId, KitchenTask task, int position) {
        String taskInsert = "INSERT INTO catering.KitchenTasks " +
                "(summary_sheet_id, procedure_id, position, time_required, completed, quantity, cook_id, kitchenshift_id) " +
                "VALUES (" + sheetId +
                ", " + task.procedure.getId() +
                ", " + position +
                ", " + task.esteemedTimeRequired +
                ", " + task.completed +
                ", '" + PersistenceManager.escapeString(task.quantity) +
                "', " + ((task.cook != null) ? task.cook.getId() : 0) +
                ", " + ((task.shift != null) ? task.shift.getId() : 0) + ");";

        PersistenceManager.executeUpdate(taskInsert);
        task.id = PersistenceManager.getLastId();

        String sheetTasksInsert = "INSERT INTO catering.SheetTasks (sheet_id, kitchentask_id) " +
                "VALUES (" + sheetId + ", " + task.id + ");";
        PersistenceManager.executeUpdate(sheetTasksInsert);

        loadedKitchenTasks.put(task.id, task);
    }

    public static void saveAllNewKitchenTasks(int sheetId, List<KitchenTask> tasks) {
        String taskInsert = "INSERT INTO catering.kitchentasks " +
                "(summary_sheet_id, procedure_id, position, time_required, completed, quantity, cook_id, kitchenshift_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        PersistenceManager.executeBatchUpdate(taskInsert, tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                KitchenTask t = tasks.get(batchCount);
                ps.setInt(1, sheetId);
                ps.setInt(2, t.procedure.getId());
                ps.setInt(3, batchCount);
                ps.setInt(4, t.esteemedTimeRequired);
                ps.setBoolean(5, t.completed);
                ps.setString(6, PersistenceManager.escapeString(t.quantity));
                ps.setInt(7, (t.cook != null) ? t.cook.getId() : 0);
                ps.setInt(8, (t.shift != null) ? t.shift.getId() : 0);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                tasks.get(count).id = rs.getInt(1);
            }
        });

        String sheetTasksInsert = "INSERT INTO catering.sheettasks (sheet_id, kitchentask_id) VALUES (?, ?);";
        PersistenceManager.executeBatchUpdate(sheetTasksInsert, tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, sheetId);
                ps.setInt(2, tasks.get(batchCount).id);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // non ci sono id generati per questa tabella
            }
        });

        for (KitchenTask task : tasks)
            loadedKitchenTasks.put(task.id, task);
    }
    public static void updateKitchenTask(KitchenTask task) {
        String taskUpdate = "UPDATE KitchenTasks SET " +
                "time_required = " + task.esteemedTimeRequired +
                ", quantity = '"+ PersistenceManager.escapeString(task.quantity) +
                "', completed = " + task.completed +
                ", cook_id = " + ((task.cook != null) ? task.cook.getId() : 0) +
                ", kitchenshift_id = " + ((task.shift != null) ? task.shift.getId() : 0) +
                " WHERE id = " + task.id;
        PersistenceManager.executeUpdate(taskUpdate);

        if (task.shift != null) {
            updateTaskAssigned(task);
        }
    }
    public static void updateAssignedTask(KitchenTask task) {
        updateKitchenTask(task);
        updateTaskAssigned(task);
    }
    public static void updateTaskReassigned(KitchenTask task) {
        updateTaskUnassigned(task);
        updateKitchenTask(task);
    }
    public static void updateTaskAssigned(KitchenTask task) {
        String assignment = "INSERT INTO AssignedTasks (kitchenshift_id, task_id) VALUES (" +
                task.shift.getId() + ", " + task.id + ");";
        PersistenceManager.executeUpdate(assignment);
    }
    public static void updateTaskUnassigned(KitchenTask task) {
        String unassigned = "DELETE FROM AssignedTasks WHERE task_id = " + task.id;
        PersistenceManager.executeUpdate(unassigned);
    }
    public static void updateKitchenTaskReset(SummarySheet sheet, KitchenTask task) {
        updateKitchenTask(task);
        updateTaskUnassigned(task);
        SummarySheet.saveKitchenTasksOrder(sheet);
    }
    public static KitchenTask loadKitchenTaskById(int taskId) {
        if (loadedKitchenTasks.containsKey(taskId)) return loadedKitchenTasks.get(taskId);

        KitchenTask newTask = new KitchenTask();
        String query = "SELECT * FROM KitchenTasks WHERE id="+ taskId;
        int[] fieldIds = new int[3];

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                newTask.id = rs.getInt("id");
                newTask.completed = rs.getBoolean("completed");
                newTask.quantity = rs.getString("quantity");
                newTask.esteemedTimeRequired = rs.getInt("esteemedTimeRequired");
                fieldIds[0] = rs.getInt("cook_id");
                fieldIds[1] = rs.getInt("procedure_id");
                fieldIds[2] = rs.getInt("kitchenshift_id");
            }
        });
        if (newTask.id > 0) {
            loadedKitchenTasks.putIfAbsent(newTask.id, newTask);

            User cook = User.loadUserById(fieldIds[0]);
            newTask.setCook((cook.getId() > 0) ? cook : null);

            Recipe procedure = Recipe.loadRecipeById(fieldIds[1]);
            newTask.setKitchenProcedure((procedure.getId() > 0) ? procedure : null);

            KitchenShift shift = KitchenShift.loadKitchenShiftById(fieldIds[2]);
            newTask.setKitchenShift((shift.getId() > 0) ? shift : null);
        }
        return newTask;
    }
    public static void deleteKitchenTask(SummarySheet sheet, KitchenTask task) {
        updateTaskUnassigned(task);

        String removeFromSheet = "DELETE FROM SheetTasks WHERE kitchentask_id = " + task.id;
        PersistenceManager.executeUpdate(removeFromSheet);

        String removeTask = "DELETE FROM KitchenTasks WHERE id = " + task.id;
        PersistenceManager.executeUpdate(removeTask);
        loadedKitchenTasks.remove(task.id);

        SummarySheet.saveKitchenTasksOrder(sheet);
    }
}

