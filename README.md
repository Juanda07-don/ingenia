# Ingenia 🎓

> Sistema Móvil de Gestión y Agendamiento de Asesorías Académicas Especializadas  
> **Facultad de Ingeniería — Universidad Autónoma de Occidente (UAO)**

---

## 📌 Descripción del Proyecto

**Ingenia** es una solución integral cliente-servidor desarrollada para reemplazar las filas y consultas presenciales por un sistema de agendamiento anticipado, trazabilidad y cierre formal de asesorías universitarias.

El sistema articula una aplicación móvil nativa en **Android (Java)**, un backend con reglas de negocio centralizadas en **Spring Boot (Java 25)** y una base de datos relacional en **MySQL**.

---

## 🏗️ Arquitectura y Tecnologías

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

* **Frontend Móvil:** Android Nativo (Java 17/21, Min SDK 26), Retrofit 2.9.0, Gson, Material Components.
* **Backend:** Spring Boot 3.x / 4.x, Java 25 (OpenJDK), Maven, Tomcat Embebido.
* **Base de Datos:** MySQL Server 8.0, JPA/Hibernate.
* **Diseño UI:** Basado en la guía de estilos institucionales UAO (`#8C233E` - Vino UAO).

---

## 📂 Estructura del Repositorio

* **`/backend`**: Proyecto Spring Boot con controladores REST, entidades JPA, servicios de negocio, manejo de excepciones y configuración de seguridad.
* **`/android`**: Proyecto de Android Studio con actividades, adaptadores, layouts XML y cliente HTTP.

---

## 👥 Equipo de Trabajo

* **Juan David Tenorio Tenorio** — Líder de Pruebas / QA Principal & Desarrollador
* **Juan Esteban Bermúdez Montenegro** — Diseñador de Casos de Prueba
* **Nicolás Zapata** — Tester Funcional Móvil
* **Miguel Ángel Benavides Valencia** — Tester de Integración y Servicios

---

## 🚀 Puesta en Marcha (Entorno Local)

### 1. Base de Datos (MySQL)
Crear la base de datos `ingenia` y configurar las credenciales en `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ingenia?useSSL=false&serverTimezone=UTC
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
Para comunicar el emulador con el backend local sin bloqueos de firewall:
```bash
adb reverse tcp:8080 tcp:8080
```

### 4. Ejecutar la App Móvil
Abrir la carpeta `/android` en Android Studio y ejecutar el proyecto en un emulador Pixel (Android 14/15 - API 34+).

---

## 🔑 Credenciales de Prueba

| Rol | Correo Institucional | Contraseña | Identificador |
| :--- | :--- | :--- | :--- |
| **Estudiante** | `valentina@uao.edu.co` | `123456` | ID: 1 |
| **Profesor** | `laura@uao.edu.co` | `123456` | ID: 2 |

---

## 📄 Licencia y Derechos
Proyecto académico desarrollado para la asignatura **Ingeniería de Software I** — Universidad Autónoma de Occidente (2026).
