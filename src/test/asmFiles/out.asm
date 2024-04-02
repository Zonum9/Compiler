.data
.globl main

.text
fun:
addiu $sp,$sp,-4
sw $fp,0($sp)
addi $fp,$sp,0
addiu $sp,$sp,-4
sw $ra,0($sp)
# ---PUSH REGISTERS START---
addiu $sp,$sp,-4
sw $t4,0($sp)
addiu $sp,$sp,-4
sw $t5,0($sp)
addiu $sp,$sp,-4
sw $t3,0($sp)
# ---PUSH REGISTERS END---
# ----Start of Block----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
# ----Start of FieldAccessExpr----
# ----Start of VarExpr----
addiu $t3,$fp,16
# ----End of of VarExpr----
addiu $t3,$t3,0
# ----End of of FieldAccessExpr----
lw $t3,0($t3)
# ----End of FieldAccessExpr----
addi $a0,$t3,0
li $v0,1
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
# ----Start of FieldAccessExpr----
# ----Start of VarExpr----
addiu $t3,$fp,16
# ----End of of VarExpr----
addiu $t3,$t3,-4
# ----End of of FieldAccessExpr----
lbu $t3,0($t3)
# ----End of FieldAccessExpr----
addi $a0,$t3,0
li $v0,11
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of FieldAccessExpr----
# ----Start of VarExpr----
addiu $t3,$fp,16
# ----End of of VarExpr----
addiu $t3,$t3,0
# ----End of of FieldAccessExpr----
# ----Start of IntLiteral----
li $t4,11
# ----End of IntLiteral----
sw $t4,0($t3)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of FieldAccessExpr----
# ----Start of VarExpr----
addiu $t4,$fp,16
# ----End of of VarExpr----
addiu $t4,$t4,-4
# ----End of of FieldAccessExpr----
# ----Start of ChrLiteral----
li $t3,101
# ----End of ChrLiteral----
sb $t3,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of Return----
# -------------------START of the return value
addiu $t5,$fp,8
# ----Start of VarExpr----
addiu $t3,$fp,16
# ----End of of VarExpr----
# -----------COPY STRUCT-------------
lw $t4,0($t3)
sw $t4,0($t5)
lw $t4,-4($t3)
sw $t4,-4($t5)
# -------------------END of the return value
# ---POP REGISTERS START---
lw $t3,0($sp)
addiu $sp,$sp,4
lw $t5,0($sp)
addiu $sp,$sp,4
lw $t4,0($sp)
addiu $sp,$sp,4
# ---POP REGISTERS END---
lw $ra,-4($fp)
addiu $sp,$fp,4
lw $fp,0($fp)
jr $ra
# ----End of Return----
# ----End of Block----
# ---POP REGISTERS START---
lw $t3,0($sp)
addiu $sp,$sp,4
lw $t5,0($sp)
addiu $sp,$sp,4
lw $t4,0($sp)
addiu $sp,$sp,4
# ---POP REGISTERS END---
lw $ra,-4($fp)
addiu $sp,$fp,4
lw $fp,0($fp)
jr $ra

.data
# Allocated labels for used registers
.align 2
label_13400_$t4:
.space 4
.align 2
label_13409_$t5:
.space 4
.align 2
label_13385_$t3:
.space 4

.text
fun2:
addiu $sp,$sp,-4
sw $fp,0($sp)
addi $fp,$sp,0
addiu $sp,$sp,-4
sw $ra,0($sp)
# ---PUSH REGISTERS START---
addiu $sp,$sp,-4
sw $t4,0($sp)
addiu $sp,$sp,-4
sw $t5,0($sp)
addiu $sp,$sp,-4
sw $t3,0($sp)
# ---PUSH REGISTERS END---
# ----Start of Block----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
# ----Start of FieldAccessExpr----
# ----Start of VarExpr----
addiu $t3,$fp,20
# ----End of of VarExpr----
addiu $t3,$t3,0
# ----End of of FieldAccessExpr----
lw $t3,0($t3)
# ----End of FieldAccessExpr----
addi $a0,$t3,0
li $v0,1
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of VarExpr----
lw $t3,12($fp)
# ----End of VarExpr----
addi $a0,$t3,0
li $v0,1
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of FieldAccessExpr----
# ----Start of VarExpr----
addiu $t3,$fp,20
# ----End of of VarExpr----
addiu $t3,$t3,0
# ----End of of FieldAccessExpr----
# ----Start of IntLiteral----
li $t4,11
# ----End of IntLiteral----
sw $t4,0($t3)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of Return----
# -------------------START of the return value
addiu $t4,$fp,8
# ----Start of VarExpr----
addiu $t5,$fp,20
# ----End of of VarExpr----
# -----------COPY STRUCT-------------
lw $t3,0($t5)
sw $t3,0($t4)
lw $t3,-4($t5)
sw $t3,-4($t4)
# -------------------END of the return value
# ---POP REGISTERS START---
lw $t3,0($sp)
addiu $sp,$sp,4
lw $t5,0($sp)
addiu $sp,$sp,4
lw $t4,0($sp)
addiu $sp,$sp,4
# ---POP REGISTERS END---
lw $ra,-4($fp)
addiu $sp,$fp,4
lw $fp,0($fp)
jr $ra
# ----End of Return----
# ----End of Block----
# ---POP REGISTERS START---
lw $t3,0($sp)
addiu $sp,$sp,4
lw $t5,0($sp)
addiu $sp,$sp,4
lw $t4,0($sp)
addiu $sp,$sp,4
# ---POP REGISTERS END---
lw $ra,-4($fp)
addiu $sp,$fp,4
lw $fp,0($fp)
jr $ra

