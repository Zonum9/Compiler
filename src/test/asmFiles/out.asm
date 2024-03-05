.data

.data
# Allocated labels for virtual registers
label_6_v2:
.space 4
label_3_v1:
.space 4
label_4_v0:
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
la $t0,label_6_v2
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_3_v1
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_4_v0
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
# ------Var decl for x
# Original instruction: addiu $sp,$sp,-4
addiu $sp,$sp,-4
# ----Start of Block----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of VarExpr----
# Original instruction: addi v0,$fp,-8
addi $t5,$fp,-8
la $t0,label_4_v0
sw $t5,0($t0)
# ----End of of VarExpr----
# ----Start of IntLiteral----
# Original instruction: li v1,9
li $t5,9
la $t0,label_3_v1
sw $t5,0($t0)
# ----End of IntLiteral----
# Original instruction: sw v1,0(v0)
la $t5,label_3_v1
lw $t5,0($t5)
la $t4,label_4_v0
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of Continue----
# ----End of Continue----
# ----Start of Break----
# ----End of Break----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of VarExpr----
# Original instruction: lw v2,-8($fp)
lw $t5,-8($fp)
la $t0,label_6_v2
sw $t5,0($t0)
# ----End of VarExpr----
# Original instruction: addi $a0,v2,0
la $t5,label_6_v2
lw $t5,0($t5)
addi $a0,$t5,0
# Original instruction: li $v0,1
li $v0,1
# Original instruction: syscall
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----End of Block----
# Original instruction: li $v0,10
li $v0,10
# Original instruction: syscall
syscall

