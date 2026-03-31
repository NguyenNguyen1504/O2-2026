#
# 
# This file is part of the CS-A1120 Programming 2 course materials at
# Aalto University in Spring 2026, and is for your personal use on that
# course only.
# Distribution of any parts of this file in any form, including posting or
# sharing on public or shared forums or repositories is *prohibited* and
# constitutes a violation of the code of conduct of the course.
# The programming exercises of CS-A1120 are individual and confidential
# assignments---this means that as a student taking the course you are
# allowed to individually and confidentially work with the material,
# to discuss and review the material with course staff, and submit the
# material for grading on course infrastructure.
# All other use - including, having other persons or programs
# (e.g. AI/LLM tools) working on or solving the exercises for you is
# forbidden, and constitutes a violation of the code of conduct of this
# course.
# 
#


# This assignment asks you to write an assembly-language program that
# computes the remainder when dividing one positive integer with another.
        
# Your task is to write an assembly-language program that computes the 
# remainder when dividing $0 by $1. The result must be stored into $2. Both
# $0 and $1 are guaranteed to be nonzero positive integers. Note that there
# is a limit to the number of ticks your program may run. Even if your program
# produces correct output you may need to optimize it if it's not fast enough.
#
# What makes this task challenging is that our "armlet" architecture
# has no hardware support for division, so you will have to make do
# without.  Note that there is a limit to the number of ticks your 
# program may run. Even if your program produces correct output you 
# may need to optimize it if it's not fast enough.
#
# Hint: You might try subtracting one number from another repeatedly. But is
# that really the fastest way?

# Here is some wrapper code to test your solution:

        mov     $0, 39208       # load values to registers $0,$1
        mov     $1, 1610

# Your solution starts here ...
# ------------------------------------------
    mov $2, $0          # $2: remainder, starts with dividend
    mov $3, $1          # $3: divisor

@find_max_shift:
    lsl $4, $3, 1       # $4: divisor shifted left
    cmp $4, $3
    bbw >subtract_loop  # If $4 < $3 (unsigned) we have overflowed, break
    cmp $4, $2          # If $4 > $2 (unsigned), we can't shift more, break
    bab >subtract_loop 
    mov $3, $4          # Shift divisor left and try again
    jmp >find_max_shift

@subtract_loop:
    cmp $2, $3          # If $2 < $3 (unsigned), we can't subtract, skip      
    bbw >no_sub        
    sub $2, $2, $3      # Subtract shifted divisor from remainder

@no_sub:
    cmp $3, $1          # If $3 == $1, we are done, break
    beq >done
    lsr $3, $3, 1
    jmp >subtract_loop

@done:
# ------------------------------------------
# ... and ends here 

        hlt                     # the processor stops here


