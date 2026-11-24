# Gestión Móvil de Oportunidades y Activaciones Corporativas

Aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose**, diseñada para que el equipo comercial identifique, organice, priorice y gestione oportunidades de activación corporativa en terreno, integrando persistencia local (offline-first), sincronización con un microservicio REST y consulta de feriados para apoyar la planificación de activaciones.

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
| **Red y API REST** | Retrofit `2.11.0` + OkHttp `4.12.0` | `converter-kotlinx-serialization 2.11.0`, Logging Interceptor, microservicio de oportunidades y API pública Nager.Date |
| **Seguridad** | jBCrypt `0.4` + KeyStore (`.jks`) | Hashing de contraseñas y firma digital APK v2 (RSA 2048-bit) |
| **Testing y QA** | JUnit 4, MockK `1.13.12`, MockWebServer `4.12.0`, Robolectric `4.14.1` | Cobertura con **JaCoCo `0.8.12`** |

---

## Arquitectura del Proyecto
La aplicación sigue el patrón **MVVM (Model-View-ViewModel)** con inyección de dependencias manual centralizada en `AppContainer.kt`:

- **`ui/`**: Pantallas en Jetpack Compose (`Oportunidades`, `Organizaciones`, `Contactos`, `Seguimiento`, `Alertas`, `Eventos`, `Feriados`, `Login`) y sus respectivos `ViewModel`.
- **`data/local/`**: Base de datos **Room** (`AppDatabase`), entidades, DAOs, convertidores y datos semilla (`SeedData`).
- **`data/remote/`**: Cliente **Retrofit** (`ApiClient`, `OportunidadApi`, DTOs, mappers y `OportunidadRemoteDataSourceImpl`) conectado al microservicio **oportunidades-service** mediante `BuildConfig.API_BASE_URL`.
- **`data/remote/externo/`**: Cliente separado para la API pública de **Nager.Date**, usado por la pantalla de feriados.
- **`data/repository/`**: Repositorios que implementan estrategia de **caché local + sincronización remota** (la UI observa Room en tiempo real; las operaciones CRUD impactan el backend y refrescan la caché).
- **`data/validation/`**: Reglas de validación de formularios desacopladas para pruebas unitarias.

---

## Funcionalidades Implementadas
- **Interfaz moderna:** Jetpack Compose con Material Design 3, animaciones (`AnimatedVisibility`) e identidad visual institucional.
- **Autenticación y roles:** Inicio de sesión con persistencia de sesión (`SessionManager`) y control de permisos por rol (solo `SUPERVISOR`/`admin` o el creador pueden eliminar una oportunidad).
- **Integración API REST + Caché Offline:** Consumo de endpoints `GET`, `POST`, `PUT` y `DELETE` con Retrofit. Incluye protección de llaves foráneas (`mapearConFkSegura`) para evitar fallos en Room si un ID remoto no existe localmente.
- **Manejo de estados de red:** Indicadores de carga, captura de errores y opción de reintento manteniendo visibles los datos locales en caso de caída de conexión.
- **Formularios validados:** Validaciones en tiempo real con retroalimentación visual.
- **Feriados para planificación:** Pantalla **Feriados (planificación de activaciones)** que consume Nager.Date para Chile (`CL`), filtra fechas pasadas y consulta el año siguiente cuando quedan menos de tres feriados futuros.
- **Recursos nativos:** Acceso a cámara para captura de evidencias (`FileProvider`), geolocalización GPS y recordatorios mediante notificaciones locales.

---

## Backend y Servicios Externos

### `oportunidades-service`
Microservicio Spring Boot ubicado en `microservicios/oportunidades-service`, Java 17, puerto `8080` y documentación Swagger en `http://localhost:8080/swagger-ui.html`.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/oportunidades` | Lista todas las oportunidades |
| `GET` | `/api/oportunidades/{id}` | Obtiene una oportunidad por ID |
| `POST` | `/api/oportunidades` | Crea una oportunidad |
| `PUT` | `/api/oportunidades/{id}` | Actualiza una oportunidad |
| `DELETE` | `/api/oportunidades/{id}` | Elimina una oportunidad |

La app Android usa `http://10.0.2.2:8080/` como `API_BASE_URL` para el emulador. En un dispositivo físico, reemplazar `10.0.2.2` por la IP de la máquina que ejecuta el microservicio.

### Nager.Date
La pantalla de feriados consume:

```text
GET https://date.nager.at/api/v3/PublicHolidays/{year}/CL
```

### `organizaciones-service`
Espacio reservado para documentar el servicio de organizaciones. La app ya contiene las pantallas de organizaciones, contactos y seguimientos, pero este README no define aún los contratos de ese servicio.

---

## Puesta en Marcha

### 1. Backend con H2 (recomendado para pruebas locales)
No requiere MySQL:

```powershell
cd microservicios\oportunidades-service
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

Consola H2: `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:oportunidades_db` · Usuario: `sa` · Contraseña: vacía.

### 2. Backend con MySQL
Requiere MySQL en `localhost:3306` y una base llamada `oportunidades_db` (la URL usa `createDatabaseIfNotExist=true`).

```powershell
cd microservicios\oportunidades-service
$env:DB_USERNAME="root"
$env:DB_PASSWORD=""
.\mvnw.cmd spring-boot:run
```

Si se usa otro usuario o contraseña, ajustar las variables de entorno `DB_USERNAME` y `DB_PASSWORD`.

### 3. App Android
1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle y esperar a que KSP genere las clases de Room.
3. Ejecutar la app en un emulador con API 24 o superior.
4. Verificar que el microservicio esté corriendo antes de usar oportunidades.

Cuando la app apunta a `http://127.0.0.1:8080/`, redirigir el puerto del equipo al dispositivo antes de abrirla:

```powershell
adb reverse tcp:8080 tcp:8080
```

### 4. Credenciales de prueba
| Usuario | Contraseña |
| :--- | :--- |
| `cristopher` | `cris123` |
| `vicente` | `vicente123` |

Todas las fechas de la app se muestran en formato `dd/MM/yyyy`.

---

## Pruebas Unitarias y Cobertura (JaCoCo)
El proyecto cuenta con **64 pruebas unitarias** que cubren mappers, cliente remoto (`MockWebServer`), repositorio con caché, `ViewModel` (`MockK` + `MainDispatcherRule`), validaciones, feriados y alertas. Además, existe una prueba instrumentada de migración (`DatabaseMigrationTest`).

Para ejecutar los tests y generar el reporte de cobertura:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:jacocoTestReport
```

El reporte HTML queda en `app/build/reports/jacoco/jacocoTestReport/html/index.html`.

Cobertura reportada en la última ejecución:

| Ámbito | Instrucciones | Líneas |
| :--- | :--- | :--- |
| Global del módulo `app` | 28.8% | 26.2% |
| `ui/oportunidades` | 86.8% | 81.7% |
| Cliente remoto de oportunidades | 92.0% | 91.3% |
| Repositorio de oportunidades | 88.7% | 86.1% |
| Validaciones de oportunidades | 100% | 100% |

## APK firmado y llave

APK release firmado: [`apk/proyecto-app-movil-release.apk`](apk/proyecto-app-movil-release.apk)
Llave de firma: `app/practica-release.jks` · Detalle del proceso: [docs/APK_FIRMADO.md](docs/APK_FIRMADO.md)

![APK firmado](docs/img/imagen%20apk.png)

![Llave .jks](docs/img/imagen%20practica-release.jks%20y%20mas.png)
