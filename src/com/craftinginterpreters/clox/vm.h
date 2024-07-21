#ifndef clox_vm_h
#define clox_vm_h

#include "chunk.h"
#include "object.h"
#include "table.h"
#include "value.h"

#define STACK_MAX (FRAMES_MAX * UINT8_COUNT)
#define FRAMES_MAX 64

typedef  struct {
    ObjClosure* closure;
    uint8_t* ip;
    Value* slots;
} CallFrame;

// Since the stack array is declared directly inline in the VM struct, we don’t need to allocate it with malloc, ... .
// Every piece of memory allocated with something like malloc, ... is dynamic memory and is allocated on the heap.
typedef struct {
    CallFrame frames[FRAMES_MAX];
    int frameCount;
    Value stack[STACK_MAX];
    Value* stackTop;
    Table globals;
    Table strings;
    Obj* objects;
} VM;

typedef enum {
    INTERPRET_OK,
    INTERPRET_COMPILE_ERROR,
    INTERPRET_RUNTIME_ERROR
} InterpretResult;

extern VM vm;

void initVM();
void freeVM();
InterpretResult interpret(const char* source);
void push(Value value);
Value pop();

#endif
