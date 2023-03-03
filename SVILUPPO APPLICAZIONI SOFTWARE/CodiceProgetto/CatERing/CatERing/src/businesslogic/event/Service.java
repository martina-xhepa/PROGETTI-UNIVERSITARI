package businesslogic.event;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import persistence.PersistenceManager;
import persistence.ResultHandler;

import businesslogic.menu.Menu;

import java.awt.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;

public class Service implements EventItemInfo {
    private int id;
    private String name;
    private Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private Menu menu;
    private int sheet;
    private SummarySheet serviceSummarySheet;
    private static Map<Integer, Service> loadedServices = FXCollections.observableHashMap();

    public Service(String name) {
        this.name = name;
    }

    public int hasSheet() {
        return this.sheet;
    }

    public Menu getMenu() {
        return this.menu;
    }
    public int getId() {
        return this.id;
    }

    public void setMenu(Menu menu){
        this.menu=menu;
    }

    public String toString() {
        return name + ": " + date + " (" + timeStart + "-" + timeEnd + "), " + participants + " pp.";
    }

    public SummarySheet getSummarySheet() {
        return this.serviceSummarySheet;
    }
    public SummarySheet defineSummarySheet(){
        this.serviceSummarySheet = new SummarySheet();
        return serviceSummarySheet;
    }



    // STATIC METHODS FOR PERSISTENCE
    public static ObservableList<Service> loadServicesForEvent(int eventId) {
        if (Event.isLoadedEvent(eventId)) {
            Event ev = Event.getLoadedEvent(eventId);
            return ev.getServices();
        }

        ObservableList<Service> result = FXCollections.observableArrayList();
        String query = "SELECT id, name, menu_id, service_date, time_start, time_end, expected_participants, sheet " +
                "FROM services WHERE event_id = " + eventId;
        ArrayList<Integer> approvedMenuIds = new ArrayList<>();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String name = rs.getString("name");
                Service serv = new Service(name);
                serv.id = rs.getInt("id");
                serv.date = rs.getDate("service_date");
                serv.timeStart = rs.getTime("time_start");
                serv.timeEnd = rs.getTime("time_end");
                serv.participants = rs.getInt("expected_participants");
                serv.sheet = rs.getInt("sheet");
                approvedMenuIds.add(rs.getInt("menu_id"));
                result.add(serv);
            }
        });
        for (int i = 0; i < result.size(); i++) {
            Service s = result.get(i);
            int menuId = approvedMenuIds.get(i);
            Menu menuApproved = (menuId > 0) ? Menu.loadMenuById(menuId) : null;
            s.setMenu((menuId > 0) ? menuApproved : null);
            loadedServices.put(s.id, s);
        }
        return result;
    }


    public static void updateService(Service currentService, int sheetId) {

        String serviceUpdate = "UPDATE services SET " +
                "sheet = " + sheetId +
                " WHERE id = " + currentService.id;
        PersistenceManager.executeUpdate(serviceUpdate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Service) {
            Service that = (Service)obj;
            return this.id == that.id;
        }
        return false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSheet(int id) {
        this.sheet = id;
    }

    public void setSummarySheet(SummarySheet sheetToOpen) {
        this.serviceSummarySheet = sheetToOpen;
    }
}
