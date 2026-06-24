# MiTiempo360 - Resumen, palabras clave y Fase 4

## Resumen del proyecto

MiTiempo360 es una aplicación web orientada al registro, seguimiento y análisis del tiempo invertido en tareas diarias, dirigida a usuarios que necesitan organizar mejor sus actividades sin depender de herramientas empresariales complejas. El problema identificado se relaciona con la pérdida de control sobre el tiempo, la multitarea, la fatiga mental y la dificultad para reconocer en qué actividades se invierte la jornada. Aunque existen plataformas como Jira o ClickUp, muchas de ellas están enfocadas en equipos empresariales y presentan exceso de funcionalidades, lo que puede dificultar su adopción por parte de usuarios individuales.

El proyecto se apoya en conceptos de gestión del tiempo, productividad, técnica Pomodoro, sistemas web, bases de datos relacionales, dashboards y diagramas de Gantt. Desde el enfoque técnico, la solución se construye bajo una arquitectura cliente-servidor, utilizando Angular para el frontend, Java con Spring Boot para el backend y PostgreSQL como sistema de gestión de base de datos. Esta combinación permite crear una aplicación modular, escalable y orientada a la consulta de información en tiempo real.

El objetivo general es diseñar, desarrollar e implementar un sistema web que permita registrar, procesar y analizar el tiempo dedicado a tareas, facilitando la administración de actividades y la toma de decisiones personales sobre productividad. La metodología utilizada es Scrum, organizada en fases de análisis, diseño, desarrollo, pruebas y despliegue, mediante sprints progresivos.

Como resultado esperado, se busca obtener una herramienta funcional que permita registrar usuarios, gestionar tareas, registrar horas, utilizar temporizadores tipo Pomodoro, visualizar tareas terminadas en calendario, consultar información mediante filtros, generar reportes y representar actividades mediante un diagrama de Gantt. Con esto, el usuario podrá tener mayor claridad sobre su tiempo y mejorar su organización personal.

## Palabras clave

Gestión del tiempo; productividad; aplicación web; técnica Pomodoro; tareas; seguimiento de actividades; dashboard; diagrama de Gantt; Angular; Java; Spring Boot; PostgreSQL.

## Fase 4. Pruebas y despliegue

La fase 4 corresponde al proceso de validación final del sistema MiTiempo360. En esta etapa se realizan pruebas manuales sobre las principales funcionalidades de la aplicación, con el fin de verificar que los módulos implementados cumplan los requerimientos funcionales y no funcionales definidos en fases anteriores. Las pruebas se ejecutan desde la interfaz web y permiten validar el comportamiento del sistema desde la perspectiva del usuario final.

### Objetivo de las pruebas

Comprobar que la aplicación MiTiempo360 permita registrar usuarios, iniciar sesión, gestionar tareas, registrar horas, utilizar el temporizador Pomodoro, consultar tareas terminadas, visualizar información en calendario, generar reportes y revisar la distribución de actividades mediante el diagrama de Gantt.

### Alcance de las pruebas

Las pruebas cubren los siguientes módulos del sistema:

- Registro e inicio de sesión de usuarios.
- Gestión de tareas.
- Registro de actividades y horas.
- Temporizador Pomodoro.
- Panel de tareas terminadas.
- Vista tipo calendario.
- Exportación de información a Excel.
- Diagrama de Gantt.
- Navegación general del dashboard.

### Formato de pruebas manuales

