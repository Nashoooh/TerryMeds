# Implementación de SQLite en TerryMeds

## Resumen de Cambios

Se ha implementado exitosamente SQLite como sistema de persistencia de datos en la aplicación TerryMeds, reemplazando el almacenamiento en memoria que se usaba anteriormente. Esto permite que los datos de usuarios y medicamentos se mantengan entre sesiones de la aplicación.

## Arquitectura Implementada

### 1. **Estructura de Base de Datos** (`TerryMedsDBHelper.kt`)
- **Base de datos**: `terry_meds.db` (versión 2)
- **Tabla usuarios**: Almacena información de usuarios registrados
- **Tabla medicamentos**: Almacena medicamentos de cada usuario
- **Relaciones**: Foreign key entre medicamentos y usuarios

#### Esquema de Tablas:

**Tabla `users`:**
```sql
CREATE TABLE users (
    email TEXT PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    password TEXT NOT NULL,
    birth_date INTEGER NOT NULL
)
```

**Tabla `medicamentos`:**
```sql
CREATE TABLE medicamentos (
    id TEXT PRIMARY KEY,
    user_email TEXT NOT NULL,
    nombre_medicamento TEXT NOT NULL,
    forma_farmaceutica TEXT NOT NULL,
    concentracion TEXT,
    cantidad_por_dosis REAL NOT NULL,
    unidad_dosis TEXT NOT NULL,
    numero_total_dosis INTEGER,
    hora_inicio_hora INTEGER NOT NULL,
    hora_inicio_minuto INTEGER NOT NULL,
    intervalo_entre_dosis_horas INTEGER NOT NULL,
    instrucciones TEXT,
    fecha_inicio_tratamiento_epoch_day INTEGER NOT NULL,
    activo INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (user_email) REFERENCES users (email)
)
```

### 2. **Capa de Acceso a Datos (DAO)**

#### `UserDao.kt`
- ✅ `insert(user)` - Crear nuevo usuario
- ✅ `update(user)` - Actualizar usuario existente
- ✅ `delete(email)` - Eliminar usuario
- ✅ `getAll()` - Obtener todos los usuarios
- ✅ `getByEmail(email)` - Buscar usuario por email
- ✅ `authenticate(email, password)` - Autenticación
- ✅ `search(query)` - Búsqueda por texto
- ✅ `updatePassword(email, newPassword)` - Cambiar contraseña

#### `MedicamentoDao.kt`
- ✅ `insert(medicamento)` - Crear nuevo medicamento
- ✅ `update(medicamento)` - Actualizar medicamento
- ✅ `delete(id)` - Eliminar medicamento
- ✅ `getAll()` - Obtener todos los medicamentos
- ✅ `getByUserEmail(email)` - Medicamentos por usuario
- ✅ `getActiveByUserEmail(email)` - Medicamentos activos por usuario
- ✅ `getById(id)` - Buscar medicamento por ID
- ✅ `search(query, userEmail)` - Búsqueda por texto
- ✅ `deactivate(id)` / `activate(id)` - Cambiar estado

### 3. **Managers con Patrón Singleton**

#### `SQLiteUserManager.kt`
- Implementa patrón Singleton thread-safe
- Maneja operaciones de usuario usando `UserDao`
- Inicialización automática de usuario por defecto
- Compatible con la interfaz original

#### `SQLiteMedicamentoManager.kt`
- Implementa patrón Singleton thread-safe
- Maneja operaciones de medicamentos usando `MedicamentoDao`
- Mantiene funciones auxiliares (cálculo de días restantes, próxima dosis)
- Inicialización automática de medicamentos de ejemplo

### 4. **Pantallas Actualizadas**

Todas las pantallas han sido actualizadas para usar SQLite:

- ✅ **LoginScreen**: Usa `SQLiteUserManager` para autenticación
- ✅ **RegisterScreen**: Usa `SQLiteUserManager` para registro
- ✅ **ResetPasswordScreen**: Usa `SQLiteUserManager` para cambio de contraseña
- ✅ **HomeScreen**: Usa `SQLiteMedicamentoManager` para mostrar medicamentos
- ✅ **AgregarMedicamentoScreen**: Usa `SQLiteMedicamentoManager` para agregar medicamentos

## Inicialización Automática

