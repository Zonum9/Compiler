# A simple hello world program
.data
hellostr: .asciiz "Hello, world\n"
.text
    li $v0, 4 # setup print syscall
    la $a0, hellostr # argument to print string
    syscall # tell the OS to do the system call
    li $v0, 10 # setup exit syscall
    syscall # tell the OS to perform the syscal
