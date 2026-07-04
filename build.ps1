# Compiles every assignment against algs4.jar into out/<assignment>/.
# Usage:  powershell -ExecutionPolicy Bypass -File build.ps1
$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$jar  = Join-Path $root "lib\algs4.jar"

if (-not (Test-Path $jar)) {
    Write-Error "lib/algs4.jar not found. Download it from https://algs4.cs.princeton.edu/code/algs4.jar"
    exit 1
}

Get-ChildItem (Join-Path $root "src") -Directory | ForEach-Object {
    $name = $_.Name
    $outDir = Join-Path $root "out\$name"
    New-Item -ItemType Directory -Force -Path $outDir | Out-Null
    $sources = Get-ChildItem $_.FullName -Filter *.java | ForEach-Object { $_.FullName }
    Write-Host "Compiling $name ($($sources.Count) file(s)) ..."
    & javac -cp $jar -d $outDir $sources
    if ($LASTEXITCODE -ne 0) { Write-Error "Failed to compile $name"; exit 1 }
}
Write-Host "All assignments compiled to out/."
