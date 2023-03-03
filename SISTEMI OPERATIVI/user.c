#include "utils.h"
#include "user.h"

#define CONDICTION_BILANCE 2
#define MIN_BILANCE 2

int so_retry_count;
int transaction_amount, transaction_reward, rand_amount;
int bilancio, uscite_totali, chiave;
struct sembuf sops;

void userFunction(int shm_id_data) {
    shared_data *data;
    struct msgbuffer msg_trans;
    struct msgbuffer *ptr_msg_trans;
    int *write_users, *write_nodes;
    struct sigaction sa;
    sigset_t my_mask, old_mask;

    /* Setup the handler */
    sa.sa_handler = &handle_user;
    sa.sa_flags = 0;
    sigemptyset(&my_mask);
    sa.sa_mask = my_mask;
    /* segnale per nuova transazione */
    if (sigaction(SIGUSR1, &sa, NULL) == -1)
        fprintf(stderr, "Cannot set a user-defined handler for Signal #%d: %s\n",10, strsignal(10));
    /* segnale dal nodo */
    if (sigaction(SIGUSR2, &sa, NULL) == -1)
        fprintf(stderr, "Cannot set a user-defined handler for Signal #%d: %s\n",12, strsignal(12));

    /* Set a mask with SIGUSR1 */
    sigaddset(&my_mask, SIGUSR1);

    /* --- attach shm --- */
    data = shmat(shm_id_data, NULL, 0);
    write_users = shmat(data->shm_id_users, NULL, 0);
    write_nodes = shmat(data->shm_id_nodes, NULL, 0);
    TEST_ERROR;

    bilancio = data->rules.SO_BUDGET_INIT;
    chiave = shm_id_data;
    uscite_totali = 0;
    so_retry_count = 0;
    ptr_msg_trans = &msg_trans;

    /* --- ciclo di vita delle transazioni --- */
    while (so_retry_count < data->rules.SO_RETRY) {

        sigprocmask(SIG_BLOCK, &my_mask, &old_mask);

        read_ledger(data);

        if (bilancio >= CONDICTION_BILANCE) {
            create_transaction(ptr_msg_trans, data, write_nodes, write_users);
            if (msg_trans.t.receiver == getpid()) {
                so_retry_count++;
            } else {
                /* --- invio transazione --- */
                if (msgsnd(data->msq_id, &msg_trans, sizeof(msg_trans) - sizeof(long), 0) < 0){
                    fprintf(stderr, "Error #%03d: %s \n", errno, strerror(errno));
                } else {
                    uscite_totali = uscite_totali + msg_trans.t.quantity + msg_trans.t.reward;
                    so_retry_count = 0;
                }
            }
            sigprocmask(SIG_SETMASK, &old_mask, NULL);

        } else {
            so_retry_count++;
        }
        wait_rand_lapse(data->rules.SO_MIN_TRANS_GEN_NSEC, data->rules.SO_MAX_TRANS_GEN_NSEC);
    }

    LOCK_TURNSTILE2;
    LOCK_RW_MUTEX2;
    remove_my_pid(data, write_users);
    UNLOCK_TURNSTILE2;
    UNLOCK_RW_MUTEX2;

    if (data->num_users == 0) {
        kill(getppid(), SIGUSR2);
    };

    shmdt(write_users);
    shmdt(write_nodes);
    shmdt(data);
}

void handle_user(int signal) {
    shared_data *data;
    struct msgbuffer signal_trans;
    struct msgbuffer *ptr_signal_trans;
    int *write_users, *write_nodes;

    switch (signal) {
        case SIGUSR1:
            printf("L'utente%d genera una transazione in risposta al segnale SIGUSR1\n", getpid());

            /* --- attach shm --- */
            data = shmat(chiave, NULL, 0);
            write_users = shmat(data->shm_id_users, NULL, 0);
            write_nodes = shmat(data->shm_id_nodes, NULL, 0);

            read_ledger(data);

            if (bilancio >= CONDICTION_BILANCE) {

                create_transaction(ptr_signal_trans, data, write_nodes, write_users);

                if (signal_trans.t.receiver == getpid()) {
                    so_retry_count++;
                    printf("USER: %d -a- transaction failed: RETRY: (%d) \n", getpid(), so_retry_count);
                } else {
                    /* --- invio transazione --- */
                    if (msgsnd(data->msq_id, &signal_trans, sizeof(signal_trans) - sizeof(long), 0) < 0) {
                        fprintf(stderr, "Error #%03d: %s \n", errno, strerror(errno));
                    } else {
                        uscite_totali = uscite_totali + signal_trans.t.quantity;
                        so_retry_count = 0;
                    }
                }
            } else {
                so_retry_count++;
                printf("PID: (%d) -- transaction failed: RETRY: (%d) \n", getpid(), so_retry_count);
            }
            wait_rand_lapse(data->rules.SO_MIN_TRANS_GEN_NSEC, data->rules.SO_MAX_TRANS_GEN_NSEC);
            break;

        case SIGUSR2:
            printf("\n --- Il nodo ha scartato una transazione dell'utente %d ---\n", getpid());
            break;

        default:
            printf("Non dovresti essere qui\n");
            break;
    }
}

