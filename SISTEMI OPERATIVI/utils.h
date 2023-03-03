#ifndef UTILS_H
#define UTILS_H
#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <time.h>
#include <string.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/msg.h>
#include <sys/sem.h>
#include <math.h>

#define SO_REGISTRY_SIZE 1000
#define SO_BLOCK_SIZE 10
#define SENDER_REWARD -1
#define MAXELEM  2

/* MACRO per definire numero semafori */
#define WAITZERO 0
#define MUTEX 1
#define RW_MUTEX 2
#define TURNSTILE 3
#define MUTEX2 4
#define RW_MUTEX2 5
#define TURNSTILE2 6

#define LOCK_MUTEX						\
	    sops.sem_num = MUTEX;			\
        sops.sem_op = -1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
#define UNLOCK_MUTEX					\
        sops.sem_num = MUTEX;			\
        sops.sem_op = 1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
        
#define LOCK_RW_MUTEX						\
	    sops.sem_num = RW_MUTEX;			\
        sops.sem_op = -1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
#define UNLOCK_RW_MUTEX					\
        sops.sem_num = RW_MUTEX;			\
        sops.sem_op = 1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
        
#define LOCK_TURNSTILE						\
	    sops.sem_num = TURNSTILE;			\
        sops.sem_op = -1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
#define UNLOCK_TURNSTILE					\
        sops.sem_num = TURNSTILE;			\
        sops.sem_op = 1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);

        
#define LOCK_MUTEX2					\
	    sops.sem_num = MUTEX2;			\
        sops.sem_op = -1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
#define UNLOCK_MUTEX2					\
        sops.sem_num = MUTEX2;			\
        sops.sem_op = 1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
        
#define LOCK_RW_MUTEX2					\
	    sops.sem_num = RW_MUTEX2;			\
        sops.sem_op = -1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
#define UNLOCK_RW_MUTEX2					\
        sops.sem_num = RW_MUTEX2;			\
        sops.sem_op = 1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
        
#define LOCK_TURNSTILE2						\
	    sops.sem_num = TURNSTILE2;			\
        sops.sem_op = -1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);
#define UNLOCK_TURNSTILE2					\
        sops.sem_num = TURNSTILE2;			\
        sops.sem_op = 1;			\
        sops.sem_flg = 0;			\
        semop(data->sem_id, &sops, 1);


#define TEST_ERROR    if (errno) {dprintf(STDERR_FILENO,		\
					  "%s:%d: PID=%5d: Error %d (%s)\n", \
					  __FILE__,			\
					  __LINE__,			\
					  getpid(),			\
					  errno,			\
					  strerror(errno));}


/**
 * defined a struct for configuration parameters
 */
typedef struct{
    int SO_USERS_NUM;
    int SO_NODES_NUM;
    int SO_BUDGET_INIT;
    int SO_REWARD;
    long SO_MIN_TRANS_GEN_NSEC;
    long SO_MAX_TRANS_GEN_NSEC;
    int SO_RETRY;
    int SO_TP_SIZE;
    long SO_MIN_TRANS_PROC_NSEC;
    long SO_MAX_TRANS_PROC_NSEC;
    int SO_SIM_SEC;
    int SO_NUM_FRIENDS;
    int SO_HOPS;
    int NUM_PROC_CHILD;
} settings;

/**
 * defined a struct for transaction
 */
struct transaction{
    long timestamp;
    int sender;
    int receiver;
    int quantity;
    int reward;
};

/**
 * defined a struct for message queue used by user and node processes
 */
struct msgbuffer{
    long mytype;
    struct transaction t;
};

/**
 * defined a generic struct to represent a process and its associated value
 */
struct pid_and_value {
    int pid;
    int value;
};

/**
 * defined a struct for shared memory
 */
typedef struct {
    settings rules;
    int num_users;
    int read_counter;
    int user_read_counter;
    int shm_id_budget_nodes;
    int shm_id_users;
    int shm_id_nodes;
    int sem_id;
    int msq_id;
    struct transaction libro_mastro[SO_REGISTRY_SIZE][SO_BLOCK_SIZE];
    int i_block_mastro;
}shared_data;

/**
 * function to read and set the rules of the simulation
 * @param shm_id_data
 */
void configFunction(int shm_id_data);

/**
 * function that waits for a random time lapse
 * @param min
 * @param max
 */
void wait_rand_lapse(long min, long max);

#endif
