.data
result:
.space 20
newline:
.asciiz "\n"

.text
.globl main
main:
li $t4,10
li $t3,0
li $t5,1
li $t6,2
la $t7,result
fib_loop:
beq $t6,$t4,fib_done
sw $t3,0($t7)
addiu $t7,$t7,4
addu $t3,$t3,$t5
addiu $t5,$t3,0
addiu $t6,$t6,-1
j fib_loop
fib_done:
la $t7,result
li $t6,0
li $v0,4
print_loop:
beq $t6,$t4,print_done
lw $a0,0($t7)
li $v0,1
syscall
li $v0,4
la $a0,newline
syscall
addiu $t7,$t7,4
addiu $t6,$t6,1
j print_loop
print_done:
li $v0,10
syscall

.data
# Allocated labels for used registers
label_9_$t5:
.space 4
label_11_$t7:
.space 4
label_8_$t3:
.space 4
label_7_$t4:
.space 4
label_10_$t6:
.space 4

