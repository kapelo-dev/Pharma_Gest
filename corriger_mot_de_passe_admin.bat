@echo off
echo ===== CORRECTION DU MOT DE PASSE ADMINISTRATEUR =====
echo.
echo Ce script va corriger le mot de passe de l'utilisateur admin
echo pour vous permettre de vous connecter a l'application.
echo.
echo Si l'utilisateur admin n'existe pas, il sera cree.
echo.
echo Appuyez sur une touche pour continuer...
pause > nul

echo.
echo Execution de l'outil de correction...
echo.

java -cp target/PharmacieGestion-1.0-SNAPSHOT-jar-with-dependencies.jar com.pharmajava.tools.AdminPasswordFixer

echo.
echo ===== OPERATION TERMINEE =====
echo Vous pouvez maintenant lancer l'application avec demarrer.bat
echo et vous connecter avec:
echo   Identifiant: admin
echo   Mot de passe: admin123
echo.
echo Appuyez sur une touche pour fermer...
pause > nul 