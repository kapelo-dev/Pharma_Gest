[Setup]
AppName=PharmaGest
AppVersion=1.0.0
AppPublisher=PharmaGest
AppPublisherURL=https://pharmacie-gestion.com
AppSupportURL=https://pharmacie-gestion.com/support
AppUpdatesURL=https://pharmacie-gestion.com/updates
DefaultDirName={pf}\PharmaGest
DefaultGroupName=PharmaGest
OutputDir=output
OutputBaseFilename=PharmaGest_Setup
Compression=lzma
SolidCompression=yes
SetupIconFile=src\main\resources\icons\pharmacy.ico
UninstallDisplayIcon={app}\PharmaGest.exe
WizardStyle=modern
ArchitecturesInstallIn64BitMode=x64

[Languages]
Name: "french"; MessagesFile: "compiler:Languages\French.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "PharmaGest.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "config\*"; DestDir: "{app}\config"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\PharmaGest"; Filename: "{app}\PharmaGest.exe"
Name: "{autodesktop}\PharmaGest"; Filename: "{app}\PharmaGest.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\PharmaGest.exe"; Description: "{cm:LaunchProgram,PharmaGest}"; Flags: nowait postinstall skipifsilent

[Code]
function InitializeSetup(): Boolean;
begin
  Result := True;
  // Vérifier si Java est installé
  if not RegKeyExists(HKEY_LOCAL_MACHINE, 'SOFTWARE\JavaSoft\Java Runtime Environment') then
  begin
    if MsgBox('Java n''est pas installé. Voulez-vous le télécharger maintenant ?', 
      mbConfirmation, MB_YESNO) = IDYES then
    begin
      ShellExec('open', 'https://adoptium.net/', '', '', SW_SHOW, ewNoWait);
    end;
    Result := False;
  end;
end; 