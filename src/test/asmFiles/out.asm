.data
label_4814_:
.asciiz ""
.align 2

.data
# Allocated labels for virtual registers
label_4816_v1462:
.space 4

.text
.globl main
main:
# Original instruction: addiu $sp,$sp,-4
addiu $sp,$sp,-4
# Original instruction: sw $fp,0($sp)
sw $fp,0($sp)
# Original instruction: addi $fp,$sp,0
addi $fp,$sp,0
# Original instruction: addiu $sp,$sp,-4
addiu $sp,$sp,-4
# Original instruction: sw $ra,0($sp)
sw $ra,0($sp)
# Original instruction: pushRegisters
la $t0,label_4816_v1462
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
# ----Start of Block----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of TypecastExpr----
# ----Start of StrLiteral----
# ----Start of StrLiteral----
# Original instruction: la v1462,label_4814_
la $t5,label_4814_
la $t0,label_4816_v1462
sw $t5,0($t0)
# ----End of StrLiteral----
# ----End of of StrLiteral----
# ----End of TypecastExpr----
# Original instruction: addi $a0,v1462,0
la $t5,label_4816_v1462
lw $t5,0($t5)
addi $a0,$t5,0
# Original instruction: li $v0,4
li $v0,4
# Original instruction: syscall
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----End of Block----
# Original instruction: li $v0,10
li $v0,10
# Original instruction: syscall
syscall

