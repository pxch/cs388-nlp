@echo off

if -%3-==-- echo %0 train.xml train.key savedir [s2 c2] & exit /b

set s2=0
if not -%4-==-- set s2 = %4

set c2=0
if not -%5-==-- set c2 = %5

set bdir=.
set libdir=%bdir%\lib
set CP="%libdir%\liblinear-1.33-with-deps.jar;%libdir%\jwnl.jar;%libdir%\commons-logging.jar;%libdir%\jdom.jar;%libdir%\trove.jar;%libdir%\maxent-2.4.0.jar;%libdir%\opennlp-tools-1.3.0.jar;%bdir%\ims_original.jar;%libdir%\edu.mit.jwi_2.1.4.jar;%libdir%\edu.sussex.nlp.jws.05090156.jar"

set xmlfile=%1
set keyfile=%2
set savedir=%3

java -mx1000m -cp %CP% sg.edu.nus.comp.nlp.ims.implement.CTrainModel -prop %libdir%\prop.xml -ptm %libdir%\tag.bin.gz -tagdict %libdir%\tagdict.txt -ssm %libdir%\EnglishSD.bin.gz %xmlfile% %keyfile% %savedir% -f sg.edu.nus.comp.nlp.ims.feature.CFeatureExtractorCombination -s2 %s2% -c2 %c2%
