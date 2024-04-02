.data
.globl main

.text
main:
j start
jumpto:
addiu $a0,v1,0
li $v0,1
syscall
jr $ra
start:
li v0,77
li v1,99
jal jumpto
addiu $a0,v0,0
li $v0,1
syscall
jal end



end: