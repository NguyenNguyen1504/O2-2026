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


# This assignment asks you to evaluate an expression in assembly language.

# Namely, your task is to compute $0 + $1 + $2 - ($3 - $4) and
# store the result in $7. Complete and submit the part delimited
# by "----" and indicated by a "nop" below.

# Here is some wrapper code to test your solution:

        mov     $0, 3908        # load some values to registers $0,$1,$2,$3,$4
        mov     $1, 762
        mov     $2, 5340
        mov     $3, 1623
        mov     $4, 4042

# Your solution starts here ...
# ------------------------------------------

  add $7, $0, $1
  add $7, $7, $2
  sub $3, $3, $4
  sub $7, $7, $3

# ------------------------------------------
# ... and ends here

  hlt     	        # the processor stops here

# (at halt we should have 12429 in $7)


