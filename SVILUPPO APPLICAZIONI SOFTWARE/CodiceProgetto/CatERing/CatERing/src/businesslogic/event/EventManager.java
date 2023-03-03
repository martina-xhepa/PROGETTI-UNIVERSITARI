package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.Procedure;
import businesslogic.user.User;
import javafx.collections.ObservableList;

import java.awt.*;
import java.util.ArrayList;

public class EventManager {
    private Service currentService;
    private ArrayList<EvEventReceiver> eventReceivers;

    public EventManager() {
        eventReceivers = new ArrayList<>();
    }

    public SummarySheet openSummarySheet(Event event, Service service) throws UseCaseLogicException, EventException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || event.getChef() == null) { throw new UseCaseLogicException(); }
        if(!event.isInCharge(user)){ throw new EventException(); }

        this.currentService = service;

        if ((this.currentService).hasSheet()==0) {

            SummarySheet sheetToOpen = this.currentService.defineSummarySheet();

            Menu serviceMenu = this.currentService.getMenu();

            for (Section sec : serviceMenu.getSections()) {
                for (MenuItem item : sec.getItems()) {
                    sheetToOpen.addKitchenTask(new KitchenTask(item.getItemRecipe()));
                }
            }
            for (MenuItem item : serviceMenu.getFreeItems()) {
                sheetToOpen.addKitchenTask(new KitchenTask(item.getItemRecipe()));
            }
            notifyServiceSheetCreated();
            notifyServiceUpdated();
            this.currentService.setSheet(this.currentService.getSummarySheet().getId());
        }

        return this.currentService.getSummarySheet();
    }
    public SummarySheet resetSummarySheet(Event event, Service service) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || this.currentService.hasSheet()==0) { throw new UseCaseLogicException(); }

        ArrayList<KitchenTask> tasksToDelete = new ArrayList<>();
        for (KitchenTask task : this.currentService.getSummarySheet().getAllTasks()) {
            if (!this.currentService.getMenu().hasRecipe(task.getKitchenProcedure())) {
                tasksToDelete.add(task);
            } else {
                task.setTimeRequired(0);
                task.setCompleted(false);
                task.setQuantity("");
                task.setCook(null);
                if (task.getKitchenShift() != null) {
                    task.getKitchenShift().unassignKitchenTask(task);
                    task.setKitchenShift(null);
                }

                notifyKitchenTaskReset(task);
            }
        }

        for (KitchenTask task : tasksToDelete) {
            deleteKitchenTask(task);
        }

        return this.currentService.getSummarySheet();
    }
    public KitchenTask insertKitchenTask(Procedure proc) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || this.currentService.hasSheet()==0) {throw new UseCaseLogicException();}

        KitchenTask newTask = new KitchenTask(proc);
        this.currentService.getSummarySheet().addKitchenTask(newTask);
        notifyKitchenTaskAdded(newTask);

        return newTask;
    }
    public void deleteKitchenTask(KitchenTask task) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || this.currentService.hasSheet()==0 || !this.currentService.getSummarySheet().hasKitchenTask(task)) { throw new UseCaseLogicException(); }
        this.currentService.getSummarySheet().removeKitchenTask(task);
        if (task.getKitchenShift() != null) {
            task.getKitchenShift().unassignKitchenTask(task);
        }
        notifyKitchenTaskDeleted(task);
    }
    public void moveKitchenTask(KitchenTask task, int position) throws UseCaseLogicException, EventException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || this.currentService.hasSheet()==0 || !this.currentService.getSummarySheet().hasKitchenTask(task)) { throw new UseCaseLogicException(); }
        if (position < 0 || position >= this.currentService.getSummarySheet().getAllTasks().size()){ throw new EventException(); }

        this.currentService.getSummarySheet().moveKitchenTask(task, position);
        notifyKitchenTasksRearranged();
    }
    public void modifyShift(KitchenTask task, KitchenShift shift) throws UseCaseLogicException, EventException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !this.currentService.getSummarySheet().hasKitchenTask(task) || this.currentService.hasSheet()==0) { throw new UseCaseLogicException(); }
        if (shift.isFull() || (task.getCook() != null && !shift.hasCookAvailable(task.getCook()))){ throw new EventException();}

        if (task.getKitchenShift() != null) {
            task.getKitchenShift().unassignKitchenTask(task);
        }

        shift.assignKitchenTask(task);

        task.setKitchenShift(shift);

        notifyKitchenTaskReassigned(task);
    }
    public void modifyCook(KitchenTask task, User cook) throws UseCaseLogicException, EventException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || this.currentService.hasSheet()==0 || !this.currentService.getSummarySheet().hasKitchenTask(task)) { throw new UseCaseLogicException();}
        if (task.getKitchenShift() != null && !task.getKitchenShift().hasCookAvailable(cook)) { throw new EventException(); }

        task.setCook(cook);
        notifyKitchenTaskUpdated(task);
    }
    public void modifyTimeRequired(KitchenTask task, int esteemedTimeRequired) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || this.currentService.hasSheet()==0 || !this.currentService.getSummarySheet().hasKitchenTask(task)) {
            throw new UseCaseLogicException();
        }

        task.setTimeRequired(esteemedTimeRequired);
        notifyKitchenTaskUpdated(task);
    }
    public void modifyQuantity(KitchenTask task, String quantity) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() ||  this.currentService.hasSheet()==0 || !this.currentService.getSummarySheet().hasKitchenTask(task)) {
            throw new UseCaseLogicException();
        }

        task.setQuantity((quantity != null) ? quantity : "");
        notifyKitchenTaskUpdated(task);
    }
    public void setKitchenTaskAsCompleted(KitchenTask task) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !this.currentService.getSummarySheet().hasKitchenTask(task)) { throw new UseCaseLogicException();}

        task.setCompleted(true);
        notifyKitchenTaskUpdated(task);
    }
    public ObservableList<KitchenShift> examineShiftsBoard() throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) { throw new UseCaseLogicException(); }
        return KitchenShift.loadShiftsBoard();
    }
    public void setKitchenShiftFull(KitchenShift shift, boolean full) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) { throw new UseCaseLogicException(); }

        shift.setFull(full);
        notifyKitchenShiftFull(shift);
    }


    // EVENT RECEIVERS NOTIFY METHODS
    private void notifyServiceSheetCreated() {
        for (EvEventReceiver er : this.eventReceivers) {
            er.updateServiceSheetCreated(this.currentService.getSummarySheet(), this.currentService.getId());
        }
    }
    private void notifyKitchenTaskReset(KitchenTask task) {
        for (EvEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskReset(this.currentService.getSummarySheet(), task);
        }
    }
    private void notifyKitchenTaskDeleted(KitchenTask task) {
        for (EvEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskDeleted(this.currentService.getSummarySheet(), task);
        }
    }
    private void notifyKitchenTaskUpdated(KitchenTask task) {
        for (EvEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskUpdated(this.currentService.getSummarySheet(), task);
        }
    }
    private void notifyKitchenTasksRearranged() {
        for (EvEventReceiver er : this.eventReceivers) {
            er.updateKitchenTasksRearranged(this.currentService.getSummarySheet());
        }
    }
    private void notifyKitchenShiftFull(KitchenShift shift) {
        for (EvEventReceiver er : this.eventReceivers) {
            er.updateKitchenShiftFull(shift);
        }
    }
    private void notifyKitchenTaskReassigned(KitchenTask task) {
        for (EvEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskReassigned(this.currentService.getSummarySheet(), task);
        }
    }
    private void notifyKitchenTaskAdded(KitchenTask task) {
        for (EvEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskAdded(this.currentService.getSummarySheet(), task);
        }
    }

    private void notifyServiceUpdated() {
        for (EvEventReceiver er : this.eventReceivers) {
            er.updateServiceUpdated(this.currentService, this.currentService.getSummarySheet().getId());
        }
    }

    public void addEventReceiver(EvEventReceiver rec) {
        this.eventReceivers.add(rec);
    }
    public void removeEventReceiver(EvEventReceiver rec) {
        this.eventReceivers.remove(rec);
    }

}
