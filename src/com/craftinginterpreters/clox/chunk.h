#ifndef clox_chunk_h
#define clox_chunk_h

#include "common.h"
#include "value.h"

typedef enum {
    OP_CONSTANT,
    OP_NIL,
    OP_TRUE,
    OP_FALSE,
    OP_POP,
    OP_GET_LOCAL,
    OP_SET_LOCAL,
    OP_GET_GLOBAL,
    OP_DEFINE_GLOBAL,
    OP_SET_GLOBAL,
    OP_GET_UPVALUE,
    OP_SET_UPVALUE,
    OP_EQUAL,
    OP_GREATER,
    OP_LESS,
    OP_ADD,
    OP_SUBSTRACT,
    OP_MULTIPLY,
    OP_DEVIDE,
    OP_NOT,
    OP_NEGATE,
    OP_PRINT,
    OP_JUMP,
    OP_JUMP_IF_FALSE,
    OP_LOOP,
    OP_CALL,
    OP_CLOSURE,
    OP_RETURN,
} OpCode;

// this is simply a wrapper around an array of bytes
// count and capacity make it like a Java ArrayList
typedef struct {
    int count;
    int capacity;
    uint8_t* code;
    int* lines; // Each number in the array is the line number for the corresponding byte in the bytecode.
    ValueArray constants;
} Chunk;

// Chunk constructor
void initChunk(Chunk* chunk);
void freeChunk(Chunk* chunk);
// To append a byte to the end of the chunk
void writeChunk(Chunk* chunk, uint8_t byte, int line);
int addConstant(Chunk* chunk, Value value);

#endif
