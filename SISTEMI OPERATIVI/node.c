#include "utils.h"
#include "node.h"
#include <stdbool.h>

int chiave;
int count_trans_in_pool;
struct transaction *pool = NULL;

void nodeFunction(int shm_id_data) {
    shared_data *data;
    struct msgbuffer msg_trans;
    struct msgbuffer *ptr_msg_trans;
    struct sembuf sops;
    struct transaction block[SO_BLOCK_SIZE];
    struct transaction *ptr_block = &block[0];
    bool check_block = false;

    int cur_i;
    struct sigaction sa;
    sigset_t my_mask;
    
    count_trans_in_pool = 0;
    chiave = shm_id_data;
    
    sa.sa_handler = handle_node;
    sa.sa_flags = 0;
    sigemptyset(&my_mask);
    sa.sa_mask = my_mask;
    if (sigaction(SIGTERM, &sa, NULL) == -1)
        fprintf(stderr, "Cannot set a user-defined handler for Signal #%d: %s\n",15, strsignal(15));
	
	ptr_msg_trans=&msg_trans;
    data = shmat(shm_id_data, NULL, 0);
    TEST_ERROR;

    pool = malloc(data->rules.SO_TP_SIZE * sizeof(*pool));

    while (1) {
        if(msgrcv(data->msq_id, &msg_trans, sizeof(msg_trans) - sizeof(long), getpid(), 0) < 0){
            fprintf(stderr, "Error #%03d: %s \n", errno, strerror(errno));
		}else if(count_trans_in_pool < data->rules.SO_TP_SIZE){
        	write_transaction_pool(ptr_msg_trans, pool);
		}else{
            kill(msg_trans.t.sender, SIGUSR2);
        }

        /* --- creazione blocck condidato --- */
        if(count_trans_in_pool >= SO_BLOCK_SIZE-2) {
            create_block(ptr_block, pool);
            check_block = true;
        	wait_rand_lapse(data->rules.SO_MIN_TRANS_PROC_NSEC, data->rules.SO_MAX_TRANS_PROC_NSEC);
        }

        /* --- scrittura blocco sul libo mastro ---*/
        if (check_block) {
                LOCK_TURNSTILE;
                LOCK_RW_MUTEX;
            if (data->i_block_mastro != SO_REGISTRY_SIZE) {
                cur_i = data->i_block_mastro;
                data->i_block_mastro++;
                write_block(cur_i, ptr_block, data);
                UNLOCK_TURNSTILE;
                UNLOCK_RW_MUTEX;
                count_trans_in_pool -= SO_BLOCK_SIZE-2;
                check_block = false;
            } else {
                /* --- libro mastro pieno--- */
                kill(getppid(), SIGUSR1);
                break;
            }
        } else {
            count_trans_in_pool ++;
        }
    }
    raise(SIGTERM);
	shmdt(data);
}


void handle_node(int signal){
	int i = 0;
	shared_data *data;
	struct pid_and_value *ptr_budget_nodes;
	data = shmat(chiave, NULL, 0);
	ptr_budget_nodes = shmat(data->shm_id_budget_nodes, NULL, 0);

	while(ptr_budget_nodes[i].pid != getpid()){
        i++;
    }
 	ptr_budget_nodes[i].value = count_trans_in_pool+1;

    free(pool);
    exit(0);
}

void write_transaction_pool(struct msgbuffer *ptr_msg_trans, struct transaction *pool) {
    pool[count_trans_in_pool].timestamp = ptr_msg_trans->t.timestamp;
    pool[count_trans_in_pool].sender = ptr_msg_trans->t.sender;
    pool[count_trans_in_pool].receiver = ptr_msg_trans->t.receiver;
    pool[count_trans_in_pool].quantity = ptr_msg_trans->t.quantity;
    pool[count_trans_in_pool].reward = ptr_msg_trans->t.reward;
}

void create_block(struct transaction *ptr_block, struct transaction *pool){
    struct timespec now;
    int i;
    int total_reward = 0;
    long timestamp;

    clock_gettime(CLOCK_REALTIME, &now);
    timestamp = now.tv_nsec;

    for(i = 0; i < SO_BLOCK_SIZE-1; i++){
        ptr_block[i].timestamp = pool[i].timestamp;
        ptr_block[i].sender = pool[i].sender;
        ptr_block[i].receiver = pool[i].receiver;
        ptr_block[i].quantity = pool[i].quantity;
        ptr_block[i].reward = pool[i].reward;

        total_reward = total_reward + pool[i].reward;
    }
    ptr_block[i].timestamp = timestamp;
    ptr_block[i].sender = SENDER_REWARD;
    ptr_block[i].receiver = getpid();
    ptr_block[i].quantity = total_reward;
    ptr_block[i].reward = 0;
}

void write_block(int cur_i, struct transaction *ptr_block, shared_data *data) {
    int cur_j;
    for(cur_j = 0; cur_j < SO_BLOCK_SIZE; cur_j++) {
        data->libro_mastro[cur_i][cur_j].timestamp = ptr_block[cur_j].timestamp;
        data->libro_mastro[cur_i][cur_j].sender = ptr_block[cur_j].sender;
        data->libro_mastro[cur_i][cur_j].receiver = ptr_block[cur_j].receiver;
        data->libro_mastro[cur_i][cur_j].quantity = ptr_block[cur_j].quantity;
        data->libro_mastro[cur_i][cur_j].reward = ptr_block[cur_j].reward;
    }
}