#
# Python utf-8 script loader
# 2024 Anthony T.
#
"""
Ensure that a target script always outputs with UTF-8 Encoding, no matter the terminal.
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
