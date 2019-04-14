@ECHO OFF
setlocal enabledelayedexpansion

SET DEFINITION = ""
:: skip=5 para pular as 5 primeiras linhas do output
for /f "skip=5 delims=" %%A in ('echo display qlocal^(*^) where^(curdepth gt 0^) ^| runmqsc QMEXPRD_2') DO (
	SET LINE=%%A
	IF "!LINE:~0,7!" EQU "AMQ8409" (
		IF "!DEFINITION!" NEQ "" (
			for /f "delims=() tokens=2,6" %%Q in ("!DEFINITION!") DO (
				ECHO A fila %%Q contem %%R mensagens
			)
			REM ECHO !DEFINITION!
		)
		SET "DEFINITION="
	) ELSE (
		SET DEFINITION=!DEFINITION!!LINE!
	)
)
endlocal