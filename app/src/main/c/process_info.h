#ifndef PROCSTAT_H
#define PROCSTAT_H

#include <linux/types.h>
#include <sys/types.h>

#ifndef TASK_COMM_LEN
#define TASK_COMM_LEN 16
#endif // TASK_COMM_LEN

#ifndef MAX_PROCSTAT_LINE_SIZE
#define MAX_PROCSTAT_LINE_SIZE 4096
#endif // MAX_PROCSTAT_LINE_SIZE

#ifndef MAX_PROCSTATM_LINE_SIZE
#define MAX_PROCSTATM_LINE_SIZE 1024
#endif // MAX_PROCSTATM_LINE_SIZE

#define PROCSTAT_PATH "/proc/%i/stat"
#define PROCSTATM_PATH "/proc/%i/statm"

#define STAT_PID 0
#define STAT_COMM_IDX 1
#define STAT_PARENT_PID_IDX 3
#define STAT_USER_TIME_IDX 13
#define STAT_SYS_TIME_IDX 14
#define STAT_CHILD_USER_TIME_IDX 15
#define STAT_CHILD_SYS_TIME_IDX 16
#define STAT_NUM_THREADS_IDX 19

#define STATM_SIZE 0
#define STATM_RESIDENT 1
#define STATM_SHARED 2
#define STATM_TEXT 3
#define STATM_DATA 5

// https://man7.org/linux/man-pages/man5/proc_pid_stat.5.html
struct procstat_t {
    // Only relevant fields

    // The pid of the current process (0)
    pid_t pid;

    // The command name (1)
    char comm[TASK_COMM_LEN];

    // The pid of the parent process (3)
    pid_t parent_pid;

    // The cpu time taken in userland (13)
    clock_t user_time;

    // The cpu time taken in kernel-land (14)
    clock_t system_time;

    // The cpu time taken in userland by children processes (or threads) (15)
    clock_t child_user_time;

    // The cpu time taken in kernel-land by children processes (or threads) (16)
    clock_t child_system_time;

    // The number of threads spawned in this process (19)
    long num_threads;
};

struct procstatm_t {
    // The size of the program in memory
    long size;

    // Resident set size
    long resident;

    // Number of resident shared pages
    long shared;

    // Size of code segments
    long text;

    // Size of the data segment + stack
    long data;
};

int GetProcessStatus(pid_t pid, struct procstat_t *output);

int GetProcessMemory(pid_t pid, struct procstatm_t *stat);

#endif //PROCSTAT_H
