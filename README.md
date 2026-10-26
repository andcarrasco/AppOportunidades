# AppOportunidades

Aplicación Android para registrar oportunidades de eventos y activaciones corporativas y llevar un seguimiento de lo que se ha hecho en cada una. Corresponde al trabajo de la Evaluación Parcial 2 de Desarrollo de Aplicaciones Móviles, DSY1105.

Integrantes: Andrés Carrasco y Pablo Rojas.

Toda la información usada para la demostración es ficticia. Los datos se guardan en el teléfono y no se comparten entre dispositivos.

## Qué se puede hacer

- Registrar y editar organizaciones y sus contactos.
- Crear oportunidades con los datos del evento, responsable, prioridad y estado.
- Buscar oportunidades por nombre y filtrarlas por fecha, tipo, estado, organización, ubicación, prioridad o responsable.
- Registrar gestiones, propuestas y próximas acciones, y consultar el historial de cada oportunidad.
- Ver avisos de eventos próximos, contactos pendientes, propuestas sin respuesta y seguimientos atrasados.
- Registrar el resultado de una activación con cantidades ficticias de asistentes, contactos e inscripciones.
- Tomar fotografías desde el detalle de una oportunidad y conservarlas como evidencias en el teléfono.
- Usar la cámara o elegir una imagen de la galería para el perfil.
- Activar o desactivar los avisos desde el perfil y conservar esa preferencia.

## Cómo ejecutar la app

1. Abrir la carpeta del proyecto en Android Studio Quail 4.
2. Sincronizar Gradle usando el JDK incluido en Android Studio. Instalar el SDK 37 si se solicita.
3. Conectar un teléfono o iniciar un emulador con Android 13 (API 33) o superior.
4. Seleccionar el módulo `app` y presionar Run.
5. Ingresar con estos datos de prueba:

   - Correo: `ejecutivo@example.com`
   - Contraseña: `Demo2026`

El acceso es local, con una cuenta fija. Al cerrar el proceso de la aplicación hay que ingresar nuevamente.

## Primer recorrido

Desde Oportunidades, entrar a Organizaciones y contactos y registrar una organización. Después se puede agregar un contacto y crear una oportunidad asociada a esa organización.

La ficha reúne la oportunidad y los datos del evento. Nombre, tipo, descripción, fuente, prioridad, potencial y estado son obligatorios; el potencial debe ser Bajo, Medio o Alto. Desde el detalle se puede editar, agregar una gestión, revisar el historial o registrar el resultado de la activación. Los cambios de estado también quedan en el historial. En el detalle, Fotografiar evidencia permite tomar una foto de prueba y verla asociada a esa oportunidad.

Para registrar una propuesta, agregar una gestión de tipo Envío de propuesta y cambiar el estado de la oportunidad a Propuesta enviada. La app solo deja constancia de esa gestión; no envía correos ni mensajes.

Las fechas se escriben como `AAAA-MM-DD`, las horas como `HH:mm` y los costos con punto decimal. Para probar cámara y galería, usar una imagen de un objeto o un emblema sin personas identificables.

## Organización del código

En los formularios, la pantalla muestra los datos y el ViewModel maneja el estado y valida. En oportunidades, el Repository coordina el acceso a los DAO y el guardado conjunto de la ficha y su historial en Room. Las evidencias tienen su propio Repository para los archivos de imagen.

- `model`: estados de los formularios, errores, catálogos y fechas.
- `model/data/local`: entidades, DAO y base Room.
- `model/data/repository`: acceso a los datos de oportunidades y archivos de evidencias.
- `viewmodel`: validaciones, guardado, filtros y avisos.
- `navigation`: rutas y conexión entre pantallas.
- `ui`: pantallas, componentes compartidos, tema y tamaños de ventana.
- `data`: preferencia de avisos con DataStore.

## Qué falta revisar

Hay pruebas de validación y de selección de la última gestión en `app/src/test`. La versión actual se revisó de forma estática, pero todavía falta compilarla, ejecutar las pruebas y comprobar el recorrido completo en un teléfono, incluidos permisos, captura y cancelación de imágenes.

También hay que comprobar que los registros sigan disponibles después de cerrar y abrir la app, y que se conserve la preferencia de avisos. La foto del perfil usa almacenamiento temporal. Las fotografías tomadas desde el detalle de una oportunidad se guardan en archivos internos asociados a su identificador; se conservan al reiniciar la app y se eliminan al borrar sus datos o desinstalarla.

El historial se consulta por oportunidad. El contacto de una gestión se puede indicar en sus observaciones; no hay un historial separado por contacto. El público esperado y el objetivo se describen en un mismo campo.

Para la entrega quedan por completar los accesos del docente. GitHub y AVA deben contener la misma version del proyecto.

## Planificacion

Tablero de Trello (publico): https://trello.com/b/ddjxbRZd/dsy1105-parcial-2-appoportunidades

Equipo N°: 2