void remove_my_pid(shared_data *data, int *write_users) {
    int i, j;
    j = 0;

    for (i = 0; i < (data->num_users - 1); i++) {
        if (write_users[i] == getpid()) {
            for (j = i; (j < data->num_users - 1); j++) {
                write_users[j] = write_users[j + 1];
            }
            write_users[j] = getpid();
        }
        if (j == data->num_users - 1) { break; }
    }
    data->num_users--;
}

void calcolateBalance(shared_data *data) {
    int i, j, entrate_libro_mastro, uscite_libro_mastro;
    entrate_libro_mastro = 0;
    uscite_libro_mastro = 0;

    for (i = 0; i < data->i_block_mastro; i++) {
        for (j = 0; j < SO_BLOCK_SIZE; j++) {
            if (getpid() == data->libro_mastro[i][j].receiver) {
                entrate_libro_mastro = entrate_libro_mastro + data->libro_mastro[i][j].quantity;
            } else if (getpid() == data->libro_mastro[i][j].sender) {
                uscite_libro_mastro =
                        uscite_libro_mastro + data->libro_mastro[i][j].quantity + data->libro_mastro[i][j].reward;
            }
        }
    }
    bilancio = data->rules.SO_BUDGET_INIT + entrate_libro_mastro - uscite_libro_mastro -
               (uscite_totali - uscite_libro_mastro);
}

void rand_quantiy_pay(int min, int reward) {
    srand(getpid());
    rand_amount = min + rand() % (bilancio - (min - 1));
    transaction_reward = ceil((rand_amount * reward) / 100);
    if (transaction_reward < 1) {
        transaction_reward = 1;
    }
    transaction_amount = rand_amount - transaction_reward;
}

int rand_process_node(int num_nodes, int *write_nodes) {
    int rand_i;
    srand(getpid());
    rand_i = rand() % num_nodes;

    return write_nodes[rand_i];
}

int rand_process_receiver(shared_data *data, int *write_users) {
    int rand_i;
    int receiver;

    LOCK_TURNSTILE2;
    UNLOCK_TURNSTILE2;

    LOCK_MUTEX2;
    data->user_read_counter = data->user_read_counter + 1;
    if (data->user_read_counter == 1) {
        LOCK_RW_MUTEX2;
    }
    UNLOCK_MUTEX2;

    srand(getpid());
    rand_i = rand() % data->num_users;
    receiver = write_users[rand_i];

    LOCK_MUTEX2;
    data->user_read_counter = data->user_read_counter - 1;
    if (data->user_read_counter == 0) {
        UNLOCK_RW_MUTEX2;
    }
    UNLOCK_MUTEX2;

    return receiver;
}

void read_ledger(shared_data *data) {
    LOCK_TURNSTILE;
    UNLOCK_TURNSTILE;

    LOCK_MUTEX;
    data->read_counter = data->read_counter + 1;
    if (data->read_counter == 1) { LOCK_RW_MUTEX; }
    UNLOCK_MUTEX;

    calcolateBalance(data);

    LOCK_MUTEX;
    data->read_counter = data->read_counter - 1;
    if (data->read_counter == 0) { UNLOCK_RW_MUTEX; }
    UNLOCK_MUTEX;
}

void create_transaction(struct msgbuffer *ptr_msg_trans, shared_data *data, int *write_nodes, int *write_users) {
    struct timespec now;
    long timestamp;

    clock_gettime(CLOCK_REALTIME, &now);
    timestamp = now.tv_nsec;

    rand_quantiy_pay(MIN_BILANCE, data->rules.SO_REWARD);

    ptr_msg_trans->mytype = rand_process_node(data->rules.SO_NODES_NUM, write_nodes);
    ptr_msg_trans->t.timestamp = timestamp;
    ptr_msg_trans->t.sender = getpid();
    ptr_msg_trans->t.receiver = rand_process_receiver(data, write_users);
    ptr_msg_trans->t.quantity = transaction_amount;
    ptr_msg_trans->t.reward = transaction_reward;
}
