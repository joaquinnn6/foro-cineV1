# **CineVerse – Foro y Noticias de Cine**

Proyecto académico desarrollado para la asignatura de Desarrollo de Aplicaciones Móviles.  
Incluye **aplicación Android (Jetpack Compose)** + **microservicio backend (Spring Boot)** + **pruebas unitarias** + **APK release firmado**.

---

## **Integrantes del equipo**

| Nombre completo | Rol |
|-----------------|------|
| **Martín Felipe Céspedes Galarce** | Backend |
| **Joaquín Ignacio Contreras Bugueño** | Frontend |

---

# **Descripción general**

**CineVerse** es una aplicación móvil para Android que permite:

- Ver **películas populares** obtenidas desde la API de TMDB.  
- Leer noticias y categorías destacadas de cine.  
- Acceder a un **foro interactivo** donde los usuarios pueden:
  - Crear publicaciones  
  - Dar like / dislike  
  - Comentar  
  - Eliminar publicaciones (solo creador o admin)
- Administrar un **perfil personal** con foto, nombre y ubicación.
- Autenticación de usuarios (login/registro).

El proyecto incluye:

✔ Microservicio backend REST en Spring Boot en la nube (Render)
✔ Aplicación móvil Android en Jetpack Compose  
✔ Tests unitarios (JUnit)  
✔ APK release firmada  
✔ Keystore de firma  
✔ Trabajo colaborativo GitHub

---

# **Funcionalidades principales**

### Autenticación
- Registro de usuario
- Login
- Persistencia de sesión (DataStore)
- Manejo de rol: `USER` / `ADMIN`

### Noticias y cine
- Películas populares obtenidas desde TMDB API
- Noticias demo en categorías (Estrenos, Reseñas, Recomendaciones)

### Foro de cine
- Crear publicaciones
- Ver feed de posts
- Likes / Dislikes con control por usuario
- Comentarios por post
- Eliminar post (solo creador o administrador)
- Actualización en tiempo real desde backend

### Perfil de usuario
- Actualizar nombre, ubicación y foto
- Guardar foto de perfil en DataStore
- Cerrar sesión

---

# **Endpoints utilizados**

## Back-end propio (Spring Boot – Puerto 10000)

### **Usuarios**
| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| POST | `/api/users/register` | Registrar usuario |
| POST | `/api/users/login` | Login |
| PUT | `/api/users/{id}` | Actualizar perfil |
| GET | `/api/users/{id}` | Obtener usuario por ID |

### **Posts (foro)**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/posts` | Listar publicaciones |
| POST | `/api/posts` | Crear publicación |
| POST | `/api/posts/{postId}/vote?userId=&vote=` | Votar (like/dislike) |
| DELETE | `/api/posts/{id}?userId=` | Eliminar publicación |

### **Comentarios**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/comments/post/{postId}` | Listar comentarios de un post |
| POST | `/api/comments` | Crear comentario |
| DELETE | `/api/comments/{id}` | Eliminar comentario |

---

# **API Externa utilizada**

### **TMDB API (TheMovieDatabase)**  

Endpoints usados:
- `/movie/popular`
- `/movie/{id}`
- `/movie/{id}/credits`

---

# **Instrucciones para ejecutar el proyecto**

## **Backend (Spring Boot)**

1. Servidor activo en:
   ```
   https://foro-cine-backend.onrender.com
   ```
2. backend ubicado en:
   ```
   https://github.com/Pastito247/foro-cine-backend
   ```
---

## **Aplicación Android**

1. Abrir carpeta `foro-cineV1` en Android Studio.
2. Esperar sincronización de Gradle.
3. Conectar un celular o abrir emulador.
4. Ejecutar con ▶️ **Run App**.

---

# **APK firmado**

Ubicación:
```
app/build/outputs/apk/release/app-release.apk
```

### 🗝 Ubicación del archivo .jks

```
https://drive.google.com/file/d/1lL26-f_f3FpUjIF3ZhQ6FYJ2hs85-R7f/view?usp=sharing
```

---

# **Pruebas unitarias**

Ubicación:
```
app/src/test/java/com/example/foro_cinev1/
```

Incluye pruebas de:
- Modelos
- Noticias
- TMDB
- Comentarios
- Likes/Dislikes

---

# **Evidencia de trabajo colaborativo**

En GitHub → Insights → Contributors  
Se muestran commits de **ambos integrantes** del proyecto.

---

# **Código fuente incluido**

- `/foro-cineV1` – Aplicación Android  
- `/README.md` – Este archivo  
- `/app-release.apk` – APK final  

---

# **Estado final del proyecto**

✔ Backend completo  
✔ App funcional  
✔ Pruebas listas  
✔ APK firmada  
✔ Documentación OK 