#include <stdint.h>
#include <stdio.h>

int main() {
	uint32_t two_bytes = 0b101010101111000011001100;
	printf("%b\n", two_bytes);
	two_bytes = two_bytes >> 8;
	printf("%b\n", two_bytes);
	printf("%b\n", two_bytes & 0xff);
	return 0;
}
