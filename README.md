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

La ficha reúne la oportunidad y los datos del evento. Desde el detalle se puede editar, agregar una gestión, revisar el historial o registrar el resultado de la activación. Los cambios de estado también quedan en el historial.

Para registrar una propuesta, agregar una gestión de tipo Envío de propuesta y cambiar el estado de la oportunidad a Propuesta enviada. La app solo deja constancia de esa gestión; no envía correos ni mensajes.

Las fechas se escriben como `AAAA-MM-DD`, las horas como `HH:mm` y los costos con punto decimal. Para probar cámara y galería, usar una imagen de un objeto o un emblema sin personas identificables.

## Organización del código

Los formularios siguen la estructura trabajada en clases: la pantalla muestra los datos, el ViewModel maneja el estado y valida, y el DAO realiza las operaciones de la base de datos.

- `model`: estados de los formularios, errores, catálogos y fechas.
- `model/data/local`: entidades, DAO y base Room.
- `viewmodel`: validaciones, guardado, filtros y avisos.
- `navigation`: rutas y conexión entre pantallas.
- `ui`: pantallas, componentes compartidos, tema y tamaños de ventana.
- `data`: preferencia de avisos con DataStore.

Se tomaron como base los formularios y la navegación de las Guías 10 y 11, la persistencia con Room, los estados y animaciones de la Guía 12 y la actividad de cámara y galería de la Guía 13. Los campos y las consultas se adaptaron al caso de oportunidades.

## Qué falta revisar

Hay pruebas de validación y de selección de la última gestión en `app/src/test`. La versión actual se revisó de forma estática, pero todavía falta compilarla, ejecutar las pruebas y comprobar el recorrido completo en un teléfono, incluidos permisos, captura y cancelación de imágenes.

También hay que comprobar que los registros sigan disponibles después de cerrar y abrir la app, y que se conserve la preferencia de avisos. La foto del perfil usa almacenamiento temporal y no tiene la misma persistencia que los datos guardados en Room.

El historial se consulta por oportunidad. El contacto de una gestión se puede indicar en sus observaciones; no hay un historial separado por contacto. El público esperado y el objetivo se describen en un mismo campo.

Para la entrega quedan por completar el número de equipo, las evidencias de Trello o una herramienta equivalente y los accesos del docente. GitHub y AVA deben contener la misma versión del proyecto.
