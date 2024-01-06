#include <stdio.h>
#include "common.h"
#include "vm.h"
#include "debug.h"

// What I’m doing here is a global variable, 
// and everything bad you’ve heard about global variables is still true when programming in the large. 
// But when keeping things small for a book . . . 
VM vm;

static void resetStack() {
    // Since the stack array is declared directly inline in the VM struct, 
    // we don’t need to allocate it. 
    // We don’t even need to clear the unused cells in the array — 
    // we simply won’t access them until after values have been stored in them. 
    // The only initialization we need is to set stackTop to point to the beginning of the array to indicate that the stack is empty.
    vm.stackTop = vm.stack;
}

void initVM() {
    resetStack();
}

void freeVM() {

}

static InterpretResult run() {
#define READ_BYTE() (*vm.ip++)
#define READ_CONSTANT() (vm.chunk->constants.values[READ_BYTE()])

    for(;;) {
#ifdef DEBUG_TRACE_EXECUTION
    printf("          ");
    for (Value* slot = vm.stack; slot < vm.stackTop; slot++) {
        printf("[ ");
        printValue(*slot);
        printf(" ]");
    }
    printf("\n");
    disassembleInstruction(vm.chunk, (int) (vm.ip - vm.chunk->code));
#endif

        uint8_t instruction;
        switch (instruction = READ_BYTE()) {
            case OP_CONSTANT: {
                Value constant = READ_CONSTANT();
                push(constant);
                break;
            }
            case OP_RETURN: {
                printValue(pop());
                printf("\n");
                return INTERPRET_OK;
            }
        }
    }

#undef READ_BYTE
#undef READ_CONSTANT
}

InterpretResult interpret(Chunk* chunk) {
    vm.chunk = chunk;
    vm.ip = vm.chunk->code;
    return run();
}

void push(Value value) {
    *vm.stackTop = value;
    vm.stackTop++;
}

Value pop() {
    vm.stackTop--;
    return *vm.stackTop;
}