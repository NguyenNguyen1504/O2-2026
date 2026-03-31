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

# This assignment asks you to write an assembly-language program
# that calcultes the arithmetic mean [https://en.wikipedia.org/wiki/Arithmetic_mean]
# of a collection of unsigned integers stored in armlet memory.

# Your task is to write an assembly-language program that calculates the
# arithmetic mean of a data array that starts at memory address $0 and
# contains $1 words. The mean must be stored in register $2.
# You can assume that the actual mean is always a whole number.
# What makes this challenging is that there is no hardware support for
# division and that the intermediate sum may overflow.

# Note that there is a limit to the number of ticks your program may run,
# just like in the remainder computation tasks.

# Hints: 
# 1. As you first want to sum the numbers up, but the sum may overflow in
#   a 16-bit register, you have to store the sum as a 32-bit number across
#   two registers (see the 32-bit remainder task). This isn't yet very complicated,
#   even at the assembly level. You simply have to figure out how to spot all the
#   instances when there is an overflow and update the high word accordingly
#   when collecting the sum.
# 2. For the division part, you may want to use long division, like in the
#   remainder computation tasks. Just notice that this time we are interested in
#   the result of the division, and not the remainder which should be 0 at the end.
#   Also notice that despite the divident being a 32-bit number stored in two
#   registers, the result will fit into a 16-bit register. To see why this is
#   the case it is sufficient to just think about what the result was
#   supposed to signify again.


# Here is some wrapper code to test your solution:

        mov     $0, >test_data      # set up the address where to get the data
        mov     $1, >test_len       # set up address where to get the length
        loa     $1, $1              # load the length from memory to $1

# Your solution starts here ...
# ------------------------------------------

	nop # ready for your code overe here

# ------------------------------------------
# ... and ends here 

        hlt                     # the processor stops here

# Here is some test data:
# The average is 6

@test_len:
      %data 5
@test_data:
      %data 7, 5, 3, 7, 8
