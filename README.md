#  Registro de Siniestros Viales

**Desarrollador:** Sebastian Betancourt  
**Materia:** Aplicaciones Móviles 



## Cambios realizados por el autor 
- Autor añadido: **Sebastian Betancourt**.
- Nombre de la aplicación actualizado en recursos: **Registro Vial - SB**.
- Tiempo de vibración reducido a **3 segundos** (antes 5 segundos) por usabilidad.
- Mensajes de interfaz refinados y strings movidos a `res/values/strings.xml`.
- Instrucciones rápidas para ejecutar el proyecto añadidas.

##  Evidencias del Funcionamiento

| Formulario de Registro | Historial de Reportes |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/f3c7d373-da46-4368-abff-a2e5adf61f9b" width="320" alt="Formulario" /> | <img src="https://github.com/user-attachments/assets/0801b76b-4345-48f6-95c7-9475a6c53acb" width="320" alt="Historial" /> |

---

## Características Principales

- **Registro Detallado:** Captura de tipo de accidente (choque, colisión, atropello), fecha y hora automática, matrícula del vehículo, nombre del conductor y número de cédula.
- **Evidencia Multimedia:** Integración con la cámara del dispositivo para capturar fotos del siniestro en tiempo real.
- **Geolocalización (GPS):** Obtención de coordenadas exactas (latitud y longitud) del lugar del incidente mediante servicios de ubicación.
- **Feedback Háptico:** Al guardar un reporte exitosamente, el dispositivo emite una **vibración de 3 segundos** (ajustada por el autor para mejor ergonomía).
- **Historial Local:** Listado de todos los reportes guardados almacenados de forma persistente.
- **Gestión de Permisos:** Implementación de permisos dinámicos para Cámara y Ubicación.

---

## Cómo ejecutar (resumen rápido)

1. Abrir la carpeta raíz del proyecto en **Android Studio** (la que contiene `settings.gradle.kts` y `gradlew`).
2. Verificar ruta del Android SDK en `File → Settings → Android SDK`.
3. `File → Sync Project with Gradle Files`.
4. Seleccionar emulador o dispositivo físico (depuración USB).
5. Ejecutar con **Run ▶**.

---

