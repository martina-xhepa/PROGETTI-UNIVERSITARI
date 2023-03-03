#ifndef USER_H
#define USER_H
/**
 * function for the user life cycle
 */
void userFunction(int shm_id_data);

/**
 * handler of signals riceived by user process
 */
void handle_user(int signals);

/**
 * function that allows the user process to remove its pid form the array
 */
void remove_my_pid(shared_data *data, int *write_users);

 /**
  * function that allows to calculate the actual user's budget
  */
void calcolateBalance(shared_data *data);

/**
 * function that allows to randomly calculate the amount that the user has to pay
 */
void rand_quantiy_pay(int min, int reward);

/**
 * function to randomly choose the node thet processes the transaction
 */
int rand_process_node(int num_nodes, int *write_nodes);

/**
 * function to randomly choose the receiver of transaction
 */
int rand_process_receiver(shared_data *data, int *write_users);

/**
 * function for reading the ledger
 */
void read_ledger(shared_data *data);

/**
 * function to create the transaction
 */
void create_transaction(struct msgbuffer *ptr_msg_trans, shared_data *data, int *write_nodes, int *write_users);

#endif
