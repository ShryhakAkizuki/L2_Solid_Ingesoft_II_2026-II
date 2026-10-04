$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$jar = "lib\junit-platform-console-standalone-1.13.4.jar"

Write-Host "== Compilando src ==" -ForegroundColor Cyan
javac -encoding UTF-8 -d out src\*.java
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "== Compilando test ==" -ForegroundColor Cyan
javac -encoding UTF-8 -cp "out;$jar" -d out test\*.java
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "== Ejecutando pruebas ==" -ForegroundColor Cyan
java -cp "out;$jar" org.junit.platform.console.ConsoleLauncher `
    --select-class TransaccionServiceTest `
    --details=tree `
    --disable-banner
exit $LASTEXITCODE
