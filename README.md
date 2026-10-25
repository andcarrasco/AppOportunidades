# AppOportunidades

Proyecto de Desarrollo de Aplicaciones Móviles, DSY1105, para la Evaluación Parcial 2.

Integrantes: Andrés Carrasco y Pablo Rojas.

La aplicación permite registrar oportunidades de eventos y activaciones, relacionarlas con organizaciones y llevar un seguimiento de las gestiones. Trabaja con datos ficticios y guarda la información en el mismo dispositivo.

## Cómo ejecutar el proyecto

1. Abrir la carpeta del proyecto en Android Studio Quail 4.
2. Esperar la sincronización de Gradle y usar el JDK integrado de Android Studio. Instalar Android SDK 37 si el programa lo solicita.
3. Conectar un teléfono o iniciar un emulador con Android 13 (API 33) o superior.
4. Seleccionar el módulo `app` y presionar Run.
5. Ingresar con el correo `ejecutivo@example.com` y la contraseña `Demo2026`.

La cuenta es de demostración. No se conecta a un servicio de autenticación y la sesión termina al cerrar el proceso de la aplicación.

## Recorrido de la aplicación

Primero se registra una organización desde Oportunidades, en la opción Organizaciones y contactos. Después se pueden agregar contactos ficticios y crear una oportunidad asociada a esa organización.

La ficha de la oportunidad contiene también los datos del evento: fechas, horario, ubicación, público y condiciones de participación. Desde su detalle se puede editar, registrar una gestión, consultar el historial o ingresar el resultado de una activación.

Para dejar constancia de una propuesta, se registra una gestión de tipo Envío de propuesta y se cambia el estado de la oportunidad a Propuesta enviada. No se envían correos ni mensajes reales.

Las fechas se escriben como `AAAA-MM-DD`, las horas como `HH:mm` y los costos con punto decimal.

## Funciones implementadas

- Inicio y cierre de sesión con una cuenta ficticia.
- Registro, consulta y edición de organizaciones y contactos.
- Registro y edición de oportunidades con prioridad, responsable y estado.
- Búsqueda por nombre y filtros por fecha, tipo, estado, organización, comuna, región, prioridad y responsable.
- Seguimientos con fecha, hora, resultado y próxima acción.
- Historial de gestiones y cambios de estado por oportunidad.
- Avisos internos de eventos próximos, contactos pendientes, propuestas sin respuesta y seguimientos pendientes o atrasados.
- Registro de resultados ficticios de una activación.
- Perfil con cámara y selección de imágenes desde la galería.
- Preferencia para activar o desactivar los avisos, guardada con DataStore.
- Mensajes de error por campo, confirmaciones de guardado y animaciones de estado.

## Organización del código

Los formularios siguen el trabajo de la Guía 11: cada uno tiene un estado, un modelo de errores y un ViewModel. La pantalla muestra los datos y llama a las funciones `onCampoChange`. El ViewModel valida y guarda.

- `model`: estados, errores, catálogos y manejo de fechas.
- `model/data/local`: entidades, DAO y base de datos Room.
- `viewmodel`: validaciones, estado de los formularios y lógica de consulta.
- `navigation`: rutas, eventos y conexión entre pantallas, siguiendo las Guías 10 y 11.
- `ui/screen`: pantallas con Scaffold y Column.
- `ui/components`: campos, selectores, botones y confirmaciones reutilizables.
- `ui/utils`: tamaños de ventana de la Guía 9.
- `data`: preferencia de avisos de la Guía 12.

Room se basa en los archivos de persistencia entregados. Los listados y las consultas de edición se adaptaron al caso. Cámara y galería corresponden a la actividad de la Guía 13. Se mantiene la configuración del proyecto entregado y no se agregaron bibliotecas en este ajuste.

## Comprobaciones antes de entregar

1. Intentar guardar formularios vacíos y corregir los campos indicados.
2. Registrar una organización, un contacto y una oportunidad. Editar el estado y consultar el historial.
3. Registrar un seguimiento reciente y luego uno con fecha anterior. El aviso debe considerar el seguimiento más reciente por fecha y hora.
4. Cerrar y volver a abrir la aplicación. Ingresar nuevamente y comprobar que los registros siguen disponibles.
5. Desactivar los avisos y volver a abrir para comprobar que se conserva la preferencia.
6. En un teléfono, probar cámara y galería, cancelar la selección y denegar el permiso de cámara. Usar imágenes de objetos o emblemas sin personas identificables.
7. Revisar los formularios con el teclado abierto y comprobar que se puede llegar al botón Guardar.

Las pruebas unitarias de validación y selección de la última gestión están en `app/src/test`. Este ajuste se revisó de forma estática; no se ha compilado ni se han ejecutado esas pruebas. También falta comprobar el recorrido completo, la cámara y la galería en un teléfono.

## Alcance y pendientes

Los datos comerciales se guardan en Room. La imagen del perfil se mantiene durante la sesión y los cambios de orientación; la captura usa un archivo temporal y no se garantiza que la imagen se conserve al cerrar la aplicación.

El público esperado y el objetivo se describen en un mismo campo. El contacto de una gestión puede anotarse en las observaciones; el historial se consulta por oportunidad y no hay un historial separado por contacto.

Los ejemplos se adaptaron para trabajar con los datos del caso. Los listados y la edición usan consultas sencillas de Room; las fechas se validan en una clase común y la imagen se muestra desde el Uri del perfil. Estas partes no son copias literales de un proyecto completo del profesor.

El guardado de una oportunidad está dividido en pasos: validar, preparar los datos y guardar la ficha con su cambio de estado. Room mantiene esas dos operaciones juntas para que un error no deje el historial incompleto.

Antes de entregar falta completar el número de equipo, adjuntar la planificación real de Trello o equivalente y comprobar los accesos del docente. La misma versión debe quedar en GitHub y en AVA, dentro del plazo indicado en la pauta.
