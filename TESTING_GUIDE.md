# Guía de Pruebas Unitarias - TerryMeds

## Introducción
Este proyecto incluye pruebas unitarias usando **JUnit** y **Mockito** para validar la funcionalidad de la aplicación TerryMeds.

## Estructura de Pruebas
Las pruebas se encuentran en: `app/src/test/java/com/example/terrymeds/`

## Archivos de Prueba
- `ExampleUnitTest.kt` - Prueba básica de ejemplo
- `SimpleUserTest.kt` - Prueba unitaria simple con JUnit y Mockito

## Dependencias de Testing

### JUnit
```kotlin
testImplementation(libs.junit)
```

### Mockito
```kotlin
testImplementation("org.mockito:mockito-core:4.11.0")
testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
```

## Ejemplo de Prueba Simple: SimpleUserTest.kt

### Características del Test:
1. **JUnit**: Para estructura básica de testing
2. **Mockito**: Para crear objetos mock y simular comportamiento
3. **Validaciones**: Para verificar funcionalidad de UserData

### Métodos de Prueba:

#### 1. `testUserDataCreation()`
- **Propósito**: Verificar que la clase UserData se crea correctamente
- **Utiliza**: JUnit asserts (assertEquals)
- **Verifica**: Que todos los campos se asignen correctamente

#### 2. `testEmailValidation()`
- **Propósito**: Validar formato básico de email
- **Utiliza**: assertTrue, assertFalse
- **Verifica**: Que el email contenga @ y .

#### 3. `testPasswordStrength()`
- **Propósito**: Verificar criterios básicos de contraseña
- **Utiliza**: assertTrue, any{} de Kotlin
- **Verifica**: Longitud, números y letras

#### 4. `testMockUserData()`
- **Propósito**: Demostrar uso de Mockito
- **Utiliza**: @Mock, whenever().thenReturn()
- **Verifica**: Que los mocks retornen valores esperados

#### 5. `testUserFullName()`
- **Propósito**: Validar concatenación de nombres
- **Utiliza**: JUnit asserts y String operations
- **Verifica**: Formato correcto del nombre completo

## Cómo Ejecutar las Pruebas

### Desde Android Studio:
1. Abrir la ventana "Run" (ejecutar)
2. Seleccionar "Run All Tests" (ejecutar todas las pruebas)
3. O click derecho en el archivo de test → "Run"

### Desde Terminal:
```bash
./gradlew test
```

### Para un test específico:
```bash
./gradlew test --tests com.example.terrymeds.SimpleUserTest
```

## Anotaciones Utilizadas

### JUnit:
- `@Test`: Marca un método como prueba unitaria
- `@Before`: Ejecuta antes de cada test

### Mockito:
- `@Mock`: Crea un objeto mock
- `whenever()`: Configura comportamiento del mock
- `thenReturn()`: Define valor de retorno del mock

## Métodos de Verificación

### JUnit Asserts:
- `assertEquals(expected, actual)`: Verifica igualdad
- `assertTrue(condition)`: Verifica que sea verdadero
- `assertFalse(condition)`: Verifica que sea falso

### Kotlin Extensions:
- `any{}`: Verifica que algún elemento cumpla condición
- `contains()`: Verifica que contenga un substring

## Notas Importantes
- Las pruebas no requieren conexión a base de datos
- Se ejecutan en la JVM local (no en dispositivo Android)
- Son rápidas y determinísticas
- Validan lógica de negocio básica sin dependencias externas
