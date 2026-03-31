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


# This assignment asks you to implement some basic word operations
# with assembly language.

# Namely, your solution should implement all of the following:
# 1) Reverse the order of bits in $0 and store the result in $5
#    (that is, the bit in position 0 goes to position 15, position 1 goes
#    to position 14, position 2 goes to position 13, ..., and position 15
#    goes to position 0; the contents of $0 must remain unchanged);
# 2) Count the number of 1-bits in $0 and store the result in $6; and
# 3) Rotate the bits in $0 by one position to the left and store 
#    the result in $7. (That is, bit in position 0 goes to position 1, 
#    position 1 goes to 2, position 2 goes to 3, ..., position 14 goes 
#    to position 15, and position 15 goes to position 0.)

# Here is some wrapper code to test your solution:
        
        mov     $0, 62361       # load test input to $0
                
# Your solution starts here ...
# ------------------------------------------
# Initialize
mov $5, 0        # bit reversing result
mov $6, 0        # bit1 counting result
mov $1, 16       # i = 16
mov $2, $0       # copy of input

@main_loop:
  and $3, $2, 1  # take last bit of input
  add $6, $6, $3 # if it is 1 then add to bit1 counter
  ior $5, $5, $3 # put it to the end of $5

  lsr $2, $2, 1  # right shift copy
  sub $1, $1, 1  # i -= 1
  cmp $1, 0
  beq >rotate_task # if i == 0 (finished) then jump

  lsl $5, $5, 1  # left shift $5
  jmp >main_loop # return to main loop

@rotate_task:
  lsl $7, $0, 1  # shift input left, save to $7
  lsr $3, $0, 15 # save MSB of input to $3
  ior $7, $7, $3 # put it to the end of $7
# ------------------------------------------
# ... and ends here 

        hlt                     # the processor stops here

# (at halt we should have 39375 in $5, 10 in $6, and 59187 in $7)


  temp = input >>> bounce <<  i
  result += temp
