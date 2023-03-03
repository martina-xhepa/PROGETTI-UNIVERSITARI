#include "utils.h"
#include "master.h"
#include "node.h"
#include "user.h"

int shm_id_data;
enum termination_status {
    RUNNING = 0, TIMEOUT = 1, FULL_BOOK = 2, ALL_USERS_FINISHED = 3, SIGNAL_SIGINT = 4
};
enum termination_status terminationStatus = RUNNING;

int main() {
    int i;
    int *write_users, *write_nodes;
    struct pid_and_value *ptr_budget_nodes;
    pid_t child_pid;
    shared_data *data;
    struct sembuf sops;
    struct sigaction sa;
    struct timespec time;
    sigset_t my_mask;

    /* --- creazione e attach shared_memory data --- */
    shm_id_data = shmget(IPC_PRIVATE, sizeof(*data), 0600);
    data = shmat(shm_id_data, NULL, 0);
    TEST_ERROR;

    configFunction(shm_id_data);

    /* --- creazione chiavi --- */
    data->shm_id_users = shmget(IPC_PRIVATE, data->rules.SO_USERS_NUM * sizeof(int), 0600);
    data->shm_id_nodes = shmget(IPC_PRIVATE, data->rules.SO_NODES_NUM * sizeof(int), 0600);
    data->shm_id_budget_nodes = shmget(IPC_PRIVATE, data->rules.SO_NODES_NUM * sizeof(struct pid_and_value), 0600);
    data->sem_id = semget(IPC_PRIVATE, 8, 0600);
    data->msq_id = msgget(getpid(), IPC_CREAT | 0600);
    TEST_ERROR;

    /* --- attach delle memorie condivise (processi figli) --- */
    write_users = shmat(data->shm_id_users, NULL, 0);
    write_nodes = shmat(data->shm_id_nodes, NULL, 0);
    ptr_budget_nodes = shmat(data->shm_id_budget_nodes, NULL, 0);
    TEST_ERROR;

    /* --- creazione semafori --- */
    semctl(data->sem_id, WAITZERO, SETVAL, data->rules.NUM_PROC_CHILD + 1);
    semctl(data->sem_id, MUTEX, SETVAL, 1);
    semctl(data->sem_id, RW_MUTEX, SETVAL, 1);
    semctl(data->sem_id, TURNSTILE, SETVAL, 1);
    semctl(data->sem_id, MUTEX2, SETVAL, 1);
    semctl(data->sem_id, RW_MUTEX2, SETVAL, 1);
    semctl(data->sem_id, TURNSTILE2, SETVAL, 1);
    TEST_ERROR;

    data->i_block_mastro = 0;
    data->read_counter = 0;
    data->user_read_counter = 0;

    /* --- fork creazioni figli --- */
    for (i = 0; i < data->rules.NUM_PROC_CHILD; i++) {
        switch (child_pid = fork()) {
            case -1:
                fprintf(stderr, "Error #%03d: %s\n", errno, strerror(errno));
                break;
/* ------------------------------------------------------------------------------------------------------------------ */
            case 0:
                if (i < data->rules.SO_USERS_NUM) {
                    /* codice processi users */

                    sops.sem_num = WAITZERO;
                    sops.sem_op = -1;
                    semop(data->sem_id, &sops, 1);
                    sops.sem_op = 0;
                    semop(data->sem_id, &sops, 1);

                    /* --- simulation start --- */
                    userFunction(shm_id_data);

                    exit(0);
                } else {
/* ------------------------------------------------------------------------------------------------------------------ */
                    /* codice processi nodes */

                    sops.sem_num = WAITZERO;
                    sops.sem_op = -1;
                    semop(data->sem_id, &sops, 1);
                    sops.sem_op = 0;
                    semop(data->sem_id, &sops, 1);

                    /* --- simulation start --- */
                    nodeFunction(shm_id_data);

                    exit(0);
                }
                break;
/* ------------------------------------------------------------------------------------------------------------------ */
            default:
                if (i < data->rules.SO_USERS_NUM) {
                    write_users[i] = child_pid;
                } else {
                    write_nodes[i - data->rules.SO_USERS_NUM] = child_pid;
                    ptr_budget_nodes[i - data->rules.SO_USERS_NUM].pid = child_pid;
                }
                break;
        }
    }

    /* --- gestione segnale ---*/
    sa.sa_handler = handle_signal;
    sa.sa_flags = 0;
    sigemptyset(&my_mask);
    sa.sa_mask = my_mask;
    if (sigaction(SIGALRM, &sa, NULL) == -1)
        fprintf(stderr, "Cannot set a user-defined handler for Signal #%d: %s\n",14, strsignal(14));
    if (sigaction(SIGINT, &sa, NULL) == -1)
        fprintf(stderr, "Cannot set a user-defined handler for Signal #%d: %s\n",2, strsignal(2));
    if (sigaction(SIGUSR2, &sa, NULL) == -1)
        fprintf(stderr, "Cannot set a user-defined handler for Signal #%d: %s\n",12, strsignal(12));
    if (sigaction(SIGUSR1, &sa, NULL) == -1)
        fprintf(stderr, "Cannot set a user-defined handler for Signal #%d: %s\n",10, strsignal(10));

    alarm(data->rules.SO_SIM_SEC);

    sops.sem_num = WAITZERO;
    sops.sem_op = -1;
    semop(data->sem_id, &sops, 1);
    sops.sem_num = 0;
    sops.sem_op = 0;
    semop(data->sem_id, &sops, 1);


    while (terminationStatus == RUNNING) {
        printf("------------------------------------------------------------\n");
        printf("utenti attivi:  %d\n", data->num_users);

        LOCK_TURNSTILE;
        UNLOCK_TURNSTILE;

        LOCK_MUTEX;
        data->read_counter = data->read_counter + 1;
        if (data->read_counter == 1) { LOCK_RW_MUTEX; }
        UNLOCK_MUTEX;

        printf("USER:\n");
        balance_child(data->rules.SO_USERS_NUM, write_users, 0, data);
        printf("NODE:\n");
        balance_child(data->rules.SO_NODES_NUM, write_nodes, 1, data);

        LOCK_MUTEX;
        data->read_counter = data->read_counter - 1;
        if (data->read_counter == 0) { UNLOCK_RW_MUTEX; }
        UNLOCK_MUTEX;

        printf("------------------------------------------------------------\n");

        time.tv_sec = 1;
        time.tv_nsec = 0;
        nanosleep(&time, NULL);
    }

    print_summary(data, write_users, write_nodes, ptr_budget_nodes);

    /* deallocation */
    shmdt(write_users);
    shmdt(write_nodes);
    shmdt(ptr_budget_nodes);
    shmctl(data->shm_id_users, IPC_RMID, NULL);
    shmctl(data->shm_id_nodes, IPC_RMID, NULL);
    shmctl(data->shm_id_budget_nodes, IPC_RMID, NULL);
    semctl(data->sem_id, 0, IPC_RMID);
    msgctl(data->msq_id, IPC_RMID, NULL);

    shmdt(data);
    shmctl(shm_id_data, IPC_RMID, NULL);
}

