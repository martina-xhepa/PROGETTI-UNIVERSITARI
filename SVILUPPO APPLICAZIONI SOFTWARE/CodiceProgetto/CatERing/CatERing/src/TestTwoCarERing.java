import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.event.KitchenShift;
import businesslogic.event.KitchenTask;
import businesslogic.event.EventException;
import businesslogic.event.SummarySheet;
import businesslogic.menu.Menu;
import businesslogic.recipe.Recipe;
import businesslogic.recipe.RecipeManager;
import businesslogic.user.User;
import javafx.collections.ObservableList;

public class TestTwoCarERing {

    public static void main(String[] args) {
        try {
            CatERing instance = CatERing.getInstance();
            instance.getUserManager().fakeLogin("Lidia");
            System.out.println("User: " + instance.getUserManager().getCurrentUser());

            Event event = Event.loadEventById(1);
            System.out.println("EVENT LOADED: " + event.toString());

            ObservableList<Service> allServices = Service.loadServicesForEvent(event.getId());

            Service service2 = allServices.get(2);
            System.out.println("SERVICE LOADED: " + service2.toString());

            System.out.println("-- OPEN SERVICE SHEET --");
            SummarySheet newSheet = instance.getEventManager().openSummarySheet(event, service2);
            System.out.println("TASKS");
            for (KitchenTask task : newSheet.getAllTasks()) {
                System.out.println(task.toString());
            }

            System.out.println("\r\n-- INSERT NEW KITCHEN TASKS --\r\n");
            Recipe rec = Recipe.loadRecipeById(10);
            KitchenTask newTask =  instance.getEventManager().insertKitchenTask(rec);
            System.out.println("NEW TASK ADDED TO THE SHEET");
            System.out.println(newTask.toString() + "\r\n");
            Recipe rec2 = Recipe.loadRecipeById(7);
            KitchenTask newTask2 =  instance.getEventManager().insertKitchenTask(rec2);
            System.out.println("NEW TASK ADDED TO THE SHEET");
            System.out.println(newTask2.toString() + "\r\n");

            System.out.println("\nASSIGNMENTS\n");
            User cookMarinella = User.loadUser("Marinella");
            User cookAntonietta = User.loadUser("Antonietta");

            System.out.println("\r\n-- COOKS --\r\n");
            instance.getEventManager().modifyCook(newSheet.getAllTasks().get(0), cookMarinella);
            instance.getEventManager().modifyCook(newSheet.getAllTasks().get(2), cookMarinella);
            instance.getEventManager().modifyCook(newSheet.getAllTasks().get(4), cookAntonietta);

            System.out.println("\r\n-- TIME --\r\n");
            instance.getEventManager().modifyTimeRequired(newSheet.getAllTasks().get(0), 110);
            instance.getEventManager().modifyTimeRequired(newSheet.getAllTasks().get(4), 45);

            System.out.println("\r\n-- QUANTITY --\r\n");
            instance.getEventManager().modifyQuantity(newSheet.getAllTasks().get(2), null);
            instance.getEventManager().modifyQuantity(newSheet.getAllTasks().get(3), "250g");

            KitchenShift shiftOne = KitchenShift.loadKitchenShiftById(1);
            KitchenShift shiftTwo = KitchenShift.loadKitchenShiftById(2);

            System.out.println("\r\n-- LOOKUP SHIFTS BOARD --\r\n");
            ObservableList<KitchenShift> allShifts = instance.getEventManager().examineShiftsBoard();
            for (KitchenShift shift : allShifts) {
                System.out.println(shift);
            }

            System.out.println("\r\n-- FULL SHIFT --\r\n");
            instance.getEventManager().setKitchenShiftFull(shiftOne, false);
            instance.getEventManager().setKitchenShiftFull(shiftTwo, false);

            System.out.println("\r\n-- SHIFT --\r\n");
            instance.getEventManager().modifyShift(newSheet.getAllTasks().get(0), shiftOne);
            instance.getEventManager().modifyShift(newSheet.getAllTasks().get(1), shiftTwo);

            System.out.println("\r\n-- COMPLETED --\r\n");
            for (int i = 2; i < 5; i++) {
                instance.getEventManager().setKitchenTaskAsCompleted(newSheet.getAllTasks().get(i));
            }

            ObservableList<KitchenTask> tasksAfter = newSheet.getAllTasks();
            for (KitchenTask task : tasksAfter) {
                System.out.println(tasksAfter.indexOf(task) + ". " + task.toString());
            }

            System.out.println("\r\n-- RESET SERVICE SHEET --\r\n");
            instance.getEventManager().resetSummarySheet(event, service2);
            System.out.println("TASKS");
            for (KitchenTask task : newSheet.getAllTasks()) {
                System.out.println(task.toString());
            }

/*

            System.out.println("\n-- DELETE KITCHEN TASK --\n");
            instance.getKitchenTaskManager().deleteKitchenTask(sheet, sheet.getAllTasks().get(4));
            ObservableList<KitchenTask> tasksAfter = sheet.getAllTasks();
            for (KitchenTask task : tasksAfter) {
                System.out.println(tasksAfter.indexOf(task) + ". " + task.toString());
            }

*/
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        } catch (EventException e) {
            System.out.println("Errore di EventException");
            e.printStackTrace();
        }
    }
}