#ifndef NODE_H
#define NODE_H
/**
 * function for the node life cycle
 */
void nodeFunction(int shm_id_data);

/**
 * handler of signals riceived by node process
 */
void handle_node(int signal);

/**
 * function that write the transaction in transaction pool
 */
void write_transaction_pool(struct msgbuffer *ptr_msg_trans, struct transaction *pool);

/**
 * function that creates a candidate block
 */
void create_block(struct transaction *ptr_block, struct  transaction *pool);

 /**
  * function that write the candidate block in the ledger
  */
void write_block(int cur_i, struct transaction *ptr_block, shared_data *data);

#endif