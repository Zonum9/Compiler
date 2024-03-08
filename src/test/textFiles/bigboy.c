#include "stdio.h"
#include "stdlib.h"
#include "minic-stdlib.h"

struct C{
    struct A* ptr;
    int f1;
    int f2;
    char f3;
};
struct B{
    struct C cs[5];
};

struct A{
    struct C aC;
    struct B bs[4][10];
};

struct A initA(struct A x){
    x.ac= initC(struct )

    return x;
}