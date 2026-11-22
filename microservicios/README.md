# Microservicios de AppOportunidades

Dos microservicios Spring Boot (Java 21, Maven) que consume la app Android con Retrofit. Todos los datos son ficticios.

| Servicio | Puerto | Que hace |
|---|---|---|
| `ms-usuarios` | 8081 | Usuarios de la app y login (correo + clave) |
| `ms-oportunidades` | 8082 | CRUD de oportunidades de eventos corporativos |

Cada servicio usa una base H2 en archivo (carpeta `data/` dentro del servicio, ignorada por git) y carga datos de ejemplo la primera vez que arranca.

## Como ejecutar

Requisitos: Java 21 y Maven (o IntelliJ / VS Code con soporte Maven).

Desde la carpeta de cada servicio:

```
cd ms-usuarios
mvnw spring-boot:run
```

```
cd ms-oportunidades
mvnw spring-boot:run
```

Cada servicio incluye Maven Wrapper (`mvnw` / `mvnw.cmd`), así que no es necesario instalar Maven: la primera vez descarga Maven 3.9.9 automáticamente. Se necesita Java 21 o superior. En PowerShell se escribe `.\mvnw spring-boot:run`.

También se puede abrir la carpeta en IntelliJ y ejecutar la clase principal (`MsUsuariosApplication` o `MsOportunidadesApplication`).

Para correr los tests: `mvnw test`.

## Base de datos H2

| Servicio | Consola H2 | JDBC URL |
|---|---|---|
| ms-usuarios | http://localhost:8081/h2-console | `jdbc:h2:file:./data/usuariosdb;AUTO_SERVER=TRUE` |
| ms-oportunidades | http://localhost:8082/h2-console | `jdbc:h2:file:./data/oportunidadesdb;AUTO_SERVER=TRUE` |

Usuario `sa`, clave vacia. Para reiniciar los datos, detener el servicio y borrar la carpeta `data/`.

## Conexion desde la app Android

- Emulador Android: usar el host `10.0.2.2` (`http://10.0.2.2:8081/` y `http://10.0.2.2:8082/`).
- Telefono fisico: usar la IP local del PC (por ejemplo `http://192.168.1.50:8081/`), con el telefono y el PC en la misma red Wi-Fi. Los servicios escuchan en `0.0.0.0`; puede ser necesario permitir los puertos 8081 y 8082 en el firewall de Windows.
- Como las URL son `http` (no `https`), la app debe permitir trafico de texto plano (`android:usesCleartextTraffic="true"`).

## Usuarios de demostracion

| Nombre | Correo | Clave | Perfil | Area | Zona |
|---|---|---|---|---|---|
| Carla Demo | ejecutivo@example.com | Demo2026 | Ejecutivo comercial | Comercial | Santiago |
| Rodrigo Demo | supervisor@example.com | Demo2026 | Supervisor comercial | Comercial | Santiago |
| Valentina Demo | activaciones@example.com | Demo2026 | Gestión de activaciones | Activaciones | Santiago |

## Endpoints de ms-usuarios (puerto 8081)

| Metodo | Ruta | Descripcion | Respuestas |
|---|---|---|---|
| POST | `/api/usuarios/login` | Login | 200 usuario, 401 credenciales incorrectas, 403 usuario inactivo, 400 datos invalidos |
| GET | `/api/usuarios` | Lista usuarios | 200 |
| GET | `/api/usuarios/{id}` | Detalle | 200, 404 |
| POST | `/api/usuarios` | Crea usuario | 201, 400, 409 correo repetido |
| PUT | `/api/usuarios/{id}` | Actualiza (si no se envia `clave`, se mantiene la actual) | 200, 400, 404, 409 |
| DELETE | `/api/usuarios/{id}` | Elimina | 204, 404 |

La `clave` solo se recibe, nunca se devuelve en las respuestas.

Login, cuerpo:

```json
{ "correo": "ejecutivo@example.com", "clave": "Demo2026" }
```

Login, respuesta 200:

```json
{
  "id": 1,
  "nombre": "Carla Demo",
  "correo": "ejecutivo@example.com",
  "perfil": "Ejecutivo comercial",
  "area": "Comercial",
  "zona": "Santiago",
  "activo": true,
  "fechaCreacion": "2026-01-15"
}
```

Login, respuesta 401:

```json
{ "mensaje": "Correo o clave incorrectos" }
```

Crear usuario (POST), cuerpo:

```json
{
  "nombre": "Usuario Nuevo",
  "correo": "nuevo@example.com",
  "clave": "Demo2026",
  "perfil": "Ejecutivo comercial",
  "area": "Comercial",
  "zona": "Santiago",
  "activo": true
}
```

Si no se envia `fechaCreacion`, se asigna la fecha de hoy (`yyyy-MM-dd`).

## Endpoints de ms-oportunidades (puerto 8082)

| Metodo | Ruta | Descripcion | Respuestas |
|---|---|---|---|
| GET | `/api/oportunidades` | Lista ordenada por id. Filtros opcionales `?estado=...&responsable=...` | 200 |
| GET | `/api/oportunidades/{id}` | Detalle | 200, 404 |
| POST | `/api/oportunidades` | Crea (si `actualizacion` viene vacia se usa la fecha de hoy) | 201, 400 |
| PUT | `/api/oportunidades/{id}` | Actualiza todo y fija `actualizacion` en hoy | 200, 400, 404 |
| PATCH | `/api/oportunidades/{id}/estado` | Cambia solo el estado y la `actualizacion` | 200, 400, 404 |
| DELETE | `/api/oportunidades/{id}` | Elimina | 204, 404 |

Obligatorios: `nombre`, `tipo`, `estado`. Los demas campos son texto y por defecto son `""`.

Ejemplo de cuerpo (POST / PUT) y respuesta:

```json
{
  "id": 1,
  "nombre": "Feria de beneficios Municipalidad Demo",
  "tipo": "Actividad municipal",
  "descripcion": "Feria comunal con stands de servicios y beneficios para vecinos.",
  "fuente": "Sitio web municipal",
  "fechaDeteccion": "2026-11-02",
  "responsable": "Carla Demo",
  "prioridad": "Alta",
  "potencial": "Alto",
  "estado": "Evento identificado",
  "organizacionId": "1",
  "fechaInicio": "2026-12-10",
  "fechaTermino": "2026-12-11",
  "horario": "10:00 a 18:00",
  "comuna": "Puente Alto",
  "region": "Metropolitana",
  "ubicacion": "Plaza de Armas Demo",
  "publico": "Vecinos y familias",
  "asistentes": "3000",
  "condiciones": "Stand de 3x3 metros, sujeto a aprobacion municipal.",
  "requisitos": "Solicitud formal y permiso de instalacion.",
  "costos": "Por definir",
  "espacios": "Stands al aire libre",
  "observaciones": "Datos ficticios para demostracion.",
  "actualizacion": "2026-11-02"
}
```

Cambio de estado (PATCH), cuerpo:

```json
{ "estado": "Contacto pendiente" }
```

## Errores

Los errores devuelven un JSON simple:

```json
{ "mensaje": "El campo nombre es obligatorio" }
```

Codigos usados: 400 validacion, 401 credenciales, 403 usuario inactivo, 404 no encontrado, 409 correo repetido.