/* --- functions ---*/

void balance_child(int num_pid, int *write_pid, int type, shared_data *data) {
    struct pid_and_value larger_budgets[MAXELEM];
    struct pid_and_value smaller_budgets[MAXELEM];
    struct pid_and_value *ptr_mag = &larger_budgets[0];
    struct pid_and_value *ptr_min = &smaller_budgets[0];
    int i, k, budget;
    int j = 0;
    int min_of_max, max_of_min;

    for (i = 0; i < num_pid; i++) {
        if (type == 0) {
            budget = budget_user(write_pid[i], data);
        } else if (type == 1) {
            budget = total_reward(write_pid[i], data);
        }

        if (j < MAXELEM) {
            larger_budgets[j].pid = write_pid[i];
            larger_budgets[j].value = budget;
            smaller_budgets[j].pid = write_pid[i];
            smaller_budgets[j].value = budget;

            j++;
        } else {
            min_of_max = min(ptr_mag);
            max_of_min = max(ptr_min);

            if (budget > min_of_max) {
                for (k = 0; larger_budgets[k].value != min_of_max; k++) {}
                larger_budgets[k].pid = write_pid[i];
                larger_budgets[k].value = budget;
            }
            if (budget < max_of_min) {
                for (k = 0; smaller_budgets[k].value != max_of_min; k++) {}
                smaller_budgets[k].pid = write_pid[i];
                smaller_budgets[k].value = budget;
            }
        }
    }
    printf("budget maggiore\n");
    for (i = 0; i < MAXELEM; i++) {
        printf("\t\tPID: %d  - VALUE: %d\n", larger_budgets[i].pid, larger_budgets[i].value);
    }
    printf("budget minore \n");
    for (i = 0; i < MAXELEM; i++) {
        printf("\t\tPID: %d - VALUE: %d\n", smaller_budgets[i].pid, smaller_budgets[i].value);
    }
}

int min(struct pid_and_value *ptr_mag) {
    int i;
    int min = ptr_mag[0].value;
    for (i = 1; i < MAXELEM; i++) {
        if (ptr_mag[i].value < min) {
            min = ptr_mag[i].value;
        }
    }
    return min;
}

int max(struct pid_and_value *ptr_min) {
    int i;
    int max = ptr_min[0].value;
    for (i = 1; i < MAXELEM; i++) {
        if (ptr_min[i].value > max) {
            max = ptr_min[i].value;
        }
    }
    return max;
}

void handle_signal(int signal) {
    switch (signal) {
        case SIGALRM:
            terminationStatus = TIMEOUT;
            break;
        case SIGINT:
            terminationStatus = SIGNAL_SIGINT;
            break;
        case SIGUSR1:
            terminationStatus = FULL_BOOK;
            break;
        case SIGUSR2:
            terminationStatus = ALL_USERS_FINISHED;
            break;
    }
    kill_processes();
}

