package businesslogic.event;

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
import java.util.ArrayList;

public class SummarySheet {
    private int id;
    private ObservableList<KitchenTask> tasks;

    public SummarySheet() {
        this.id = 0;
        this.tasks = FXCollections.observableArrayList();
    }
    public void addKitchenTask(int position, KitchenTask task)        { this.tasks.add(position, task); }
    public void addKitchenTask(KitchenTask task)        { this.tasks.add(task); }
    public boolean removeKitchenTask(KitchenTask task)  { return this.tasks.remove(task); }
    public boolean hasKitchenTask(KitchenTask task)  { return this.tasks.contains(task); }

    public void moveKitchenTask(KitchenTask task, int position) {
        this.tasks.remove(task);
        this.tasks.add(position, task);
    }
    public void setId(int id) { this.id = id; }
    public int getId() { return this.id; }
    public ObservableList<KitchenTask> getAllTasks() { return FXCollections.unmodifiableObservableList(this.tasks); }


    // STATIC METHODS FOR PERSISTENCE

    public static void saveNewSummarySheet(SummarySheet sheet, int service_id) {
        String sheetInsert = "INSERT INTO catering.summarysheets (service_id) VALUES (?);";
        int[] result = PersistenceManager.executeBatchUpdate(sheetInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, service_id);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                if (count == 0) {
                    sheet.id = rs.getInt(1);
                }
            }
        });

        if (result[0] > 0) { // sheet effettivamente inserito
            // salva i compiti
            if (sheet.tasks.size() > 0) {
                KitchenTask.saveAllNewKitchenTasks(sheet.id, sheet.tasks);
            }
        }
    }

    public static void saveKitchenTasksOrder(SummarySheet sheet) {
        String upd = "UPDATE KitchenTasks SET position = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, sheet.tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, batchCount);
                ps.setInt(2, sheet.tasks.get(batchCount).getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
    }

    /*public static SummarySheet loadServiceSheet(Service s) {
        String query = "SELECT * FROM ServiceSheets sh " +
                "join SheetTasks st on (sh.id=st.sheet_id) " +
                "join KitchenTasks kts on (st.kitchentask_id=kts.id) WHERE sh.service_id=" + s.getId();
        ObservableList<KitchenTask> newTasks = FXCollections.observableArrayList();
        ArrayList<Integer> procedureIds = new ArrayList<>(),
                cookIds = new ArrayList<>(),
                shiftIds = new ArrayList<>(),
                positions = new ArrayList<>();
        int[] sheetId = new int[1];

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                if (sheetId[0] == 0)
                    sheetId[0] = rs.getInt("sh.id");
                KitchenTask kt = new KitchenTask();
                kt.setId(rs.getInt("kitchentask_id"));
                kt.setCompleted(rs.getBoolean("prepared"));
                kt.setQuantity(rs.getString("quantity"));
                kt.setTimeRequired(rs.getInt("time_required"));

                newTasks.add(kt);
                cookIds.add(rs.getInt("cook_id"));
                shiftIds.add(rs.getInt("kitchenshift_id"));
                procedureIds.add(rs.getInt("procedure_id"));
                positions.add(rs.getInt("position"));
            }
        });

        SummarySheet newSheet = new SummarySheet(positions.size());
        newSheet.id = sheetId[0];
        newSheet.service = s;

        for (Integer position : positions) {
            for (int i = 0; i < newTasks.size(); i++) {
                if (positions.get(i) == position) {
                    KitchenTask task = newTasks.get(i);

                    User cook = User.loadUserById(cookIds.get(i));
                    task.setCook((cook.getId() > 0) ? cook : null);

                    KitchenShift shift = KitchenShift.loadKitchenShiftById(shiftIds.get(i));
                    task.setKitchenShift((shift.getId() > 0) ? shift : null);

                    Recipe recipe = Recipe.loadRecipeById(procedureIds.get(i));
                    task.setKitchenProcedure((recipe.getId() > 0) ? recipe : null);

                    newSheet.addKitchenTask(task);
                    KitchenTask.addLoadedKitchenTask(task);
                }
            }
        }

        return newSheet;
    }*/

}


