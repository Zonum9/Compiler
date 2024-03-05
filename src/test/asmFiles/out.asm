.data

.data
# Allocated labels for virtual registers
label_32_v13:
.space 4
label_17_v5:
.space 4
label_31_v10:
.space 4
label_14_v2:
.space 4
label_56_v20:
.space 4
label_21_v6:
.space 4
label_13_v3:
.space 4
label_45_v17:
.space 4
label_55_v22:
.space 4
label_52_v21:
.space 4
label_53_v23:
.space 4
label_59_v24:
.space 4
label_42_v16:
.space 4
label_18_v4:
.space 4
label_28_v11:
.space 4
label_27_v9:
.space 4
label_38_v12:
.space 4
label_60_v26:
.space 4
label_46_v15:
.space 4
label_43_v18:
.space 4
label_48_v19:
.space 4
label_22_v8:
.space 4
label_62_v25:
.space 4
label_36_v14:
.space 4
label_24_v7:
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
la $t0,label_32_v13
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_17_v5
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_31_v10
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_14_v2
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_56_v20
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_21_v6
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_13_v3
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_45_v17
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_55_v22
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_52_v21
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_53_v23
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_59_v24
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_42_v16
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_18_v4
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_28_v11
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_27_v9
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_38_v12
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_60_v26
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_46_v15
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_43_v18
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_48_v19
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_22_v8
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_62_v25
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_36_v14
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_24_v7
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
# ------Var decl for x
# Original instruction: addiu $sp,$sp,-4
addiu $sp,$sp,-4
# ------Var decl for range
# Original instruction: addiu $sp,$sp,-4
addiu $sp,$sp,-4
# ----Start of Block----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of VarExpr----
# Original instruction: addi v2,$fp,-8
addi $t5,$fp,-8
la $t0,label_14_v2
sw $t5,0($t0)
# ----End of of VarExpr----
# ----Start of IntLiteral----
# Original instruction: li v3,1
li $t5,1
la $t0,label_13_v3
sw $t5,0($t0)
# ----End of IntLiteral----
# Original instruction: sw v3,0(v2)
la $t5,label_13_v3
lw $t5,0($t5)
la $t4,label_14_v2
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of VarExpr----
# Original instruction: addi v4,$fp,-12
addi $t5,$fp,-12
la $t0,label_18_v4
sw $t5,0($t0)
# ----End of of VarExpr----
# ----Start of IntLiteral----
# Original instruction: li v5,100
li $t5,100
la $t0,label_17_v5
sw $t5,0($t0)
# ----End of IntLiteral----
# Original instruction: sw v5,0(v4)
la $t5,label_17_v5
lw $t5,0($t5)
la $t4,label_18_v4
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of While----
# ----Start of BinOp----
# ----Start of VarExpr----
# Original instruction: lw v6,-8($fp)
lw $t5,-8($fp)
la $t0,label_21_v6
sw $t5,0($t0)
# ----End of VarExpr----
# ----Start of VarExpr----
# Original instruction: lw v8,-12($fp)
lw $t5,-12($fp)
la $t0,label_22_v8
sw $t5,0($t0)
# ----End of VarExpr----
# Original instruction: slt v7,v6,v8
la $t5,label_21_v6
lw $t5,0($t5)
la $t4,label_22_v8
lw $t4,0($t4)
slt $t3,$t5,$t4
la $t0,label_24_v7
sw $t3,0($t0)
# ----End of BinOp----
# Original instruction: beq v7,$zero,label_8_LoopEnd
la $t5,label_24_v7
lw $t5,0($t5)
beq $t5,$zero,label_8_LoopEnd
label_7_LoopStart:
# ----Start of Block----
# ----Start of If----
# ----Start of BinOp----
# ----Start of BinOp----
# ----Start of VarExpr----
# Original instruction: lw v9,-12($fp)
lw $t5,-12($fp)
la $t0,label_27_v9
sw $t5,0($t0)
# ----End of VarExpr----
# ----Start of VarExpr----
# Original instruction: lw v11,-8($fp)
lw $t5,-8($fp)
la $t0,label_28_v11
sw $t5,0($t0)
# ----End of VarExpr----
# Original instruction: div v9,v11
la $t5,label_27_v9
lw $t5,0($t5)
la $t4,label_28_v11
lw $t4,0($t4)
div $t5,$t4
# Original instruction: mfhi v10
mfhi $t5
la $t0,label_31_v10
sw $t5,0($t0)
# ----End of BinOp----
# ----Start of IntLiteral----
# Original instruction: li v13,0
li $t5,0
la $t0,label_32_v13
sw $t5,0($t0)
# ----End of IntLiteral----
# Original instruction: xor v12,v10,v13
la $t5,label_31_v10
lw $t5,0($t5)
la $t4,label_32_v13
lw $t4,0($t4)
xor $t3,$t5,$t4
la $t0,label_38_v12
sw $t3,0($t0)
# Original instruction: li v14,1
li $t5,1
la $t0,label_36_v14
sw $t5,0($t0)
# Original instruction: sltu v12,v12,v14
la $t3,label_38_v12
lw $t3,0($t3)
la $t4,label_36_v14
lw $t4,0($t4)
sltu $t3,$t3,$t4
la $t0,label_38_v12
sw $t3,0($t0)
# ----End of BinOp----
# Original instruction: beq v12,$zero,label_9_ifFailed
la $t5,label_38_v12
lw $t5,0($t5)
beq $t5,$zero,label_9_ifFailed
# ----Start of Block----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of VarExpr----
# Original instruction: addi v15,$fp,-8
addi $t5,$fp,-8
la $t0,label_46_v15
sw $t5,0($t0)
# ----End of of VarExpr----
# ----Start of BinOp----
# ----Start of VarExpr----
# Original instruction: lw v16,-8($fp)
lw $t5,-8($fp)
la $t0,label_42_v16
sw $t5,0($t0)
# ----End of VarExpr----
# ----Start of IntLiteral----
# Original instruction: li v18,1
li $t5,1
la $t0,label_43_v18
sw $t5,0($t0)
# ----End of IntLiteral----
# Original instruction: add v17,v16,v18
la $t5,label_42_v16
lw $t5,0($t5)
la $t4,label_43_v18
lw $t4,0($t4)
add $t3,$t5,$t4
la $t0,label_45_v17
sw $t3,0($t0)
# ----End of BinOp----
# Original instruction: sw v17,0(v15)
la $t5,label_45_v17
lw $t5,0($t5)
la $t4,label_46_v15
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----Start of Continue----
# ----End of Continue----
# ----End of Block----
# Original instruction: j label_10_endOfIfBlock
j label_10_endOfIfBlock
label_9_ifFailed:
label_10_endOfIfBlock:
# ----End of If----
# ----Start of ExprStmt----
# ----Start of FunCallExpr----
# ----Start of VarExpr----
# Original instruction: lw v19,-8($fp)
lw $t5,-8($fp)
la $t0,label_48_v19
sw $t5,0($t0)
# ----End of VarExpr----
# Original instruction: addi $a0,v19,0
la $t5,label_48_v19
lw $t5,0($t5)
addi $a0,$t5,0
# Original instruction: li $v0,1
li $v0,1
# Original instruction: syscall
syscall
# ----End of FunCallExpr----
# ----End of ExprStmt----
# ----Start of ExprStmt----
# ----Start of Assign----
# ----Start of VarExpr----
# Original instruction: addi v20,$fp,-8
addi $t5,$fp,-8
la $t0,label_56_v20
sw $t5,0($t0)
# ----End of of VarExpr----
# ----Start of BinOp----
# ----Start of VarExpr----
# Original instruction: lw v21,-8($fp)
lw $t5,-8($fp)
la $t0,label_52_v21
sw $t5,0($t0)
# ----End of VarExpr----
# ----Start of IntLiteral----
# Original instruction: li v23,1
li $t5,1
la $t0,label_53_v23
sw $t5,0($t0)
# ----End of IntLiteral----
# Original instruction: add v22,v21,v23
la $t5,label_52_v21
lw $t5,0($t5)
la $t4,label_53_v23
lw $t4,0($t4)
add $t3,$t5,$t4
la $t0,label_55_v22
sw $t3,0($t0)
# ----End of BinOp----
# Original instruction: sw v22,0(v20)
la $t5,label_55_v22
lw $t5,0($t5)
la $t4,label_56_v20
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----End of ExprStmt----
# ----End of Block----
# ----Start of BinOp----
# ----Start of VarExpr----
# Original instruction: lw v24,-8($fp)
lw $t5,-8($fp)
la $t0,label_59_v24
sw $t5,0($t0)
# ----End of VarExpr----
# ----Start of VarExpr----
# Original instruction: lw v26,-12($fp)
lw $t5,-12($fp)
la $t0,label_60_v26
sw $t5,0($t0)
# ----End of VarExpr----
# Original instruction: slt v25,v24,v26
la $t5,label_59_v24
lw $t5,0($t5)
la $t4,label_60_v26
lw $t4,0($t4)
slt $t3,$t5,$t4
la $t0,label_62_v25
sw $t3,0($t0)
# ----End of BinOp----
# Original instruction: bne v25,$zero,label_7_LoopStart
la $t5,label_62_v25
lw $t5,0($t5)
bne $t5,$zero,label_7_LoopStart
label_8_LoopEnd:
# ----End of While----
# ----End of Block----
# Original instruction: li $v0,10
li $v0,10
# Original instruction: syscall
syscall

