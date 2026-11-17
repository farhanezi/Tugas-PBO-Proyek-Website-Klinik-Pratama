$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force -Path bin | Out-Null
$tmpList = Join-Path $env:TEMP "klinik_sources.txt"
Get-ChildItem -Path src -Recurse -Filter "*.java" | ForEach-Object { $_.FullName } | Set-Content -Path $tmpList -Encoding ASCII
try {
    javac -encoding UTF-8 -cp "lib/*" -d bin "@$tmpList"
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
} finally {
    Remove-Item -Path $tmpList -ErrorAction SilentlyContinue
}
Write-Host "Build selesai. Menjalankan Klinik Pratama..."
java -cp "bin;resources;lib/*" klinik.Main