### En `MainActivity.onCreate()`:
```kotlin
private fun initializeSQLiteData() {
    // Inicializar el manager de usuarios con datos por defecto
    val userManager = SQLiteUserManager.getInstance(this)
    userManager.initializeDefaultUser()
    
    // Inicializar el manager de medicamentos con datos por defecto
    val medicamentoManager = SQLiteMedicamentoManager.getInstance(this)
    medicamentoManager.initializeDefaultMedicamentos()
}
```

### Usuario por Defecto:
- **Email**: nacho@correo.com
- **Contraseña**: Clave123
- **Nombre**: Ignacio Andana
- **Fecha de nacimiento**: 30/12/1992

### Medicamentos de Ejemplo:
1. **Paracetamol** (500 mg, cada 8h, 20 dosis)
2. **Vitamina D3** (1000 UI, cada 24h, 30 dosis)

## Uso en las Pantallas

### Ejemplo en Composables:
```kotlin
@Composable
fun MiPantalla() {
    val context = LocalContext.current
    val userManager = remember { SQLiteUserManager.getInstance(context) }
    val medicamentoManager = remember { SQLiteMedicamentoManager.getInstance(context) }
    
    // Usar los managers...
}
```

## Beneficios de la Implementación

### ✅ **Persistencia de Datos**
- Los datos se mantienen entre sesiones de la app
- No se pierden al cerrar o reiniciar la aplicación

### ✅ **Rendimiento Optimizado**
- Consultas SQL eficientes
- Lazy loading de datos
- Singleton pattern para managers

### ✅ **Escalabilidad**
- Estructura preparada para agregar nuevas tablas
- Sistema de versioning de BD implementado
- Relaciones entre tablas establecidas

### ✅ **Compatibilidad**
- Interfaz compatible con el código existente
- Migración transparente desde memoria a SQLite
- Mismo comportamiento desde la perspectiva del usuario

### ✅ **Robustez**
- Manejo de errores implementado
- Transacciones automáticas
- Validación de datos

## Archivos Creados/Modificados

### **Archivos Nuevos:**
- `data/sqlite/TerryMedsDBHelper.kt` - Helper de base de datos
- `data/sqlite/UserDao.kt` - DAO para usuarios
- `data/sqlite/MedicamentoDao.kt` - DAO para medicamentos
- `data/sqlite/SQLiteUserManager.kt` - Manager de usuarios con SQLite
- `data/sqlite/SQLiteMedicamentoManager.kt` - Manager de medicamentos con SQLite

### **Archivos Modificados:**
- `MainActivity.kt` - Inicialización de SQLite
- `ui/screens/LoginScreen.kt` - Uso de SQLiteUserManager
- `ui/screens/RegisterScreen.kt` - Uso de SQLiteUserManager
- `ui/screens/ResetPasswordScreen.kt` - Uso de SQLiteUserManager
- `ui/screens/HomeScreen.kt` - Uso de SQLiteMedicamentoManager
- `ui/screens/AgregarMedicamentoScreen.kt` - Uso de SQLiteMedicamentoManager

## Verificación de la Implementación

### ✅ **Compilación Exitosa**
- Build completo sin errores
- Solo advertencias menores sobre APIs obsoletas
- Todos los tests unitarios pasan

### ✅ **Funcionalidad Preservada**
- Login y registro funcionan correctamente
- Gestión de medicamentos mantiene todas las características
- Cálculos de dosis y tratamientos funcionan igual

### ✅ **Accesibilidad Mantenida**
- Los cambios de accesibilidad visual se mantienen intactos
- Compatible con el tema accesible implementado

## Próximos Pasos Opcionales

Para futuras mejoras, se podrían considerar:

1. **Migraciones de BD**: Sistema más robusto para actualizaciones de esquema
2. **Índices**: Agregar índices para mejorar rendimiento de consultas
3. **Validaciones**: Validaciones más estrictas a nivel de base de datos
4. **Backup/Restore**: Sistema de respaldo y restauración de datos
5. **Encriptación**: Encriptar datos sensibles como contraseñas
6. **Logs**: Sistema de logs para debugging y monitoreo

## Conclusión

La implementación de SQLite ha sido exitosa y transparente. La aplicación ahora cuenta con persistencia de datos robusta manteniendo toda la funcionalidad existente y la accesibilidad mejorada implementada anteriormente.
