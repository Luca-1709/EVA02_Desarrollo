# AgroAlerta

App Android (Kotlin + Jetpack Compose + Material 3, arquitectura MVVM) para el caso **DSY1105 · AgroAlerta**:
monitoreo móvil de telemetría y alertas críticas del predio de "Agrícola FRUTIS" (datos ficticios).

## Cumplimiento de la segunda entrega

| Punto | Implementación |
|---|---|
| 1. Pantallas por rol | Login, Registro, Inicio (distinto por rol), Sensores, Actuadores, Alertas, Detalle de alerta, Historial, Reportar incidencia, Resumen ejecutivo (Gerencia), Gestión de usuarios y Configuración remota (ASTI), Mi perfil |
| 2. Navegación | `NavHost` + `NavController` con rutas en `sealed class Screen`; `Scaffold` con `TopAppBar`, `NavigationBar` inferior y `ModalNavigationDrawer` con opciones según el rol. En tablets se usa `NavigationRail` (diseño adaptable) |
| 3. Formularios | Reportar incidencia, Registrar acción sobre una alerta, Configuración de umbrales, Editar perfil y control de actuadores: validan los campos y muestran el diálogo **"Para una futura actualización"** |
| 4. Login con SQLite | `AgroDbHelper` (SQLiteOpenHelper, BD `agroalerta.db`): registro y autenticación offline; contraseñas guardadas como hash SHA-256; correo único |

## Usuarios de prueba (contraseña `123456`)

| Rol | Correo |
|---|---|
| Supervisor agrícola | supervisor@frutis.cl |
| Técnico de mantenimiento | tecnico@frutis.cl |
| Gerencia | gerencia@frutis.cl |
| Equipo técnico ASTI | admin@asti.cl |

También se puede crear una cuenta nueva desde "Regístrate" eligiendo el rol.

## Estructura (MVVM)

```
app/src/main/java/com/example/myapplication/
├── MainActivity.kt
├── model/            Rol, Usuario, Telemetria (cuarteles, actuadores, alertas, eventos)
├── data/
│   ├── MockData.kt   Caché simulada de las últimas lecturas
│   ├── local/        AgroDbHelper (SQLite)
│   └── repository/   UsuarioRepository
├── viewmodel/        AuthViewModel, MonitoreoViewModel
├── navigation/       Screen (rutas), MenuPorRol, AppNavigation (NavHost + Scaffold)
└── ui/
    ├── components/   Barras de navegación, diálogo "futura actualización", tarjetas
    ├── screens/      Pantallas
    ├── theme/        Colores y tema Material 3
    └── utils/        WindowSizeUtils (compacto / mediano / expandido)
```

## Guion sugerido para el video (≤ 90 s)

1. **0–15 s** Registro de un usuario nuevo → volver al login → iniciar sesión (SQLite). Mostrar error con clave incorrecta.
2. **15–40 s** Supervisor: Inicio, Sensores, Alertas → Detalle → "Registrar acción" (diálogo *Para una futura actualización*), Historial, Reportar incidencia.
3. **40–55 s** Técnico: Actuadores (switch → diálogo), menú lateral.
4. **55–70 s** Gerencia: Resumen ejecutivo.
5. **70–90 s** ASTI: Gestión de usuarios (muestra el usuario recién registrado en SQLite), Configuración → guardar, Perfil → Cerrar sesión.

## Ejecutar

Abrir el proyecto en Android Studio, sincronizar Gradle y ejecutar el módulo `app` en un emulador (API 24+).
