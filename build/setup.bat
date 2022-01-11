@echo off
rem #%L
rem Codenjoy - it's a dojo-like platform from developers to developers.
rem %%
rem Copyright (C) 2012 - 2022 Codenjoy
rem %%
rem This program is free software: you can redistribute it and/or modify
rem it under the terms of the GNU General Public License as
rem published by the Free Software Foundation, either version 3 of the
rem License, or (at your option) any later version.
rem
rem This program is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
rem GNU General Public License for more details.
rem
rem You should have received a copy of the GNU General Public
rem License along with this program.  If not, see
rem <http://www.gnu.org/licenses/gpl-3.0.html>.
rem #L%
@echo on

if "%VERSION%"=="" set VERSION=1.1.3

echo VERSION=%VERSION%

if exist ".\engine-%VERSION%-pom.xml" (
    call :build_engine_from_zip
    call :build_java_client
)
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

:build_java_client
    echo Build games

    call %MVNW% install:install-file -Dfile=client-java-%VERSION%.jar -Dsources=client-java-%VERSION%-sources.jar -DpomFile=client-java-%VERSION%.pom -DgroupId=com.codenjoy -DartifactId=client-java -Dversion=%VERSION% -Dpackaging=jar

    echo off
    echo [44;93m
    echo        +--------------------------------------------------+
    echo        !         Check that BUILD was SUCCESS             !
    echo        +--------------------------------------------------+
    echo [0m
    echo on
    if "%DEBUG%"=="true" (
        pause >nul
    )
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