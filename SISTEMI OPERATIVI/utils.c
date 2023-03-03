#include "utils.h"

void configFunction(int shm_id_data) {
    shared_data * data;
    FILE *config;
    int i = 0;
    int file[13];
    char buf[50];

    data = shmat(shm_id_data, NULL, 0);
    config = fopen("config.cfg", "r");
    if (config == NULL) {
        TEST_ERROR;
        exit(0);
    }
    while (fgets(buf, sizeof buf, config) != NULL) {
        if (buf[0] != '#') {
            file[i] = atoi(buf);
            i++;
        }
    }
    fclose(config);

    data->num_users = file[0];
    data->rules.SO_USERS_NUM = file[0];
    data->rules.SO_NODES_NUM = file[1];
    data->rules.SO_BUDGET_INIT = file[2];
    data->rules.SO_REWARD = file[3];
    data->rules.SO_MIN_TRANS_GEN_NSEC = file[4];
    data->rules.SO_MAX_TRANS_GEN_NSEC = file[5];
    data->rules.SO_RETRY = file[6];
    data->rules.SO_TP_SIZE = file[7];
    data->rules.SO_MIN_TRANS_PROC_NSEC = file[8];
    data->rules.SO_MAX_TRANS_PROC_NSEC = file[9];
    data->rules.SO_SIM_SEC = file[10];
    data->rules.SO_NUM_FRIENDS = file[11];
    data->rules.SO_HOPS = file[12];
    data->rules.NUM_PROC_CHILD = file[0] + file[1];
}

void wait_rand_lapse(long min, long max) {
    long waiting_time;
    struct timespec time;
    srand(getpid());
    waiting_time = min + rand() % max;
    time.tv_sec = 0;
    time.tv_nsec = waiting_time;

    nanosleep(&time, NULL);
}