#include <linux/limits.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include "process_info.h"

#define MIN(a, b) ({ int _a = (a); int _b = (b); ((_a) < (_b)) ? (_a) : (_b); })

int GetProcessStatus(pid_t pid, struct procstat_t *stat) {
    int got = 0;
    char line[MAX_PROCSTAT_LINE_SIZE] = {0};
    char filename[PATH_MAX] = {0};

    snprintf(filename, sizeof(filename), PROCSTAT_PATH, pid);

    int fd = open(filename, O_RDONLY);

    if (fd == -1) {
        return -1;
    }

    if ((got = read(fd, line, MAX_PROCSTAT_LINE_SIZE)) == -1) {
        close(fd);

        return -1;
    }

    close(fd);

    if (got < sizeof(line)) {
        line[got] = 0;
    }

    int idx = 0;
    char *tk = strtok(line, " ");
    while (tk != NULL) {
        switch (idx) {
            case STAT_PID:
                stat->pid = strtol(tk, NULL, 10);

                // Assert that we're reading from the same PID as we asked for
                assert(stat->pid == pid);
                break;

            case STAT_COMM_IDX:
                memcpy(stat->comm, tk, MIN(TASK_COMM_LEN, strlen(tk)));
                break;

            case STAT_PARENT_PID_IDX:
                stat->parent_pid = strtol(tk, NULL, 10);
                break;

            case STAT_USER_TIME_IDX:
                stat->user_time = strtol(tk, NULL, 10);
                break;

            case STAT_SYS_TIME_IDX:
                stat->system_time = strtol(tk, NULL, 10);
                break;

            case STAT_CHILD_USER_TIME_IDX:
                stat->child_user_time = strtol(tk, NULL, 10);
                break;

            case STAT_CHILD_SYS_TIME_IDX:
                stat->child_system_time = strtol(tk, NULL, 10);
                break;

            case STAT_NUM_THREADS_IDX:
                stat->num_threads = strtol(tk, NULL, 10);
                break;

            default:
                break;
        }

        idx++;
        tk = strtok(NULL, " ");
    }

    return 0;
}

int GetProcessMemory(pid_t pid, struct procstatm_t *stat) {
    int got = 0;
    char line[MAX_PROCSTATM_LINE_SIZE] = {0};
    char filename[PATH_MAX] = {0};

    snprintf(filename, sizeof(filename), PROCSTATM_PATH, pid);

    int fd = open(filename, O_RDONLY);

    if (fd == -1) {
        return -1;
    }

    if ((got = read(fd, line, MAX_PROCSTATM_LINE_SIZE)) == -1) {
        close(fd);

        return -1;
    }

    close(fd);

    if (got < sizeof(line)) {
        line[got] = 0;
    }

    int idx = 0;
    const char *tk = strtok(line, " ");
    while (tk != NULL) {
        switch (idx) {
            case STATM_SIZE:
                stat->size = strtol(tk, NULL, 10);
                break;
            case STATM_RESIDENT:
                stat->resident = strtol(tk, NULL, 10);
                break;
            case STATM_SHARED:
                stat->shared = strtol(tk, NULL, 10);
                break;
            case STATM_TEXT:
                stat->text = strtol(tk, NULL, 10);
                break;
            case STATM_DATA:
                stat->data = strtol(tk, NULL, 10);
                break;

            default:
                break;
        }

        idx++;
        tk = strtok(NULL, " ");
    }

    return 0;
}
