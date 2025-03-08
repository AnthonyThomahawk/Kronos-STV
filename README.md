# Kronos STV
Kronos is a decision support system for STV (Single transferrable vote) elections. It was specifically designed for Greek university council elections, and is based upon the back-end of ZEUS, a digital voting system for greek elections.
# Why
ZEUS, and by extension Helios (which is the system ZEUS is based on), are hard to use if one desires to create many test scenarios. Each scenario requires a lot of spare e-mail addresses (at least as many voters in the election) and also requires multiple browsers with seperate cookies, making it a very hard procedure for anyone who wants to view the results of hypothetical elections quickly.<br><br>
Kronos makes it easy to examine multiple STV election scenarios with a user friendly GUI and simple to use workflow.<br>
Kronos is also able to find certain vote limits, where certain user-defined groups of people get elected.
# Features
- Simple to use GUI
- Examine STV election scenarios quickly
- Find the vote limit required for specific groups of candidates to win the election
- Export/Import scenario files
# Implementation
Kronos is written in Java with the back-end being in Python 3.
The entire application is self-contained in a single JAR file and only requires python 3 and java to be installed on the user's system. It is also completely cross-platform, supporting most OS's and CPU architectures.
# Installation
## Windows x86_64 and x86
For the Windows x86 platform, standalone binaries are provided.<br>
No dependencies are required.<br>
[Windows standalone Installer download](https://github.com/AnthonyThomahawk/Kronos-STV/releases/latest/download/KronosInstaller.exe)<br>
## Other platforms
For every other platform, the user is required to have Java 8 or newer and Python 3.8 or newer installed on their system. If those requirements are met, they can use the standalone JAR file.<br>
[Standalone JAR file download](https://github.com/AnthonyThomahawk/Kronos-STV/releases/latest/download/Kronos.jar)
# Tested environments
The currently tested platforms are :
- Windows 10/11 x86_64 and x86
- Linux OpenSUSE/Arch/Ubuntu x86_64
- Linux Debian arm64

MacOS is untested, but it should work.