@echo off
echo Réinitialisation du mot de passe administrateur...
echo.

REM Demander les informations de connexion MySQL
set /p MYSQL_USER=Entrez votre nom d'utilisateur MySQL (par défaut: root): 
if "%MYSQL_USER%"=="" set MYSQL_USER=root

REM Exécuter le script SQL
echo Exécution du script SQL de correction...
mysql -u %MYSQL_USER% -p pharmacie_db < database\scripts\fix_admin_password.sql

echo.
echo Terminé! Vous pouvez maintenant vous connecter avec:
echo Identifiant: admin
echo Mot de passe: admin123
echo.
pause 