| Código | Módulo | Caso de prueba | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| CP-001 | Registro de usuario | Crear un nuevo usuario en el sistema | 1. Ingresar al login. 2. Seleccionar crear usuario. 3. Diligenciar los datos. 4. Guardar el registro. | El sistema registra el usuario y permite posteriormente iniciar sesión. | El usuario se registra correctamente. | Aprobado |
| CP-002 | Inicio de sesión | Validar acceso con credenciales correctas | 1. Ingresar usuario y contraseña. 2. Presionar iniciar sesión. | El sistema autentica al usuario y redirige al dashboard. | El usuario accede al panel principal. | Aprobado |
| CP-003 | Inicio de sesión | Validar acceso con credenciales incorrectas | 1. Ingresar datos no válidos. 2. Presionar iniciar sesión. | El sistema muestra mensaje de error y no permite el acceso. | Se muestra alerta de credenciales inválidas. | Aprobado |
| CP-004 | Gestión de tareas | Crear una nueva tarea | 1. Ir al panel de tareas. 2. Ingresar nombre, fecha de inicio y estado. 3. Presionar crear tarea. | La tarea queda registrada y aparece en el tablero. | La tarea se visualiza en el panel de tareas. | Aprobado |
| CP-005 | Gestión de tareas | Generar ID automático de tarea | 1. Crear una tarea sin escribir ID. 2. Guardar la tarea. | El sistema asigna un identificador secuencial automáticamente. | La tarea queda creada con ID generado por el sistema. | Aprobado |
| CP-006 | Gestión de tareas | Editar una tarea existente | 1. Seleccionar una tarea. 2. Presionar editar. 3. Modificar información. 4. Guardar cambios. | La información de la tarea se actualiza correctamente. | La tarea muestra los cambios realizados. | Aprobado |
| CP-007 | Gestión de tareas | Finalizar una tarea | 1. Seleccionar una tarea activa. 2. Presionar el botón finalizar. | La tarea cambia a estado finalizada y deja de mostrarse en el tablero principal. | La tarea pasa al panel de terminadas. | Aprobado |
| CP-008 | Tareas terminadas | Devolver una tarea al tablero | 1. Entrar al panel de tareas terminadas. 2. Seleccionar una tarea. 3. Presionar devolver a ejecución. | La tarea vuelve al tablero principal y cambia su estado a ejecución. | La tarea se muestra nuevamente en tareas activas. | Aprobado |
| CP-009 | Registro de horas | Registrar tiempo manual en una tarea | 1. Seleccionar una tarea. 2. Escribir descripción, horas y fecha. 3. Presionar registrar. | El sistema guarda la descripción, horas y fecha del registro. | El registro aparece asociado a la tarea. | Aprobado |
| CP-010 | Pomodoro | Ejecutar temporizador Pomodoro | 1. Configurar tiempo de trabajo y descanso. 2. Iniciar temporizador. 3. Esperar finalización del ciclo. | El sistema controla el tiempo y genera alerta visual o sonora al finalizar. | El temporizador finaliza y emite alerta. | Aprobado |
| CP-011 | Calendario | Visualizar tareas terminadas por fecha | 1. Entrar al panel de tareas terminadas. 2. Revisar el calendario mensual. | Las tareas finalizadas se ubican en el día correspondiente según fecha de finalización. | Las tareas se muestran organizadas por día. | Aprobado |
| CP-012 | Detalle de tarea terminada | Abrir detalle de una tarea terminada | 1. Hacer clic sobre una tarjeta del calendario. | Se muestra una ventana con descripción, horas registradas y fechas de registro. | El detalle se despliega correctamente. | Aprobado |
| CP-013 | Exportación | Descargar información en Excel | 1. Ingresar al panel de tareas terminadas. 2. Presionar descargar .xls. | El sistema genera un archivo compatible con Excel con los encabezados definidos. | Se descarga el archivo con la información pintada en pantalla. | Aprobado |
| CP-014 | Diagrama de Gantt | Consultar tareas en vista Gantt | 1. Ingresar al panel Gantt. 2. Aplicar filtros por tarea o estado. | El sistema muestra las tareas en una línea de tiempo filtrable. | El panel permite visualizar y filtrar tareas. | Aprobado |
| CP-015 | Diseño responsive | Validar adaptación visual | 1. Abrir la aplicación en escritorio. 2. Reducir tamaño de pantalla. | La interfaz se adapta sin perder funcionalidad principal. | Los componentes se ajustan correctamente. | Aprobado |

### Pruebas no funcionales

| Código | Tipo de prueba | Descripción | Resultado esperado | Estado |
| --- | --- | --- | --- | --- |
| PNF-001 | Compatibilidad | Validar ejecución en navegador web moderno. | La aplicación funciona correctamente en Google Chrome o Microsoft Edge. | Aprobado |
| PNF-002 | Usabilidad | Verificar que la navegación sea clara para el usuario. | El usuario puede acceder a los módulos principales desde el dashboard. | Aprobado |
| PNF-003 | Seguridad | Validar que las rutas principales requieran autenticación. | El usuario debe iniciar sesión para acceder al dashboard. | Aprobado |
| PNF-004 | Integridad de datos | Verificar que las tareas y registros se almacenen en la base de datos. | La información registrada se conserva y puede consultarse nuevamente. | Aprobado |
| PNF-005 | Mantenibilidad | Revisar organización del código frontend y backend. | El sistema mantiene separación entre componentes, servicios, entidades y controladores. | Aprobado |

### Evidencias sugeridas

Para soportar la fase de pruebas dentro del documento final, se recomienda agregar capturas de pantalla de:

- Registro de usuario.
- Inicio de sesión.
- Panel principal del dashboard.
- Creación de tareas.
- Registro de horas en una tarea.
- Temporizador Pomodoro en ejecución.
- Alerta al finalizar el Pomodoro.
- Panel de tareas terminadas estilo calendario.
- Detalle emergente de una tarea terminada.
- Archivo exportado en Excel.
- Diagrama de Gantt con filtros.

### Conclusión de las pruebas

Después de ejecutar las pruebas manuales sobre los módulos principales de MiTiempo360, se evidencia que la aplicación cumple con los requerimientos funcionales planteados para el registro, seguimiento y análisis del tiempo. El sistema permite crear usuarios, iniciar sesión, gestionar tareas, registrar horas con fecha, finalizar tareas, consultar tareas terminadas en una vista tipo calendario, visualizar detalles de los registros, exportar información a Excel y revisar la distribución de actividades mediante un diagrama de Gantt.

Las pruebas realizadas también permitieron validar la integración entre el frontend desarrollado en Angular, el backend construido con Java Spring Boot y la base de datos PostgreSQL. La información registrada desde la interfaz se almacena y consulta correctamente, lo que demuestra una comunicación adecuada entre las capas del sistema.

En cuanto a la experiencia de usuario, la aplicación presenta una navegación clara y funcionalidades orientadas al control personal del tiempo, manteniendo el enfoque de simplicidad planteado en el proyecto. Por lo tanto, se concluye que MiTiempo360 es una herramienta funcional para apoyar la organización de tareas, el registro de tiempos y el análisis básico de productividad personal. Como trabajo futuro, se recomienda fortalecer las pruebas automatizadas, ampliar métricas estadísticas y realizar validaciones con usuarios reales para obtener retroalimentación sobre usabilidad y mejora continua.
