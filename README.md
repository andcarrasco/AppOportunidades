# AppOportunidades

App Android para registrar y hacer seguimiento a oportunidades de eventos y activaciones corporativas. Está basada en el caso 003V (Gestión móvil de oportunidades y activaciones corporativas) y es nuestra entrega de la Evaluación Parcial 4 de DSY1105, Desarrollo de Aplicaciones Móviles. Partimos desde lo que entregamos en la Parcial 2 y le fuimos agregando lo que pedía esta evaluación.

## 1. Nombre del proyecto

AppOportunidades. La app está hecha en Kotlin con Jetpack Compose y Material 3, usando MVVM como en las guías del curso. El backend son dos microservicios en Spring Boot hechos por nosotros, y además la app consume una API externa de feriados.

## 2. Integrantes

- Andrés Carrasco
- Pablo Rojas

Todos los datos que aparecen en la app (usuarios, organizaciones, contactos, costos, resultados) son inventados para la demo.

## 3. Funcionalidades

### Login con tres perfiles

El login se valida contra el microservicio de usuarios. Según el perfil con que entres cambian el menú y lo que puedes hacer, siguiendo el diagrama de flujo del caso.

| Perfil | Correo | Clave | Qué hace |
|---|---|---|---|
| Ejecutivo comercial | `ejecutivo@example.com` | `Demo2026` | Registra oportunidades (evento, organización, contacto, detalle y fecha), puede escanear un QR para registrar una oportunidad de inmediato y anota las gestiones y propuestas enviadas. |
| Supervisor comercial | `supervisor@example.com` | `Demo2026` | Ve la lista de oportunidades con búsqueda y filtros, revisa el detalle y el seguimiento, cambia el estado (Pendiente, En gestión, Confirmada, Descartada), asigna responsable, cambia la prioridad y puede eliminar. |
| Gestión de activaciones | `activaciones@example.com` | `Demo2026` | Coordina la activación: confirma la participación, anota las condiciones, asigna responsable y sube fotos de evidencia. Al final registra el resultado, las observaciones y las próximas acciones. |

Para no tener que escribir los correos, en el login hay un botón por cada cuenta de prueba. Para salir o cambiar de perfil está el ícono de salida arriba en Inicio, o el botón "Cerrar sesión / Cambiar perfil" en Perfil. Los dos te devuelven al login.

Si el microservicio de usuarios está apagado igual se puede entrar con las cuentas de prueba, y la app avisa que el ingreso fue sin conexión.

### Otras cosas que tiene

- Pestaña Pendientes con avisos de eventos que se acercan, contactos pendientes, propuestas sin respuesta y seguimientos atrasados.
- Historial por oportunidad, donde quedan también los cambios de estado.
- Organizaciones y contactos de ejemplo.
- Lector de códigos QR con CameraX y ML Kit.
- Pantalla de feriados de Chile (API externa). Si un evento cae en feriado, la app lo avisa.
- La preferencia de avisos se guarda con DataStore, y la foto de perfil se puede sacar con la cámara o elegir de la galería.

### Dónde se guardan los datos

Las oportunidades viven en el microservicio `ms-oportunidades`, que tiene el CRUD completo: crear, consultar, editar, cambiar estado y eliminar. Lo que responde el servidor también se copia en Room, así que si se cae la conexión la app sigue mostrando lo último que descargó.

Organizaciones, contactos, gestiones, activaciones y resultados se guardan solo en Room, igual que en el diagrama del caso.

Los feriados (la API externa) tienen su propia pantalla y no se mezclan con nuestros datos.

### Códigos QR de prueba

En la carpeta `qr/` dejamos 3 códigos, cada uno con una oportunidad inventada. Al lado de cada imagen hay un `.txt` con el mismo contenido por si se quiere revisar.

- `qr_1_feria_empresarial.png`
- `qr_2_jornada_beneficios.png`
- `qr_3_actividad_municipal.png`

Para probarlos hay que entrar como ejecutivo comercial, tocar "Escanear QR de oportunidad", apuntar al código y tocar "Registrar oportunidad". Si la organización del QR no existe la app la crea, después guarda la oportunidad en el microservicio y muestra un resumen. Si prefieres revisar los datos antes de guardar, usa "Revisar en el formulario".

## 4. Endpoints utilizados

### Microservicios propios (carpeta `microservicios/`)

Desde el emulador la app llega al computador por `http://10.0.2.2`.

ms-usuarios, puerto 8081, base de datos H2:

| Método | Endpoint | Para qué se usa |
|---|---|---|
| POST | `/api/usuarios/login` | Login, devuelve el perfil |
| GET | `/api/usuarios` | Listar usuarios |
| GET | `/api/usuarios/{id}` | Ver un usuario |
| POST | `/api/usuarios` | Crear usuario |
| PUT | `/api/usuarios/{id}` | Editar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

ms-oportunidades, puerto 8082, base de datos H2:

| Método | Endpoint | Para qué se usa |
|---|---|---|
| GET | `/api/oportunidades` | Lista de oportunidades (botón "Actualizar desde el servidor") |
| GET | `/api/oportunidades/{id}` | Ver una oportunidad |
| POST | `/api/oportunidades` | Registrar oportunidad, desde el formulario o desde un QR |
| PUT | `/api/oportunidades/{id}` | Editar la oportunidad y guardar la decisión del supervisor |
| PATCH | `/api/oportunidades/{id}/estado` | Confirmar participación en Coordinar activación |
| DELETE | `/api/oportunidades/{id}` | Eliminar oportunidad (solo supervisor) |

La consola H2 de cada servicio está en `http://localhost:8081/h2-console` y `http://localhost:8082/h2-console`. En `microservicios/README.md` está el detalle de cada endpoint con ejemplos de JSON.

### API externa

| Método | Endpoint | Para qué se usa |
|---|---|---|
| GET | `https://date.nager.at/api/v3/PublicHolidays/{año}/CL` | Pantalla "Feriados de Chile", con el año actual y el siguiente |

## 5. Pasos para ejecutar

### Microservicios

Se necesita Java 21 o más nuevo. Cada servicio va en su propia terminal.

```
cd microservicios/ms-usuarios
mvnw spring-boot:run
```

```
cd microservicios/ms-oportunidades
mvnw spring-boot:run
```

En PowerShell hay que escribir `.\mvnw spring-boot:run`. La primera vez se demora un poco más porque descarga Maven. Para saber si quedaron arriba, abre http://localhost:8081/api/usuarios y http://localhost:8082/api/oportunidades en el navegador; si sale un JSON, están funcionando.

### App móvil

1. Abrir la carpeta del proyecto en Android Studio.
2. En Settings > Build Tools > Gradle, dejar el Gradle JDK en 21.
3. Sincronizar Gradle. Si pide instalar el SDK 37, aceptar.
4. Levantar un emulador con Android 13 (API 33) o superior. Nosotros probamos con un Pixel 7 (API 34) y un Pixel 5 con Android 17.
5. Elegir el módulo `app` y darle a Run.
6. En Android 17 o superior la app pide permiso de dispositivos cercanos / red local al abrirse. Hay que aceptarlo, porque sin ese permiso Android no la deja conectarse a los microservicios y entra en modo sin conexión.
7. Entrar con alguna de las cuentas de prueba.

Para probar en un teléfono físico hay que cambiar `HOST` en `remote/RetrofitInstance.kt` por la IP del computador (por ejemplo `http://192.168.1.10`), con el teléfono y el PC en la misma red Wi-Fi. También hay que abrir los puertos 8081 y 8082 en el firewall de Windows.

### Pruebas unitarias

Las pruebas de la lógica están en `app/src/test/java`. Usamos Kotest, JUnit 5, MockK y kotlinx-coroutines-test, y cubren los ViewModel y los Repository. En `app/src/androidTest/java` hay además una prueba de interfaz con Compose UI Test y MockK.

Para correrlas en Android Studio: clic derecho en la carpeta de pruebas y "Run 'Tests in ...'". Si se quiere ver la cobertura, la opción es "Run with Coverage".

Los microservicios tienen sus propias pruebas de la capa de servicio con JUnit 5 y Mockito. Se corren con `mvnw test` dentro de la carpeta de cada uno.

## 6. APK firmado y llave

La llave está en `keystore/oportunidades.jks`, con alias `oportunidades`. La clave del almacén y de la llave es `Oportunidades2026` (la dejamos acá porque es un proyecto académico).

La configuración de firma está en `app/build.gradle.kts`, dentro de `signingConfigs` → `release`.

Para generar el APK usamos Build > Generate Signed App Bundle or APK > APK, con esa misma llave. También sirve `gradlew assembleRelease`. El APK queda en `app/build/outputs/apk/release/app-release.apk` y dejamos una copia en `apk/app-release.apk`.

Capturas:

![APK firmado](docs/capturas/apk_firmado.png)
![Llave .jks](docs/capturas/llave_jks.png)

## Cómo está ordenado el código

Seguimos la misma estructura MVVM de las guías del curso:

| Carpeta | Qué hay |
|---|---|
| `model` | Estados de los formularios (UiState), errores, catálogos, fechas y los modelos que vienen de las APIs (Usuario, Feriado) |
| `model/data/local` | Entidades, DAO y la base de datos Room |
| `remote` | `RetrofitInstance` y las interfaces de cada servicio (usuarios, oportunidades, feriados) |
| `repository` | Un repositorio por cada fuente de datos. Los ViewModel los reciben por constructor, así en las pruebas los podemos reemplazar por mocks. |
| `viewmodel` | Estado de cada pantalla, validaciones (`validarFormulario`), guardado y eventos de navegación |
| `navigation` | Rutas y qué pantallas ve cada perfil |
| `ui` | Pantallas, componentes, tema y el lector QR |

## Planificación

Tablero de Trello (público): https://trello.com/b/ddjxbRZd/dsy1105-parcial-2-appoportunidades
