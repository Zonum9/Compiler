.globl main
.text
fun:
pushRegisters

add v0,v0,v0
add v0,v1,v1
add v0,v2,v2
add v0,v3,v3
add v0,v4,v4
add v0,v5,v5
add v0,v6,v6
add v0,v7,v7
add v0,v8,v8
add v0,v9,v9


addi v0,$a0,0
add v0,v0,v0
addi $a0,v0,0
li $v0,1
syscall
popRegisters
addi $a0,v0,0
jr $ra

.text
main:
.global _start
_start:
li v0,180
li v1,1
li v2,1
li v3,1
li v4,1
li v5,1
li v6,1
li v7,1
li v8,1
li v9,1
li v10,1
li v11,1
li v12,1
li v13,1
li v14,1
li v15,1
li v16,1
li v17,1
li v18,1
li v19,1
li v20,1

add v0,v0,v0
addi $a0,v0,0
li $v0,1
syscall
jal fun
li $v0, 10
syscall



