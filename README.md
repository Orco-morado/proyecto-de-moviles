# Gestión Móvil de Oportunidades y Activaciones Corporativas

Aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose**, diseñada para que el equipo comercial identifique, organice, priorice y gestione oportunidades de activación corporativa en terreno, integrando persistencia local (offline-first) y sincronización remota en la nube.

---

## Integrantes del Equipo 11
- **Vicente Guajardo**
- **Cristopher Rojas**

---

## Entorno de Desarrollo y Stack Tecnológico

| Categoría | Tecnología / Herramienta | Versión / Detalle |
| :--- | :--- | :--- |
| **IDE** | Android Studio | Gradle Kotlin DSL (`build.gradle.kts`) |
| **Lenguaje** | Kotlin | JVM Target 11 |
| **SDK Android** | Compile / Target / Min SDK | `compileSdk 36` · `targetSdk 36` · `minSdk 24` |
| **Interfaz (UI)** | Jetpack Compose + Material 3 | Navegación con `navigation-compose 2.7.7` |
| **Arquitectura** | MVVM + Repository Pattern | `ViewModel`, `StateFlow`, `AppContainer` (DI manual) |
| **Persistencia Local** | Room + DataStore Preferences | SQLite local con migraciones y KSP |
| **Red y API REST** | Retrofit `2.11.0` + OkHttp `4.12.0` | Gson Converter, Logging Interceptor, Cloud Firestore REST API |
| **Seguridad** | jBCrypt `0.4` + KeyStore (`.jks`) | Hashing de contraseñas y firma digital APK v2 (RSA 2048-bit) |
| **Testing y QA** | JUnit 4, MockK `1.13.12`, MockWebServer `4.12.0`, Robolectric `4.14.1` | Cobertura con **JaCoCo `0.8.12`** |

---

## Arquitectura del Proyecto
La aplicación sigue el patrón **MVVM (Model-View-ViewModel)** con inyección de dependencias manual centralizada en `AppContainer.kt`:

- **`ui/`**: Pantallas en Jetpack Compose (`Oportunidades`, `Organizaciones`, `Contactos`, `Seguimiento`, `Alertas`, `Eventos`, `Login`) y sus respectivos `ViewModel`.
- **`data/local/`**: Base de datos **Room** (`AppDatabase`), entidades, DAOs, convertidores y datos semilla (`SeedData`).
- **`data/remote/`**: Cliente **Retrofit** (`ApiClient`, `OportunidadApi`, DTOs, mappers y `OportunidadRemoteDataSourceImpl`) conectado a la API REST de **Cloud Firestore** mediante `BuildConfig.API_BASE_URL`.
- **`data/repository/`**: Repositorios que implementan estrategia de **caché local + sincronización remota** (la UI observa Room en tiempo real; las operaciones CRUD impactan el backend y refrescan la caché).
- **`data/validation/`**: Reglas de validación de formularios desacopladas para pruebas unitarias.

---

## Funcionalidades Implementadas
- **Interfaz moderna:** Jetpack Compose con Material Design 3, animaciones (`AnimatedVisibility`) e identidad visual institucional.
- **Autenticación y roles:** Inicio de sesión con persistencia de sesión (`SessionManager`) y control de permisos por rol (solo `SUPERVISOR`/`admin` o el creador pueden eliminar una oportunidad).
- **Integración API REST + Caché Offline:** Consumo de endpoints `GET`, `POST`, `PATCH` y `DELETE` con Retrofit. Incluye protección de llaves foráneas (`mapearConFkSegura`) para evitar fallos en Room si un ID remoto no existe localmente.
- **Manejo de estados de red:** Indicadores de carga, captura de errores y opción de reintento manteniendo visibles los datos locales en caso de caída de conexión.
- **Formularios validados:** Validaciones en tiempo real con retroalimentación visual.
- **Recursos nativos:** Acceso a cámara para captura de evidencias (`FileProvider`), geolocalización GPS y recordatorios mediante notificaciones locales.

---

## Pruebas Unitarias y Cobertura (JaCoCo)
El proyecto cuenta con **30 pruebas unitarias** cubriendo mappers, cliente remoto (`MockWebServer`), repositorio con caché (`Room` en memoria), `ViewModel` (`MockK` + `MainDispatcherRule`), validaciones y alertas, además de pruebas instrumentadas de migración (`DatabaseMigrationTest`).

Para ejecutar los tests y generar el reporte de cobertura:
```bash
./gradlew testDebugUnitTest jacocoTestReport
