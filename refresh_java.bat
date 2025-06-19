@echo off
echo Nettoyage du cache Java Language Server...
echo.

REM Fermer VS Code/Cursor si ouvert
taskkill /F /IM Cursor.exe >nul 2>&1
taskkill /F /IM Code.exe >nul 2>&1

REM Supprimer le dossier workspace
SET JAVA_LS_DIR=%USERPROFILE%\.vscode\extensions\redhat.java-*\server\config_win
echo Suppression de %JAVA_LS_DIR%\...
IF EXIST "%JAVA_LS_DIR%" rmdir /S /Q "%JAVA_LS_DIR%"

REM Supprimer également le dossier de Cursor si présent
SET CURSOR_JAVA_LS_DIR=%USERPROFILE%\.cursor\extensions\redhat.java-*\server\config_win
echo Suppression de %CURSOR_JAVA_LS_DIR%\...
IF EXIST "%CURSOR_JAVA_LS_DIR%" rmdir /S /Q "%CURSOR_JAVA_LS_DIR%"

echo.
echo Nettoyage terminé. Vous pouvez maintenant redémarrer VS Code/Cursor.
echo.
pause 