# 🏛️ Sistema de Gestión de Trámites y Licencias

Un sistema Full-Stack diseñado para digitalizar, gestionar y emitir licencias de funcionamiento municipales. Permite a los ciudadanos (administrados) registrar solicitudes y a los funcionarios evaluar expedientes, finalizando con la emisión automática de un documento PDF validado con un Código QR.

---

<img width="406" height="288" alt="image" src="https://github.com/user-attachments/assets/c9b96149-eb6f-46c1-ba50-1c4b8d301a34" />


## 🛠️ Stack Tecnológico

**Backend:**
* **Java 17 + Spring Boot 3** (REST API)
* **Spring Data JPA / Hibernate** (ORM)
* **Spring Security + JWT** (Autenticación y Autorización)
* **MySQL** (Base de datos relacional)
* **OpenPDF + ZXing** (Generación de documentos físicos y Códigos QR)

**Frontend:**
* **Angular 17+** (Arquitectura Standalone Components)
* **Bootstrap 5 + Bootstrap Icons** (UI/UX)
* **RxJS** (Gestión de estado y peticiones HTTP)

---

## 🔄 Arquitectura y Flujo de Trabajo (Workflow)

El corazón de este sistema es su máquina de estados y la separación estricta de roles. El ciclo de vida de un trámite está controlado para garantizar la integridad de los datos y evitar alteraciones una vez que se emite una resolución.

### 👥 Roles del Sistema
1. **ROLE_ADMINISTRADO (Ciudadano):** Puede registrarse en la plataforma, solicitar nuevos trámites y descargar sus licencias emitidas. No tiene permisos de evaluación.
2. **ROLE_EVALUADOR (Funcionario):** Tiene acceso a la bandeja de trámites pendientes. Es el único autorizado para aprobar, rechazar y emitir resoluciones finales.

### 🛤️ El Flujo del Trámite (Paso a Paso)

El sistema sigue una lógica lineal inmutable. Un trámite no puede saltarse pasos ni retroceder a estados anteriores una vez procesado.

```text
[ 👤 CIUDADANO ]                         [ 👮 FUNCIONARIO ]                        [ ⚙️ SISTEMA ]
       |                                          |                                       |
 1. REGISTRO ------------------------------------>|                                       |
  (Inicia sesión y crea un nuevo expediente)      |                                       |
       |                                          |                                       |
       |---> ESTADO: PENDIENTE                    |                                       |
                                                  |                                       |
                                           2. EVALUACIÓN <--------------------------------|
                                  (Revisa la bandeja de pendientes)                       |
                                                  |                                       |
                                      [¿Cumple los requisitos?]                           |
                                      /                       \                           |
                                 (NO)                           (SÍ)                      |
                                  /                               \                       |
                    ESTADO: RECHAZADO                       ESTADO: APROBADO              |
                      (Fin del flujo)                             |                       |
                                                                  |                       |
                                                           3. EMISIÓN ------------------->|
                                              (El funcionario presiona "Emitir")          |
                                                                                          |
                                                                           4. GENERACIÓN PDF
                                                             (Se crea el documento con código QR)
                                                                                          |
                                                                           ESTADO: EMITIDO
```

### 🔒 Reglas de Negocio Implementadas
* **Bloqueo por Estado:** Un trámite `APROBADO`, `RECHAZADO` o `EMITIDO` desaparece de la cola de evaluación. No puede volver a ser evaluado.
* **Prevención de Duplicados:** La base de datos restringe (Relación `1:1`) la creación de más de una licencia por cada expediente.
* **Autenticidad:** Cada PDF generado inyecta un Código QR único que apunta al endpoint público de validación del sistema (`/api/validar/{numeroLicencia}`).

---

## 🚀 Instalación y Ejecución

### 1. Base de Datos
* Crea una base de datos en MySQL llamada `db_licencias`.
* El sistema cuenta con un **Data Seeder** automático. Al levantar el proyecto por primera vez, Spring Boot creará las tablas e insertará los Roles y Tipos de Trámites necesarios automáticamente para que no tengas que inyectar SQL manual.

### 2. Backend (Spring Boot)
1. Abre el proyecto en tu IDE preferido (IntelliJ IDEA, Eclipse, VS Code).
2. Configura tus credenciales de MySQL en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/db_licencias
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_password
   # Opción recomendada para desarrollo limpio:
   spring.jpa.show-sql=false
   logging.level.org.springframework.web=WARN
   logging.level.org.apache.catalina=WARN
   ```
3. Ejecuta la clase principal (`SistemaLicenciasApplication.java`).
4. **Sandbox de Consola:** El backend incluye un simulador interactivo por consola (CLI). Puedes ver los logs en la terminal de tu IDE e interactuar numéricamente para crear usuarios, registrar trámites, evaluar expedientes y generar PDFs físicos sin necesidad de usar el Frontend o herramientas externas.

### 3. Frontend (Angular)
1. Navega a la carpeta del frontend en tu terminal.
2. Instala las dependencias necesarias:
   ```bash
   npm install
   ```
3. Levanta el servidor de desarrollo:
   ```bash
   ng serve
   ```
4. Abre tu navegador en `http://localhost:4200/login`.

---

## 🎯 Casos de Uso (CUS) Completados
- [x] **CUS01:** Iniciar Sesión (Autenticación y protección de rutas con Guards).
- [x] **CUS02:** Registrar Solicitud de Trámite.
- [x] **CUS03:** Consultar Trámites y Estados (Bandeja principal interactiva).
- [x] **CUS04:** Evaluar Trámite (Aprobar / Rechazar con control de estado).
- [x] **CUS05:** Emitir Resolución (Generación de registro de Licencia).
- [x] **CUS06/07:** Generar y Descargar Licencia en formato PDF físico con Código QR y apertura automática mediante Java Desktop.
