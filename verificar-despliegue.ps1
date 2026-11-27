# Verifica que los 8 servicios de Render respondan (HU-33, NFR-PH-005).
# Edita las direcciones si Render asignó nombres distintos.
$base = @{
  "copropiedad"   = "https://torres-mirador-copropiedad.onrender.com"
  "finanzas"      = "https://torres-mirador-finanzas.onrender.com"
  "reservas"      = "https://torres-mirador-reservas.onrender.com"
  "pqr"           = "https://torres-mirador-pqr.onrender.com"
  "porteria"      = "https://torres-mirador-porteria.onrender.com"
  "mantenimiento" = "https://torres-mirador-mantenimiento.onrender.com"
  "gateway"       = "https://torres-mirador-gateway.onrender.com"
  "web"           = "https://torres-mirador-web.onrender.com"
}
$negocio = "copropiedad","finanzas","reservas","pqr","porteria","mantenimiento"
$fallos = 0
foreach ($n in $base.Keys | Sort-Object) {
  $url = $base[$n] + "/actuator/health"
  try {
    $r = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 120
    $estado = if ($r.Content -match '"status"\s*:\s*"UP"') { "UP" } else { "RESPONDE pero no UP" ; $fallos++ }
  } catch { $estado = "SIN RESPUESTA"; $fallos++ }
  "{0,-14} health  {1}" -f $n, $estado
  if ($negocio -contains $n) {
    try {
      $r = Invoke-WebRequest -Uri ($base[$n] + "/swagger-ui.html") -UseBasicParsing -TimeoutSec 60
      "{0,-14} swagger {1}" -f $n, $r.StatusCode
    } catch { "{0,-14} swagger SIN RESPUESTA" -f $n; $fallos++ }
  }
}
if ($fallos -eq 0) { "TODO CORRECTO" } else { "FALLOS: $fallos" }
