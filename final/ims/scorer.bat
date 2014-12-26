@echo off

if -%2-==-- echo %0 answerfile keyfile & exit /b

set bdir=.
set libdir=%bdir%\lib
set CP="%libdir%\liblinear-1.33-with-deps.jar;%libdir%\jwnl.jar;%libdir%\commons-logging.jar;%libdir%\jdom.jar;%libdir%\trove.jar;%libdir%\maxent-2.4.0.jar;%libdir%\opennlp-tools-1.3.0.jar;%bdir%\ims.jar"

set answerfile=%1
set keyfile=%2

java -mx1000m -cp %CP% sg.edu.nus.comp.nlp.ims.util.CScorer %answerfile% %keyfile%
