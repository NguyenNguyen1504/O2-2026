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


# This assignment asks you to find the range of a data array.

# Namely, you must find the range of the data array that starts
# at memory address $0 and contains $1 words.
# The range is the maximum value minus the minimum value in the data.
# All values should be viewed as unsigned.
# The range must be stored in register $2.
# Complete and submit the part delimited by "----" and indicated by
# a "nop" below.


# Let us set up some test data ...

        mov     $0, >test_data  # set up the address where to get the data
        mov     $1, >test_len   # set up address where to get the length
        loa     $1, $1          # load the length from memory to $1

# The solution you submitted starts here ...
# ------------------------------------------
cmp $1, 0                    # compare length with 0
      bab >have_data               # ... if length > 0, branch to process data
      hlt                          # ... otherwise halt immediately
@have_data:
      mov $7, $0                   # $7 pointer
      add $5, $7, $1               # set up last address
      loa $2, $7                   # set up a candidate maximum
      loa $3, $7                   # set up a candidate minimum
      add $7, $7, 1                # advance current address
      cmp $7, $5                   # are we at the last address?
      bbw >scan_loop               # ... if not, continue scanning
      jmp >calc_range              # ... otherwise we are done

@scan_loop:
      loa $6, $7                   # load current data item
      cmp $6, $2                   # compare current with candidate maximum
      bbe >no_update_max           # ... if current <= candidate, no update
      mov $2, $6                   # ... update candidate maximum
@no_update_max:
      cmp $6, $3                   # compare current with candidate minimum
      bab >no_update_min           # ... if current >= candidate, no update
      mov $3, $6                   # ... update candidate minimum
@no_update_min:
      add $7, $7, 1                # advance current address
      cmp $7, $5                   # compare current address with last address
      bbw >scan_loop               # ... if curr addr < last addr, continue scan

@calc_range:
      sub $2, $2, $3               # $2 = max - min = range

@done:
# ------------------------------------------
        hlt

# ------------------------------------------
# ... and ends here

        hlt                     # the processor stops here
# Here is some test data:
# (the minimum is 151 and the maximum is 9978, so the range is 9978-151 = 9827)

@test_len:
        %data   35
@test_data:
        %data   6277, 1692, 8747, 5105, 6424, 6431, 1311, 4497, 1112, 806, 7346, 5891, 6225, 295, 8615, 2294, 5190, 151, 4255, 6114, 9978, 3836, 7304, 1808, 5982, 3809, 7795, 1222, 6552, 4946, 7264, 7249, 8476, 2887, 9384


