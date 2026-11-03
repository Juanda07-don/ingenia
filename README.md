# Ingenia

> Sistema Móvil de Gestión y Agendamiento de Asesorías Académicas Especializadas  
> **Facultad de Ingeniería — Universidad Autónoma de Occidente (UAO)**

---

## Descripción del Proyecto

**Ingenia** es una solución integral cliente-servidor desarrollada para reemplazar las filas y consultas presenciales por un sistema de agendamiento anticipado, trazabilidad y cierre formal de asesorías universitarias.

El sistema articula una aplicación móvil nativa en **Android (Java)**, un backend con reglas de negocio centralizadas en **Spring Boot (Java 25)** y una base de datos relacional en **MySQL**.

---

## Arquitectura y Tecnologías

```
┌───────────────────────────────────────┐
│     App Móvil Android (Java)          │
│   Material 3 · Retrofit 2 · Figma UI  │
└───────────────────▲───────────────────┘
                    │ HTTP / REST (ADB Reverse Tunnel)
┌───────────────────▼───────────────────┐
│     Backend Spring Boot (Java 25)     │
│   API REST · Spring Data JPA · Maven  │
└───────────────────▲───────────────────┘
                    │ JDBC (Puerto 3306)
┌───────────────────▼───────────────────┐
│        Base de Datos MySQL            │
│   Esquema 'ingenia' · Hash BCrypt     │
└───────────────────────────────────────┘
```

* **Frontend Móvil:** Android Nativo (Java 11/17, Min SDK 26, Target SDK 37), Retrofit 2.11.0, Gson, Material Components.
* **Backend:** Spring Boot 4.1.1, Java 25 (OpenJDK), Apache Maven, Apache Tomcat Embebido.
* **Base de Datos:** MySQL Community Server 8.4.9, Spring Data JPA / Hibernate.
* **Diseño UI:** Basado en la guía de estilos institucionales UAO (`#8C233E` - Vino UAO).

---

## Estructura del Repositorio

* **`/backend`**: Proyecto Spring Boot con controladores REST, entidades JPA, servicios de negocio, manejo de excepciones y configuración de seguridad.
* **`/android`**: Proyecto de Android Studio con actividades, adaptadores, layouts XML y cliente HTTP.
* **`/database`**: Script SQL (`ingenia.sql`) con la creación del esquema y datos iniciales de prueba (usuarios, materias, cubículos y franjas).

---

## Equipo de Trabajo

* **Juan David Tenorio Tenorio** — Líder de Pruebas / QA Principal & Desarrollador
* **Juan Esteban Bermúdez Montenegro** — Diseñador de Casos de Prueba
* **Nicolás Zapata** — Tester Funcional Móvil
* **Miguel Ángel Benavides Valencia** — Tester de Integración y Servicios

---

## Puesta en Marcha (Entorno Local)

### 1. Base de Datos (MySQL)
1. Crear la base de datos `ingenia` e importar el archivo `database/ingenia.sql` (vía MySQL Workbench o línea de comandos):
```sql
CREATE DATABASE IF NOT EXISTS ingenia;
```
2. Verificar las credenciales en `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ingenia?useSSL=false&serverTimezone=America/Bogota
spring.datasource.username=ingenia
spring.datasource.password=Ingenia2026!
spring.jpa.hibernate.ddl-auto=update
```

### 2. Iniciar el Backend
Desde la carpeta `/backend`:
```bash
mvn spring-boot:run
```
El servidor quedará escuchando en `http://localhost:8080/api/`.

### 3. Conectar el Emulador de Android
Para comunicar el emulador con el backend local sin bloqueos de firewall, abrir la terminal integrada de Android Studio (`Alt + F12`) y ejecutar:
```bash
adb reverse tcp:8080 tcp:8080
```
Si el sistema operativo no reconoce el comando `adb` directamente en Windows, ejecutar el siguiente comando genérico:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:8080 tcp:8080
```

### 4. Ejecutar la Aplicación Móvil
Abrir la carpeta `/android` en Android Studio y ejecutar el proyecto en un emulador Pixel (Android 14/15 - API 34+).

---

## Credenciales de Prueba

| Rol | Correo Institucional | Contraseña | Identificador |
| :--- | :--- | :--- | :--- |
| **Estudiante** | `valentina@uao.edu.co` | `123456` | ID: 1 |
| **Profesor** | `laura@uao.edu.co` | `123456` | ID: 2 |

---

## Licencia y Derechos
Proyecto académico desarrollado para la asignatura **Ingeniería de Software I** — Universidad Autónoma de Occidente (2026).
