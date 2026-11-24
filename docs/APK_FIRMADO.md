# APK firmado

Generado el 2026-11-24 desde `main` con:

```powershell
.\gradlew.bat assembleRelease --no-daemon --console=plain
```

El APK firmado se copió a `apk/proyecto-app-movil-release.apk` (12.251.728 bytes).

Hash SHA-256 del archivo:

```text
3C7A1640F5C2713CC8F346C37BB3B5446F2113806B8BB00099F68952382691D4
```

`apksigner` no está en `PATH`, así que se usó la ruta completa de Build Tools:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\37.0.0\apksigner.bat" verify --print-certs apk\proyecto-app-movil-release.apk
```

Salida de la verificación:

```text
V2 Signer: certificate DN: CN=ProyectoMovil, OU=DuocUC, O=DuocUC, L=Santiago, ST=RM, C=CL
V2 Signer: certificate SHA-256 digest: 3f95c5741dd56efb6f5417b62ebf0f0d9537f45e4371a1e0f9516a5f4f63141e
V2 Signer: certificate SHA-1 digest: f80b04b89732abf16196d9de0f9ce593bbbd3eec
V2 Signer: certificate MD5 digest: aeb37d101a59bcad2ad16fe66736c356
```

No se modificó la configuración de firma: se usó el `signingConfigs.release` ya existente en `app/build.gradle.kts`.
