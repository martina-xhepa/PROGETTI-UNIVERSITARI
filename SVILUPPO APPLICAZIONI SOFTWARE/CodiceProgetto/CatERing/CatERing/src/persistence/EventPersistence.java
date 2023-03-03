package persistence;

import businesslogic.event.*;


public class EventPersistence implements EvEventReceiver {
    @Override
    public void updateKitchenTaskUpdated(SummarySheet summarySheet, KitchenTask task) {
        KitchenTask.updateKitchenTask(task);
    }

    @Override
    public void updateServiceSheetCreated(SummarySheet summarySheet, int service_id) {
        SummarySheet.saveNewSummarySheet(summarySheet, service_id);
    }

    @Override
    public void updateKitchenTaskDeleted(SummarySheet summarySheet, KitchenTask task) {
        KitchenTask.deleteKitchenTask(summarySheet, task);
    }

    @Override
    public void updateKitchenTasksRearranged(SummarySheet summarySheet) {
        SummarySheet.saveKitchenTasksOrder(summarySheet);
    }

    @Override
    public void updateKitchenShiftFull(KitchenShift shift) {
        KitchenShift.updateKitchenShift(shift);
    }

    @Override
    public void updateKitchenTaskReassigned(SummarySheet summarySheet, KitchenTask task) {
        KitchenTask.updateTaskReassigned(task);
    }

    @Override
    public void updateKitchenTaskAdded(SummarySheet summarySheet, KitchenTask task) {
        KitchenTask.saveNewKitchenTask(summarySheet.getId(), task, summarySheet.getAllTasks().indexOf(task));
    }

    @Override
    public void updateKitchenTaskReset(SummarySheet summarySheet, KitchenTask task) {
        KitchenTask.updateKitchenTaskReset(summarySheet, task);
    }

    public void updateServiceUpdated(Service currentService, int sheet_id){
        Service.updateService(currentService, sheet_id);
    }
}