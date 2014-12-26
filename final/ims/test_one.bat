@echo off

if -%3-==-- echo %0 modeldir testfile savedir [index.sense] & exit /b

set bdir=.
set libdir=%bdir%\lib
set CP="%libdir%\liblinear-1.33-with-deps.jar;%libdir%\jwnl.jar;%libdir%\commons-logging.jar;%libdir%\jdom.jar;%libdir%\trove.jar;%libdir%\maxent-2.4.0.jar;%libdir%\opennlp-tools-1.3.0.jar;%bdir%\ims.jar;%libdir%\edu.mit.jwi_2.1.4.jar;%libdir%\edu.sussex.nlp.jws.05090156.jar"

set modeldir=%1
set testfile=%2
set savedir=%3

if -%4-==-- (
	java -mx1000m -cp %CP% sg.edu.nus.comp.nlp.ims.implement.CTester -ptm %libdir%\tag.bin.gz -tagdict %libdir%\tagdict.txt -ssm %libdir%\EnglishSD.bin.gz -prop %libdir%\prop.xml -r sg.edu.nus.comp.nlp.ims.io.CResultWriter %testfile% %modeldir% %modeldir% %savedir% -f sg.edu.nus.comp.nlp.ims.feature.CFeatureExtractorCombination
) else (
	java -mx1000m -cp %CP% sg.edu.nus.comp.nlp.ims.implement.CTester -ptm %libdir%\tag.bin.gz -tagdict %libdir%\tagdict.txt -ssm %libdir%\EnglishSD.bin.gz -prop %libdir%\prop.xml -r sg.edu.nus.comp.nlp.ims.io.CResultWriter %testfile% %modeldir% %modeldir% %savedir% -is %4 -f sg.edu.nus.comp.nlp.ims.feature.CFeatureExtractorCombination
)
