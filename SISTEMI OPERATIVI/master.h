#ifndef MASTER_H
#define MASTER_H
/**
 * print the pid and the budget of the processes with higher and lower budget
 */
void balance_child(int num_pid, int *write_pid, int type, shared_data *data);

/**
 * calculate the largest budget
 */
int min(struct pid_and_value *ptr_mag);

/**
 * calculate the smallest budget
 */
int max(struct pid_and_value *ptr_min);

/**
 * handler of signals riceived by master process
 */
void handle_signal(int signals);

/**
 * function to kill all child processes
 */
void kill_processes();

/**
 * function to print simulation summary
 */
void print_summary(shared_data *data, int *write_users, int *write_nodes, struct pid_and_value *ptr_budget_nodes);

/**
 * print the pid and the budget of user processes
 */
void print_balance_all_users(int num_users, int *write_users, shared_data *data);

 /**
  * calculate the user's budget by reading the ledger
  */
int budget_user(int user_pid, shared_data *data);

/**
 * print the pid and the budget of node processes
 */
void print_balance_all_nodes(int num_nodes, int *write_nodes, shared_data *data);

 /**
  * calculate the node's budget by reading the ledger
  */
int total_reward(int node_pid, shared_data *data);

/**
 * Print list of pid
 */
void print_pid(int num_pids, int *write_pids);

/**
 * print the ledget
 */
void print_book();

#endif