void kill_processes() {
    shared_data *data;
    int *write_users, *write_nodes;
    int i;
    data = shmat(shm_id_data, NULL, 0);
    write_users = shmat(data->shm_id_users, NULL, 0);
    write_nodes = shmat(data->shm_id_nodes, NULL, 0);

    for (i = 0; i < data->rules.SO_USERS_NUM; i++) {
        kill(write_users[i], SIGTERM);
    }
    for (i = 0; i < data->rules.SO_NODES_NUM; i++) {
        kill(write_nodes[i], SIGTERM);
    }
}

void print_summary(shared_data *data, int *write_users, int *write_nodes, struct pid_and_value *ptr_budget_nodes) {
    int l;
    printf("\n ------------------------------ \n");
    printf("     RIEPILOGO SIMULAZIONE");
    printf("\n ------------------------------ \n");
    switch (terminationStatus) {
        case 1:
            printf("Causa terminazione: TIMEOUT \n\n");
            break;
        case 2:
            printf("Causa terminazione: FULL_BOOK\n\n");
            break;
        case 3:
            printf("Causa terminazione: ALL_USERS_FINISHED\n\n");
            break;
        case 4:
            printf("Causa terminazione: SIGNAL_SIGINT\n\n");
            break;
        default:
            break;
    }
    printf("Bilancio utenti: \n\n");
    print_balance_all_users(data->rules.SO_USERS_NUM, write_users, data);

    printf("Bilancio nodi:\n");
    print_balance_all_nodes(data->rules.SO_NODES_NUM, write_nodes, data);

    printf("Numero processi utente terminati prematuramente: %d\n\n", data->rules.SO_USERS_NUM - data->num_users);

    printf("Numero blocchi libro mastro: %d\n\n", data->i_block_mastro);

    printf("Numero transazione in pool \n");

    for (l = 0; l < data->rules.SO_NODES_NUM; l++) {
        printf("\t\tNODE: %d -- %d transazioni \n", ptr_budget_nodes[l].pid, ptr_budget_nodes[l].value);
    }
}

void print_balance_all_users(int num_users, int *write_users, shared_data *data) {
    int i;
    for (i = 0; i < num_users; i++) {
        printf("\t\tPID: %d -- blc: %d\n", write_users[i], budget_user(write_users[i], data));
    }
}

int budget_user(int user_pid, shared_data *data) {
    int i, j;
    int total_balance;

    total_balance = data->rules.SO_BUDGET_INIT;
    for (i = 0; i < SO_REGISTRY_SIZE; i++) {
        for (j = 0; j < SO_BLOCK_SIZE - 1; j++) {
            if (data->libro_mastro[i][j].receiver == user_pid) {
                total_balance = total_balance + data->libro_mastro[i][j].quantity;
            } else if (data->libro_mastro[i][j].sender == user_pid) {
                total_balance = total_balance - (data->libro_mastro[i][j].quantity + data->libro_mastro[i][j].reward);
            }
        }
    }
    return total_balance;
}

void print_balance_all_nodes(int num_nodes, int *write_nodes, shared_data *data) {
    int i;
    for (i = 0; i < num_nodes; i++) {
        printf("\t\tPID: %d -- blc: %d\n", write_nodes[i], total_reward(write_nodes[i], data));
    }
}

int total_reward(int node_pid, shared_data *data) {
    int i;
    int total_reward = 0;

    for (i = 0; i < data->i_block_mastro; i++) {
        if (data->libro_mastro[i][SO_BLOCK_SIZE - 1].receiver == node_pid) {
            total_reward = total_reward + data->libro_mastro[i][SO_BLOCK_SIZE - 1].quantity;
        }
    }
    return total_reward;
}

void print_pid(int num_pids, int *write_pids) {
    int i;
    for (i = 0; i < num_pids; i++) {
        printf("    %d", write_pids[i]);
    }
    printf("\n");
}

void print_book() {
    shared_data *data;
    int i, j;
    data = shmat(shm_id_data, NULL, 0);

    for (i = 0; i < data->i_block_mastro; i++) {
        for (j = 0; j < SO_BLOCK_SIZE; j++) {
            printf("------------------");
        }
        printf("\n");
        for (j = 0; j < SO_BLOCK_SIZE; j++) {
            printf("|tm: %ld    ", data->libro_mastro[i][j].timestamp);
        }
        printf("|\n");
        for (j = 0; j < SO_BLOCK_SIZE; j++) {
            printf("|snd: %d       ", data->libro_mastro[i][j].sender);
        }
        printf("|\n");
        for (j = 0; j < SO_BLOCK_SIZE; j++) {
            printf("|rcv: %d       ", data->libro_mastro[i][j].receiver);
        }
        printf("|\n");
        for (j = 0; j < SO_BLOCK_SIZE; j++) {
            printf("|qty: %d           ", data->libro_mastro[i][j].quantity);
        }
        printf("|\n");
        for (j = 0; j < SO_BLOCK_SIZE; j++) {
            printf("|rwd: %d           ", data->libro_mastro[i][j].reward);
        }
        printf("|\n");
    }
}
