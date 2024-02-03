cd ..
gradlew.bat wrapper
gradlew.bat clean
gradlew.bat build -x testClasses -x test

::cd docker\

docker build -t booking-service -f docker\Dockerfile .

for /f "delims=[] tokens=2" %%a in ('ping-4 -n 1 %ComputerName% ^| findstr [') do set NetworkIP=%%a
echo Network IP: %NetworkIP%
set DOCKERHOST=%NetworkIP%

docker-compose -f docker\docker-compose.yaml up