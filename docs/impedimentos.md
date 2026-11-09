# Registro de impedimentos

| ID | Fecha | Quién lo reporta | Descripción | Tarjeta afectada | Estado | Resolución |
|---|---|---|---|---|---|---|
| IMP-01 | 2026-11-08 | Felix José Navarro Ortiz | Los pull requests #3 (PBI-02) y #4 (D03 y D04) se fusionaron sin aprobación previa y #3 antes del turno que le correspondía | PBI-02, D03 y D04 | Resuelto | Revisión posterior por escrito en cada pull request (plantillas H8, H4 y H3) y acción de mejora en la Retro |
| IMP-02 | 2026-11-08 | Felix José Navarro Ortiz | Los contratos OpenAPI del primer paquete traían alias YAML que Swagger Editor marca como error | D06 | Resuelto | Contratos regenerados sin alias y verificados en Swagger Editor |
| IMP-03 | 2026-11-08 | Felix José Navarro Ortiz | El commit del esqueleto quedó en main local y la rama feature se subió vacía | PBI-02 | Resuelto | El commit se movió a la rama feature y main se restauró con git reset --hard origin/main |
| IMP-04 | 2026-11-08 | Estiven Alexander Rodríguez Pérez | Los pull requests #1 y #4 los fusionó el revisor y no el autor, y el pull request #9 (actas de las puertas 0, 1 y 3) no estaba en la secuencia del sprint | D01, D03, D04, D02, D05 y D07 | Resuelto | Se documenta en la Review y la Retro; desde el Sprint 2 fusiona el autor, con la aprobación de otra persona |

Cada impedimento se anota aquí con el siguiente IMP-## (IMP-05, IMP-06…) el día en que aparece.