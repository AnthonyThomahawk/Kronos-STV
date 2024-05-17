#
# Python utf-8 script loader
# Copyright 2024 Anthony T.
#
"""
Calls a target script with a redirected stdin where the contents of an input file is read into as utf-8 text. 
Input file name is parsed from the commandline, the switch is removed and the rest of the commadline (if any) is passed to the target script for any further processing.
stdout is set to utf-8 so target output is also utf-8. No changes are required to original target script.
"""

import sys
import subprocess
import os
import argparse

# change stdout encoding to UTF-8
sys.stdout.reconfigure(encoding='utf-8') 
# ensure the target script runs with the same encoding
env = os.environ.copy()
env['PYTHONIOENCODING'] = 'utf-8'

target_script = 'stv.py'

if not os.path.isfile(target_script):
  print("File not found.")
  sys.exit(-1)

try:
  # grab commandline
  args = sys.argv[1:] 
  parser = argparse.ArgumentParser()
  # define cli switch containing input file name
  parser.add_argument('-b', type=str)
  # Parse commandline and extract filename switch
  known_args, remaining_args = parser.parse_known_args(args)
  input_name = known_args.b
  # open input filename as utf-8
  with open(input_name, "r", encoding="utf-8") as file:
    # start target subprocess with redirected stdin, and this process' environment
    result = subprocess.Popen([sys.executable, target_script] + remaining_args, stdin=subprocess.PIPE, env=env)
    # read contents of input file into stdin as utf-8 text
    file_contents = file.read()
    result.communicate(input=file_contents.encode("utf-8"))
    exit_code = result.returncode

except subprocess.CalledProcessError as e:
  print("Error:", e)
  sys.exit(exit_code)
