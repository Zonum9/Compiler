.data
label_0_:
.asciiz "First "
.align 2
label_1_:
.asciiz " terms of Fibonacci series are : "
.align 2
label_2_:
.asciiz " "
.align 2

.data
# Allocated labels for virtual registers
label_13_v3:
.space 4
label_15_v5:
.space 4
label_23_v8:
.space 4
label_19_v7:
.space 4
label_22_v9:
.space 4
label_12_v4:
.space 4
label_5_v0:
.space 4
label_8_v2:
.space 4
label_9_v1:
.space 4
label_17_v6:
.space 4

.data
# Allocated labels for virtual registers

.text
.globl main
main:
# Original instruction: addiu $sp,$sp,-4
# Original instruction: addiu $sp,$sp,-4
addiu $sp,$sp,-4
# Original instruction: sw $fp,0($sp)
# Original instruction: sw $fp,0($sp)
sw $fp,0($sp)
# Original instruction: addi $fp,$sp,0
# Original instruction: addi $fp,$sp,0
addi $fp,$sp,0
# Original instruction: addiu $sp,$sp,-4
# Original instruction: addiu $sp,$sp,-4
addiu $sp,$sp,-4
# Original instruction: sw $ra,0($sp)
# Original instruction: sw $ra,0($sp)
sw $ra,0($sp)
# Original instruction: pushRegisters
# Original instruction: la $t0,label_13_v3
la $t0,label_13_v3
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# Original instruction: la $t0,label_15_v5
la $t0,label_15_v5
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# Original instruction: la $t0,label_23_v8
la $t0,label_23_v8
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# Original instruction: la $t0,label_19_v7
la $t0,label_19_v7
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# Original instruction: la $t0,label_22_v9
la $t0,label_22_v9
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# Original instruction: la $t0,label_12_v4
la $t0,label_12_v4
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# Original instruction: la $t0,label_5_v0
la $t0,label_5_v0
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# Original instruction: la $t0,label_8_v2
la $t0,label_8_v2
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# Original instruction: la $t0,label_9_v1
la $t0,label_9_v1
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# Original instruction: la $t0,label_17_v6
la $t0,label_17_v6
# Original instruction: lw $t0,0($t0)
lw $t0,0($t0)
# Original instruction: addi $sp,$sp,-4
addi $sp,$sp,-4
# Original instruction: sw $t0,0($sp)
sw $t0,0($sp)
# ------Var decl for n
# Original instruction: addiu $sp,$sp,4
# Original instruction: addiu $sp,$sp,4
addiu $sp,$sp,4
# ------Var decl for first
# Original instruction: addiu $sp,$sp,4
# Original instruction: addiu $sp,$sp,4
addiu $sp,$sp,4
# ------Var decl for second
# Original instruction: addiu $sp,$sp,4
# Original instruction: addiu $sp,$sp,4
addiu $sp,$sp,4
# ------Var decl for next
# Original instruction: addiu $sp,$sp,4
# Original instruction: addiu $sp,$sp,4
addiu $sp,$sp,4
# ------Var decl for c
# Original instruction: addiu $sp,$sp,4
# Original instruction: addiu $sp,$sp,4
addiu $sp,$sp,4
# ------Var decl for t
# Original instruction: addiu $sp,$sp,4
# Original instruction: addiu $sp,$sp,4
addiu $sp,$sp,4
# ----Start of Block----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of VarExpr----
# Original instruction: addi v0,$fp,-8
# Original instruction: addi $t5,$fp,-8
addi $t5,$fp,-8
# Original instruction: la $t0,label_5_v0
la $t0,label_5_v0
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of of VarExpr----
# ----Start of FunCallExpr----
# Original instruction: li $v0,5
# Original instruction: li $v0,5
li $v0,5
# Original instruction: syscall
# Original instruction: syscall
syscall
# ----End of FunCallExpr----
# Original instruction: sw $v0,0(v0)
# Original instruction: la $t5,label_5_v0
la $t5,label_5_v0
# Original instruction: lw $t5,0($t5)
lw $t5,0($t5)
# Original instruction: sw $v0,0($t5)
sw $v0,0($t5)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of VarExpr----
# Original instruction: addi v1,$fp,-12
# Original instruction: addi $t5,$fp,-12
addi $t5,$fp,-12
# Original instruction: la $t0,label_9_v1
la $t0,label_9_v1
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of of VarExpr----
# ----Start of IntLiteral----
# Original instruction: li v2,0
# Original instruction: li $t5,0
li $t5,0
# Original instruction: la $t0,label_8_v2
la $t0,label_8_v2
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of IntLiteral----
# Original instruction: sw v2,0(v1)
# Original instruction: la $t5,label_8_v2
la $t5,label_8_v2
# Original instruction: lw $t5,0($t5)
lw $t5,0($t5)
# Original instruction: la $t4,label_9_v1
la $t4,label_9_v1
# Original instruction: lw $t4,0($t4)
lw $t4,0($t4)
# Original instruction: sw $t5,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of VarExpr----
# Original instruction: addi v3,$fp,-16
# Original instruction: addi $t5,$fp,-16
addi $t5,$fp,-16
# Original instruction: la $t0,label_13_v3
la $t0,label_13_v3
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of of VarExpr----
# ----Start of IntLiteral----
# Original instruction: li v4,1
# Original instruction: li $t5,1
li $t5,1
# Original instruction: la $t0,label_12_v4
la $t0,label_12_v4
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of IntLiteral----
# Original instruction: sw v4,0(v3)
# Original instruction: la $t5,label_12_v4
la $t5,label_12_v4
# Original instruction: lw $t5,0($t5)
lw $t5,0($t5)
# Original instruction: la $t4,label_13_v3
la $t4,label_13_v3
# Original instruction: lw $t4,0($t4)
lw $t4,0($t4)
# Original instruction: sw $t5,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of TypecastExpr----
# ----Start of StrLiteral----
# Original instruction: la v5,label_0_
# Original instruction: la $t5,label_0_
la $t5,label_0_
# Original instruction: la $t0,label_15_v5
la $t0,label_15_v5
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of StrLiteral----
# ----End of TypecastExpr----
# Original instruction: addi $a0,v5,0
# Original instruction: la $t5,label_15_v5
la $t5,label_15_v5
# Original instruction: lw $t5,0($t5)
lw $t5,0($t5)
# Original instruction: addi $a0,$t5,0
addi $a0,$t5,0
# Original instruction: li $v0,4
# Original instruction: li $v0,4
li $v0,4
# Original instruction: syscall
# Original instruction: syscall
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of VarExpr----
# Original instruction: lw v6,-8($fp)
# Original instruction: lw $t5,-8($fp)
lw $t5,-8($fp)
# Original instruction: la $t0,label_17_v6
la $t0,label_17_v6
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of VarExpr----
# Original instruction: addi $a0,v6,0
# Original instruction: la $t5,label_17_v6
la $t5,label_17_v6
# Original instruction: lw $t5,0($t5)
lw $t5,0($t5)
# Original instruction: addi $a0,$t5,0
addi $a0,$t5,0
# Original instruction: li $v0,1
# Original instruction: li $v0,1
li $v0,1
# Original instruction: syscall
# Original instruction: syscall
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of TypecastExpr----
# ----Start of StrLiteral----
# Original instruction: la v7,label_1_
# Original instruction: la $t5,label_1_
la $t5,label_1_
# Original instruction: la $t0,label_19_v7
la $t0,label_19_v7
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of StrLiteral----
# ----End of TypecastExpr----
# Original instruction: addi $a0,v7,0
# Original instruction: la $t5,label_19_v7
la $t5,label_19_v7
# Original instruction: lw $t5,0($t5)
lw $t5,0($t5)
# Original instruction: addi $a0,$t5,0
addi $a0,$t5,0
# Original instruction: li $v0,4
# Original instruction: li $v0,4
li $v0,4
# Original instruction: syscall
# Original instruction: syscall
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of VarExpr----
# Original instruction: addi v8,$fp,-24
# Original instruction: addi $t5,$fp,-24
addi $t5,$fp,-24
# Original instruction: la $t0,label_23_v8
la $t0,label_23_v8
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of of VarExpr----
# ----Start of IntLiteral----
# Original instruction: li v9,0
# Original instruction: li $t5,0
li $t5,0
# Original instruction: la $t0,label_22_v9
la $t0,label_22_v9
# Original instruction: sw $t5,0($t0)
sw $t5,0($t0)
# ----End of IntLiteral----
# Original instruction: sw v9,0(v8)
# Original instruction: la $t5,label_22_v9
la $t5,label_22_v9
# Original instruction: lw $t5,0($t5)
lw $t5,0($t5)
# Original instruction: la $t4,label_23_v8
la $t4,label_23_v8
# Original instruction: lw $t4,0($t4)
lw $t4,0($t4)
# Original instruction: sw $t5,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of While----
# ----End of While----
# ----End of Block----
# Original instruction: li $v0,10
# Original instruction: li $v0,10
li $v0,10
# Original instruction: syscall
# Original instruction: syscall
syscall

