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
  result = subprocess.Popen([sys.executable, target_script] + args, env=env)
  exit_code = result.returncode
  

except subprocess.CalledProcessError as e:
  print("Error:", e)
  sys.exit(exit_code)
