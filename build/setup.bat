if "%VERSION%"=="" set VERSION=1.1.1

echo VERSION=%VERSION%

if exist ".\engine-%VERSION%-pom.xml" call :build_engine_from_zip
if exist ".\..\build" call :build_engine_from_sources
call :build_games
goto :eof

:build_games
    echo Build games

    call %MVNW% install:install-file -Dfile=games-%VERSION%-pom.xml -DpomFile=games-%VERSION%-pom.xml -DgroupId=com.codenjoy -DartifactId=games -Dversion=%VERSION% -Dpackaging=pom

    echo off
    echo [44;93m
    echo        +--------------------------------------------------+
    echo        !         Check that BUILD was SUCCESS             !
    echo        !           Then press Enter to exit               !
    echo        +--------------------------------------------------+
    echo [0m
    echo on

    pause >nul
    goto :eof

:build_engine_from_zip
    echo Build engine from zip

    set ROOT=%cd%
    set MVNW=%ROOT%\mvnw

    call %MVNW% install:install-file -Dfile=engine-%VERSION%.jar -Dsources=engine-%VERSION%-sources.jar -DpomFile=engine-%VERSION%-pom.xml -DgroupId=com.codenjoy -DartifactId=engine -Dversion=%VERSION% -Dpackaging=jar

    echo off
    echo [44;93m
    echo        +--------------------------------------------------+
    echo        !           Check that BUILD was SUCCESS           !
    echo        +--------------------------------------------------+
    echo [0m
    echo on
    if "%DEBUG%"=="true" (
        pause >nul
    )

    goto :eof

:build_engine_from_sources
    echo Build engine from sources

    cd ..
    set ROOT=%cd%
    set MVNW=%ROOT%\mvnw

    call %MVNW% clean install -DskipTests=true

    echo off
    echo [44;93m
    echo        +--------------------------------------------------+
    echo        !           Check that BUILD was SUCCESS           !
    echo        +--------------------------------------------------+
    echo [0m
    echo on
    if "%DEBUG%"=="true" (
        pause >nul
    )

    copy ..\pom.xml .\target\games-%VERSION%-pom.xml
    cd .\target
    goto :eof