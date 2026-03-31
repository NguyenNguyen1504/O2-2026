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
# computes the greatest common divisor of two positive integers.
        
# Namely, your task is to compute the greatest common divisor of $0 and $1.
# The result must be stored into $2. Both $0 and $1 are guaranteed to
# be nonzero. Complete and submit the part delimited
# by "----" and indicated by a "nop" below.
#
# What makes this task challenging is that our "armlet" architecture
# has no hardware support for division, so you will have to make do
# without.  Note that there is a limit to the number of ticks your 
# program may run. Even if your program produces correct output you 
# may need to optimize it if it's not fast enough.
#
# Hint: Greatest common divisors may be computed using, for example,
# the Euclidean algorithm or the Binary GCD algorithm (faster).

# Here is some wrapper code to test your solution:

        mov     $0, 26064       # load values to registers $0,$1
        mov     $1, 4706

# Your solution starts here ...
# ------------------------------------------
        
        nop                     # ready for your code over here

# ------------------------------------------
# ... and ends here 

        hlt                     # the processor stops here

# (at halt we should have 362 in $2)


