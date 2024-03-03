.data

.data
# Allocated labels for virtual registers
label_40_v12:
.space 4
label_76_v24:
.space 4
label_11_v3:
.space 4
label_24_v6:
.space 4
label_5_v1:
.space 4
label_64_v20:
.space 4
label_18_v4:
.space 4
label_74_v23:
.space 4
label_30_v8:
.space 4
label_46_v14:
.space 4
label_54_v17:
.space 4
label_42_v13:
.space 4
label_48_v15:
.space 4
label_62_v19:
.space 4
label_23_v7:
.space 4
label_58_v18:
.space 4
label_70_v22:
.space 4
label_35_v11:
.space 4
label_12_v2:
.space 4
label_17_v5:
.space 4
label_52_v16:
.space 4
label_6_v0:
.space 4
label_29_v9:
.space 4
label_36_v10:
.space 4
label_68_v21:
.space 4

.data
# Allocated labels for virtual registers

.text
.globl main
main:
addi $sp,$sp,-4
sw $fp,0($sp)
addi $fp,$sp,0
addi $sp,$sp,-4
sw $ra,0($sp)
# Original instruction: pushRegisters
la $t0,label_40_v12
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_76_v24
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_11_v3
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_24_v6
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_5_v1
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_64_v20
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_18_v4
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_74_v23
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_30_v8
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_46_v14
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_54_v17
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_42_v13
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_48_v15
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_62_v19
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_23_v7
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_58_v18
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_70_v22
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_35_v11
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_12_v2
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_17_v5
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_52_v16
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_6_v0
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_29_v9
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_36_v10
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
la $t0,label_68_v21
lw $t0,0($t0)
addi $sp,$sp,-4
sw $t0,0($sp)
addi $sp,$sp,28
# ----Start of Assign----
addi $t5,$fp,-32
la $t0,label_6_v0
sw $t5,0($t0)
la $t4,label_6_v0
lw $t4,0($t4)
addi $t4,$t4,-8
la $t0,label_6_v0
sw $t4,0($t0)
# ----Start of IntLiteral----
li $t5,1
la $t0,label_5_v1
sw $t5,0($t0)
# ----End of IntLiteral----
la $t5,label_5_v1
lw $t5,0($t5)
la $t4,label_6_v0
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----Start of Assign----
addi $t5,$fp,-32
la $t0,label_12_v2
sw $t5,0($t0)
la $t4,label_12_v2
lw $t4,0($t4)
addi $t4,$t4,-12
la $t0,label_12_v2
sw $t4,0($t0)
# ----Start of IntLiteral----
li $t5,2
la $t0,label_11_v3
sw $t5,0($t0)
# ----End of IntLiteral----
la $t5,label_11_v3
lw $t5,0($t5)
la $t4,label_12_v2
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----Start of Assign----
addi $t5,$fp,-32
la $t0,label_18_v4
sw $t5,0($t0)
la $t4,label_18_v4
lw $t4,0($t4)
addi $t4,$t4,-16
la $t0,label_18_v4
sw $t4,0($t0)
# ----Start of ChrLiteral----
li $t5,51
la $t0,label_17_v5
sw $t5,0($t0)
# ----End of ChrLiteral----
la $t5,label_17_v5
lw $t5,0($t5)
la $t4,label_18_v4
lw $t4,0($t4)
sb $t5,0($t4)
# ----End of Assign----
# ----Start of Assign----
addi $t5,$fp,-32
la $t0,label_24_v6
sw $t5,0($t0)
la $t4,label_24_v6
lw $t4,0($t4)
addi $t4,$t4,-20
la $t0,label_24_v6
sw $t4,0($t0)
# ----Start of AddressOfExpr----
addi $t5,$fp,-32
la $t0,label_23_v7
sw $t5,0($t0)
# ----End of AddressOfExpr----
la $t5,label_23_v7
lw $t5,0($t5)
la $t4,label_24_v6
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----Start of Assign----
addi $t5,$fp,-32
la $t0,label_30_v8
sw $t5,0($t0)
la $t4,label_30_v8
lw $t4,0($t4)
addi $t4,$t4,-24
la $t0,label_30_v8
sw $t4,0($t0)
# ----Start of IntLiteral----
li $t5,4
la $t0,label_29_v9
sw $t5,0($t0)
# ----End of IntLiteral----
la $t5,label_29_v9
lw $t5,0($t5)
la $t4,label_30_v8
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----Start of Assign----
addi $t5,$fp,-32
la $t0,label_36_v10
sw $t5,0($t0)
la $t4,label_36_v10
lw $t4,0($t4)
addi $t4,$t4,-28
la $t0,label_36_v10
sw $t4,0($t0)
# ----Start of IntLiteral----
li $t5,5
la $t0,label_35_v11
sw $t5,0($t0)
# ----End of IntLiteral----
la $t5,label_35_v11
lw $t5,0($t5)
la $t4,label_36_v10
lw $t4,0($t4)
sw $t5,0($t4)
# ----End of Assign----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
addi $t5,$fp,-32
la $t0,label_40_v12
sw $t5,0($t0)
la $t4,label_40_v12
lw $t4,0($t4)
addi $t4,$t4,-8
la $t0,label_40_v12
sw $t4,0($t0)
la $t5,label_40_v12
lw $t5,0($t5)
lw $t4,0($t5)
la $t0,label_42_v13
sw $t4,0($t0)
# ----End of FieldAccessExpr----
la $t5,label_42_v13
lw $t5,0($t5)
addi $a0,$t5,0
li $v0,1
syscall
# ----End of FunCallExpr----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
addi $t5,$fp,-32
la $t0,label_46_v14
sw $t5,0($t0)
la $t4,label_46_v14
lw $t4,0($t4)
addi $t4,$t4,-12
la $t0,label_46_v14
sw $t4,0($t0)
la $t5,label_46_v14
lw $t5,0($t5)
lw $t4,0($t5)
la $t0,label_48_v15
sw $t4,0($t0)
# ----End of FieldAccessExpr----
la $t5,label_48_v15
lw $t5,0($t5)
addi $a0,$t5,0
li $v0,1
syscall
# ----End of FunCallExpr----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
addi $t5,$fp,-32
la $t0,label_52_v16
sw $t5,0($t0)
la $t4,label_52_v16
lw $t4,0($t4)
addi $t4,$t4,-16
la $t0,label_52_v16
sw $t4,0($t0)
la $t5,label_52_v16
lw $t5,0($t5)
lb $t4,0($t5)
la $t0,label_54_v17
sw $t4,0($t0)
# ----End of FieldAccessExpr----
la $t5,label_54_v17
lw $t5,0($t5)
addi $a0,$t5,0
li $v0,11
syscall
# ----End of FunCallExpr----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
# ----Start of FieldAccessExpr----
addi $t5,$fp,-32
la $t0,label_58_v18
sw $t5,0($t0)
la $t4,label_58_v18
lw $t4,0($t4)
addi $t4,$t4,-20
la $t0,label_58_v18
sw $t4,0($t0)
la $t5,label_58_v18
lw $t5,0($t5)
lw $t4,0($t5)
la $t0,label_62_v19
sw $t4,0($t0)
# ----End of FieldAccessExpr----
la $t4,label_62_v19
lw $t4,0($t4)
addi $t4,$t4,-24
la $t0,label_62_v19
sw $t4,0($t0)
la $t5,label_62_v19
lw $t5,0($t5)
lw $t4,0($t5)
la $t0,label_64_v20
sw $t4,0($t0)
# ----End of FieldAccessExpr----
la $t5,label_64_v20
lw $t5,0($t5)
addi $a0,$t5,0
li $v0,1
syscall
# ----End of FunCallExpr----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
addi $t5,$fp,-32
la $t0,label_68_v21
sw $t5,0($t0)
la $t4,label_68_v21
lw $t4,0($t4)
addi $t4,$t4,-24
la $t0,label_68_v21
sw $t4,0($t0)
la $t5,label_68_v21
lw $t5,0($t5)
lw $t4,0($t5)
la $t0,label_70_v22
sw $t4,0($t0)
# ----End of FieldAccessExpr----
la $t5,label_70_v22
lw $t5,0($t5)
addi $a0,$t5,0
li $v0,1
syscall
# ----End of FunCallExpr----
# ----Start of FunCallExpr----
# ----Start of FieldAccessExpr----
addi $t5,$fp,-32
la $t0,label_74_v23
sw $t5,0($t0)
la $t4,label_74_v23
lw $t4,0($t4)
addi $t4,$t4,-28
la $t0,label_74_v23
sw $t4,0($t0)
la $t5,label_74_v23
lw $t5,0($t5)
lw $t4,0($t5)
la $t0,label_76_v24
sw $t4,0($t0)
# ----End of FieldAccessExpr----
la $t5,label_76_v24
lw $t5,0($t5)
addi $a0,$t5,0
li $v0,1
syscall
# ----End of FunCallExpr----
li $v0,10
syscall

