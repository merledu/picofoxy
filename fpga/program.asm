li x15, 0x40001000
li x14, 200000

mainLoop:
li x3, 0
li x16, 0
li x17, 0
li x18, 0
li x19, 0
li x13, 0
    loop0:
        addi x16, x16, 1
        bne x16, x14, loop0
    addi x3,x3,1
	sw x3, 0x1c(x15)
	sw x3, 0x10(x15)

    loop1:
        addi x17, x17, 1
        bne x17, x14, loop1	
    addi x3,x3,2
    sw x3, 0x1c(x15)
    sw x3, 0x10(x15)
    
    loop2:
        addi x18, x18, 1
        bne x18, x14, loop2
	addi x3,x3,4
    sw x3, 0x1c(x15)
    sw x3, 0x10(x15)
    
    loop3:
    	addi x19, x19, 1
        bne x19, x14, loop3
    addi x3,x3,8
    sw x3, 0x1c(x15)
    sw x3, 0x10(x15)
    
    reset:
    	addi x13, x13, 1
        bne x13, x14, reset

	sw x0, 0x1c(x15)
    sw x0, 0x10(x15)
    
	jal mainLoop
