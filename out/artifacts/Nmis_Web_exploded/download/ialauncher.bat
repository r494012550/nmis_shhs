
echo %1

set para=%1

::echo %para:~10,0%
echo %para:~10,-1%

set para1=%para:~10,-1%

echo %para1%

start /d "C:\Program Files (x86)\Siemens\syngo.via\bin" ialauncher.exe -s1 %para1% -l user1 -p user1 -server 192.168.1.154 -type READ



::pause