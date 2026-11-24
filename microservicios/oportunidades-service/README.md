# oportunidades-service

Microservicio Spring Boot 3.3.5 / Java 17 que expone el CRUD de oportunidades usado por la app Android.

## Ejecutar

MySQL local (base `oportunidades_db`, usuario `root` por defecto):

```bash
./mvnw spring-boot:run
```

Variables opcionales: `DB_USERNAME` y `DB_PASSWORD`.

### Perfil Neon (PostgreSQL)

`application-neon.properties` lee estas variables de entorno sin valores por defecto:

| Variable | Uso |
|---|---|
| `DB_URL` | URL JDBC de Neon, por ejemplo `jdbc:postgresql://HOST/DB?sslmode=require` |
| `DB_USERNAME` | Usuario de la base de datos Neon |
| `DB_PASSWORD` | Contraseña del usuario Neon |

En PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://HOST/DB?sslmode=require"
$env:DB_USERNAME="USUARIO"
$env:DB_PASSWORD="CLAVE"
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=neon
```

No guardar los valores reales en el repositorio.

Perfil H2 en memoria:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

Puerto: `8080`. Swagger UI: `http://localhost:8080/swagger-ui.html`.

## Endpoints

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/oportunidades` | Lista todas las oportunidades |
| GET | `/api/oportunidades/{id}` | Obtiene una oportunidad |
| POST | `/api/oportunidades` | Crea una oportunidad (ignora `id` entrante) |
| PUT | `/api/oportunidades/{id}` | Actualiza una oportunidad |
| DELETE | `/api/oportunidades/{id}` | Elimina una oportunidad |

Las fechas `fechaDeteccion` y `fechaActualizacion` son `Long` con milisegundos desde epoch. Los campos `tipo`, `prioridad` y `estado` usan los nombres exactos de los enums de la app Android.

## Errores

Respuesta de error:

```json
{
  "status": 400,
  "error": "Bad Request",
  "mensajes": ["El nombre es obligatorio"]
}
```

- `400`: validación fallida o cuerpo inválido.
- `404`: no existe la oportunidad solicitada.

## Datos iniciales

Si la tabla está vacía se insertan 4 oportunidades con ids `1` a `4` (mismos datos de `SeedData.kt` de la app). Después se reinicia la identidad para que el siguiente POST genere `id = 5`:

- MySQL: `ALTER TABLE oportunidades AUTO_INCREMENT = 5`.
- H2: `ALTER TABLE oportunidades ALTER COLUMN id RESTART WITH 5`.

## Pruebas

```bash
./mvnw test
```

Incluye pruebas MockMvc de los 5 endpoints (400/404 e id generado) y una prueba de integración con H2 que verifica el seed y que el primer POST genera `id = 5`.

## Prueba punta a punta con el emulador

1. Iniciar el servicio con MySQL en `8080`.
2. Configurar la app Android con `http://10.0.2.2:8080/`.
3. Iniciar sesión con `cristopher` / `cris123`.
4. Crear una oportunidad desde la app y verificar que aparece con `id = 5`.
5. Ejecutar en MySQL:

```sql
SELECT id, nombre, estado, fechaActualizacion FROM oportunidades ORDER BY id;
```
