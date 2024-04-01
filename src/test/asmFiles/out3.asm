.data
result:
.space 20
newline:
.asciiz "\n"

.text
.globl main

main:
    li v0, 10

    
    li v1, 0                
    li v2, 1                

    
    li v3, 2                
    la v4, result       

fib_loop:
    beq v3, v0, fib_done

    
    sw v1, 0(v4)           
    addiu v4, v4, 4         

    
    addu v1, v1, v2        
    addiu v2, v1,0
    addiu v3, v3, -1
    j fib_loop               

fib_done:
    
    la v4, result       
    li v3, 0                
    li $v0, 4                

print_loop:
    beq v3, v0, print_done

    lw $a0, 0(v4)           
    li $v0, 1                
    syscall

    
    li $v0, 4                
    la $a0, newline          
    syscall

    addiu v4, v4, 4         
    addiu v3, v3, 1         
    j print_loop             

print_done:
    
    li $v0, 10               
    syscall

