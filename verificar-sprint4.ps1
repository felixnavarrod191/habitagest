# Ejecuta mvn verify en los 8 módulos y resume el resultado (Sprint 4).
# Uso, desde C:\habitagest:  .\verificar-sprint4.ps1
$modulos = "ms-copropiedad","ms-finanzas","ms-reservas","ms-pqr","ms-porteria","ms-mantenimiento","api-gateway","web"
$raiz = Get-Location
$resumen = @()
foreach ($m in $modulos) {
  Write-Host "=== $m ===" -ForegroundColor Cyan
  Set-Location (Join-Path $raiz $m)
  $salida = mvn -B verify 2>&1
  $pruebas = ($salida | Select-String "Tests run:.*Fail" | Select-Object -Last 1).Line
  $ok = ($salida | Select-String "BUILD SUCCESS") -ne $null
  $resumen += [pscustomobject]@{ Modulo = $m; Resultado = $(if ($ok) { "BUILD SUCCESS" } else { "BUILD FAILURE" }); Pruebas = $pruebas }
  if (-not $ok) { $salida | Select-String "ERROR|FAIL" | Select-Object -First 25 | ForEach-Object { $_.Line } }
}
Set-Location $raiz
$resumen | Format-Table -AutoSize
if (($resumen | Where-Object { $_.Resultado -ne "BUILD SUCCESS" }).Count -eq 0) { "TODO CORRECTO" } else { "HAY MODULOS CON FALLOS: copia el mensaje de ERROR y envíalo." }
