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


# This assignment asks you to find the most frequent value in a data array.
        
# Namely, you must find the most frequently occurring value in a data
# array that starts at memory address $0 and contains $1 words.
# The most frequent value must be stored in register $2.
# Complete and submit the part delimited by "----" and indicated
# by a "nop" below.

# Here is some wrapper code to test your solution:

        mov     $0, >test_data  # set up the address where to get the data
        mov     $1, >test_len   # set up address where to get the length
        loa     $1, $1          # load the length from memory to $1

# Your solution starts here ...
# $0: first item
# $1: last item
# $2: candidate max
# $3: address of candidate max
# $4: current address
# $5: current item
# ------------------------------------------
      add $1, $1, $0
      sub $1, $1, 1           # address of the last data item
@select_loop:
      cmp $0, $1              # compare start address and last address
      bae >count_loop               # ... if start addr >= last addr, we are done
      loa $2, $0              # set up a candidate maximum
      mov $3, $0              # set up address of candidate maximum
      add $4, $0, 1           # set up current address
@max_scan_loop:
      cmp $4, $1              # compare current address with last address
      bab >scan_done          # ... if curr addr > last addr, we have the max
      loa $5, $4              # load current data item
      cmp $5, $2              # compare current item with candidate max
      bbe >no_update          # ... if current <= candidate, no need to update
      mov $2, $5              # update candidate maximum
      mov $3, $4              # update address of candidate maximum
@no_update:
      add $4, $4, 1           # advance to next element
      jmp >max_scan_loop      # continue scanning
@scan_done:
      # at this point $2 is the max item and $3 is its addr in array
      # transpose max item and last item in current array ...
      sub $4, $4, 1           # address of last item
      loa $5, $4              # load last item
      sto $4, $2              # store max to last position
      sto $3, $5              # store last item to max position
      sub $1, $1, 1           # remove last item (now =max) from consideration
      jmp >select_loop        # continue sorting the remaining array
@count_loop:
      mov   $1, >test_len
      loa   $1, $1          
      add   $1, $1, $0      # last address of the array
      
      mov   $3, 0           # $3: max freqency count
      mov   $2, 0           # $2: most frequent value
      
@outer_scan:
      cmp   $0, $1
      bae   >done           # stop if scanned the whole array
      
      loa   $4, $0          # $4 curret value
      mov   $5, 0           # $5 current frequency count
      mov   $6, $0          # $6 current address for inner scan

@inner_count:
      cmp   $6, $1
      bae   >check_max      # check max frquency if reached end of array
      loa   $7, $6          # load current item
      cmp   $7, $4
      bne   >check_max      # if current item != current value, check max frequency
      
      add   $5, $5, 1       # increment frequency count
      add   $6, $6, 1       # increment inner scan address
      jmp   >inner_count

@check_max:
      cmp   $5, $3
      bbe   >next_group     # if current frequency <= max frequency, move to next group
      mov   $3, $5          # else, update max frequency
      mov   $2, $4          # update most frequent value

@next_group:
      mov   $0, $6          # advance outer scan address to the next group
      jmp   >outer_scan

@done:
# ------------------------------------------
# ... and ends here 

        hlt                     # the processor stops here

# Here is the test data:
# (the most frequent value is 56369 with frequency 5 in the array)

@test_len:
        %data   23
@test_data:
        %data   18701, 1100, 590, 17017, 56369, 19296, 1100, 56369, 1100, 56369, 590, 18701, 19296, 19296, 56369, 1100, 55034, 590, 29135, 18701, 18701, 29135, 56369


