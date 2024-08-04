cmake_minimum_required(VERSION 3.22.1)

project("org.space.pmmp")

add_library(pmmp-natives SHARED
        process.c
        process_info.c)

target_link_libraries(pmmp-natives
        android
        log)
