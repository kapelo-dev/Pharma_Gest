@echo off
echo Démarrage de l'application PharmacieGestion...
echo.

REM Vérifier si le JAR existe
if not exist "target\PharmacieGestion-1.0-SNAPSHOT-jar-with-dependencies.jar" (
  echo Le fichier JAR n'existe pas. Compilation du projet...
  call mvn clean package
)

REM Lancer l'application
java -jar target\PharmacieGestion-1.0-SNAPSHOT-jar-with-dependencies.jar

echo.
echo Application arrêtée. Appuyez sur une touche pour fermer cette fenêtre.
pause > nul 