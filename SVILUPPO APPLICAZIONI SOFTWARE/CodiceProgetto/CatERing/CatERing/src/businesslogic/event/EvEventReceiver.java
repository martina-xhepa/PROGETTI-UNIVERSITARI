package businesslogic.event;

public interface EvEventReceiver {
    void updateKitchenTaskUpdated(SummarySheet summarySheet, KitchenTask task);

    void updateServiceSheetCreated(SummarySheet summarySheet, int service_id);

    void updateKitchenTaskDeleted(SummarySheet summarySheet, KitchenTask task);

    void updateKitchenTasksRearranged(SummarySheet summarySheet);

    void updateKitchenShiftFull(KitchenShift shift);

    void updateKitchenTaskReassigned(SummarySheet summarySheet, KitchenTask task);

    void updateKitchenTaskAdded(SummarySheet summarySheet, KitchenTask task);

    void updateKitchenTaskReset(SummarySheet summarySheet, KitchenTask task);

    void updateServiceUpdated(Service currentService, int sheet_id);
}