.data
# Allocated labels for used registers
.align 2
label_13430_$t4:
.space 4
.align 2
label_13434_$t5:
.space 4
.align 2
label_13419_$t3:
.space 4

.text
main:
addiu $sp,$sp,-4
sw $fp,0($sp)
addi $fp,$sp,0
addiu $sp,$sp,-4
sw $ra,0($sp)
# ------Var decl for x
addiu $sp,$sp,-8
# ----Start of Block----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of FieldAccessExpr----
# ----Start of VarExpr----
addiu $t4,$fp,-8
# ----End of of VarExpr----
addiu $t4,$t4,0
# ----End of of FieldAccessExpr----
# ----Start of IntLiteral----
li $t3,99
# ----End of IntLiteral----
sw $t3,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of FieldAccessExpr----
# ----Start of VarExpr----
addiu $t4,$fp,-8
# ----End of of VarExpr----
addiu $t4,$t4,-4
# ----End of of FieldAccessExpr----
# ----Start of ChrLiteral----
li $t3,111
# ----End of ChrLiteral----
sb $t3,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
# ----Start of FieldAccessExpr----
# ----Start of FunCallExpr----
# ----Start of FunCallExpr----
# ------------ PARAM 0
# ----Start of VarExpr----
addiu $t5,$fp,-8
# ----End of of VarExpr----
# ------------ SPACE FOR PARAM 0
addiu $sp,$sp,-8
addiu $t3,$sp,4
# -----------COPY STRUCT-------------
lw $t4,0($t5)
sw $t4,0($t3)
lw $t4,-4($t5)
sw $t4,-4($t3)
addiu $sp,$sp,-8
jal fun
addiu $t3,$sp,4
addiu $sp,$sp,8
addiu $sp,$sp,8
# ----End of FunCallExpr----
# ----End of of FunCallExpr----
addiu $t3,$t3,0
# ----End of of FieldAccessExpr----
lw $t3,0($t3)
# ----End of FieldAccessExpr----
addi $a0,$t3,0
li $v0,1
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
# ----Start of FieldAccessExpr----
# ----Start of FunCallExpr----
# ----Start of FunCallExpr----
# ------------ PARAM 0
# ----Start of VarExpr----
addiu $t5,$fp,-8
# ----End of of VarExpr----
# ------------ SPACE FOR PARAM 0
addiu $sp,$sp,-8
addiu $t4,$sp,4
# -----------COPY STRUCT-------------
lw $t3,0($t5)
sw $t3,0($t4)
lw $t3,-4($t5)
sw $t3,-4($t4)
addiu $sp,$sp,-8
jal fun
addiu $t3,$sp,4
addiu $sp,$sp,8
addiu $sp,$sp,8
# ----End of FunCallExpr----
# ----End of of FunCallExpr----
addiu $t3,$t3,-4
# ----End of of FieldAccessExpr----
lbu $t3,0($t3)
# ----End of FieldAccessExpr----
addi $a0,$t3,0
li $v0,11
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
# ----Start of FieldAccessExpr----
# ----Start of FunCallExpr----
# ----Start of FunCallExpr----
# ------------ PARAM 0
# ----Start of VarExpr----
addiu $t5,$fp,-8
# ----End of of VarExpr----
# ------------ SPACE FOR PARAM 0
addiu $sp,$sp,-8
addiu $t3,$sp,4
# -----------COPY STRUCT-------------
lw $t4,0($t5)
sw $t4,0($t3)
lw $t4,-4($t5)
sw $t4,-4($t3)
# ------------ PARAM 1
# ----Start of IntLiteral----
li $t3,66
# ----End of IntLiteral----
# ------------ SPACE FOR PARAM 1
addiu $sp,$sp,-4
sw $t3,0($sp)
addiu $sp,$sp,-8
jal fun2
addiu $t3,$sp,4
addiu $sp,$sp,8
addiu $sp,$sp,4
addiu $sp,$sp,8
# ----End of FunCallExpr----
# ----End of of FunCallExpr----
addiu $t3,$t3,0
# ----End of of FieldAccessExpr----
lw $t3,0($t3)
# ----End of FieldAccessExpr----
addi $a0,$t3,0
li $v0,1
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----End of Block----
li $v0,10
syscall

.data
# Allocated labels for used registers
.align 2
label_13443_$t4:
.space 4
.align 2
label_13455_$t5:
.space 4
.align 2
label_13446_$t3:
.space 4

