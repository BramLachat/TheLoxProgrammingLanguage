#ifndef clox_chunk_h
#define clox_chunk_h

#include "common.h"

typedef enum {
    OP_RETURN,
} OpCode;

// this is simply a wrapper around an array of bytes
// count and capacity make it like a Java ArrayList
typedef struct {
    int count;
    int capacity;
    uint8_t* code;
} Chunk;

// Chunk constructor
void initChunk(Chunk* chunk);
void freeChunk(Chunk* chunk);
// To append a byte to the end of the chunk
void writeChunk(Chunk* chunk, unit8_t byte);

#endif