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
# multiplies two integers.
        
# Namely, your task is to compute $0 * $1 and store the result in $2.
# The result is guaranteed to be in the range 0,1,...,65535.
# Both $0 and $1 should be viewed as unsigned integers.
# Complete and submit the part delimited by "----" and indicated by
# a "nop" below.

# Here is some wrapper code to test your solution:

        mov     $0, 82          # load some values to registers $0,$1
        mov     $1, 430

# Your solution starts here ...
# ------------------------------------------
        mov $2, 0          # set accumulator for the result = 0

@loop:
    cmp $1, 0  # if $1 == 0, we are done
    beq >done

    and $3, $1, 1      # Save LSB of $1 (the current bit) to $3
    cmp $3, 0
    beq >skip_add      # If the bit is 0, skip the addition step
    add $2, $2, $0     # If the bit is 1, add $0 to the result in $2

@skip_add:
    lsl $0, $0, 1      # Left shift $0 (x2 for the next bit)
    lsr $1, $1, 1      # Right shift $1 (/2 to process the next bit)
    jmp >loop

@done:
# ------------------------------------------
# ... and ends here 

        hlt                     # the processor stops here

# (at halt we should have 35260 in $2)


