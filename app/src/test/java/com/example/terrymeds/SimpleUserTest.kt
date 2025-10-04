package com.example.terrymeds

import com.example.terrymeds.data.UserData
import com.example.terrymeds.data.UserManager
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

/**
 * Prueba unitaria simple con JUnit y Mockito
 * Esta prueba demuestra el uso básico de JUnit para testing y Mockito para mocking
 */
class SimpleUserTest {

    // Interface simple para testing con Mockito
    interface UserService {
        fun isEmailValid(email: String): Boolean
        fun isPasswordStrong(password: String): Boolean
        fun getUserById(id: Int): String
    }

    // Mock object usando Mockito
    @Mock
    private lateinit var mockUserService: UserService

    @Before
    fun setUp() {
        // Inicializar los mocks
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testUserDataCreation() {
        // Arrange - Configurar datos de prueba
        val firstName = "Juan"
        val lastName = "Pérez"
        val email = "juan@test.com"
        val password = "password123"
        val birthDate = System.currentTimeMillis()

        // Act - Crear el objeto UserData
        val userData = UserData(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            birthDate = birthDate
        )

        // Assert - Verificar que los datos son correctos (JUnit)
        assertEquals("El nombre debería ser Juan", firstName, userData.firstName)
        assertEquals("El apellido debería ser Pérez", lastName, userData.lastName)
        assertEquals("El email debería coincidir", email, userData.email)
        assertEquals("La contraseña debería coincidir", password, userData.password)
        assertEquals("La fecha de nacimiento debería coincidir", birthDate, userData.birthDate)
    }

    @Test
    fun testEmailValidation() {
        // Arrange & Act - Probar diferentes emails (JUnit básico)
        val validEmail = "usuario@correo.com"
        val invalidEmail = "email-invalido"

        // Assert - Validar formato de email usando JUnit asserts
        assertTrue("Email válido debería contener @", validEmail.contains("@"))
        assertTrue("Email válido debería contener .", validEmail.contains("."))
        assertFalse("Email inválido no debería ser válido", 
            invalidEmail.contains("@") && invalidEmail.contains("."))
    }

    @Test
    fun testPasswordStrength() {
        // Arrange
        val weakPassword = "123"
        val strongPassword = "MiPassword123"

        // Act & Assert - Usar JUnit para validar reglas de contraseña
        assertTrue("Contraseña débil debería ser menor a 6 caracteres", weakPassword.length < 6)
        assertTrue("Contraseña fuerte debería tener al menos 6 caracteres", strongPassword.length >= 6)
        assertTrue("Contraseña fuerte debería contener números", strongPassword.any { it.isDigit() })
        assertTrue("Contraseña fuerte debería contener letras", strongPassword.any { it.isLetter() })
    }

    @Test
    fun testMockUserService() {
        // Arrange - Configurar el comportamiento del mock usando Mockito
        whenever(mockUserService.isEmailValid("test@example.com")).thenReturn(true)
        whenever(mockUserService.isEmailValid("invalid-email")).thenReturn(false)
        whenever(mockUserService.isPasswordStrong("123")).thenReturn(false)
        whenever(mockUserService.isPasswordStrong("StrongPass123")).thenReturn(true)
        whenever(mockUserService.getUserById(1)).thenReturn("Juan Pérez")

        // Act & Assert - Verificar que el mock funciona correctamente
        assertTrue("Email válido debería retornar true", mockUserService.isEmailValid("test@example.com"))
        assertFalse("Email inválido debería retornar false", mockUserService.isEmailValid("invalid-email"))
        assertFalse("Contraseña débil debería retornar false", mockUserService.isPasswordStrong("123"))
        assertTrue("Contraseña fuerte debería retornar true", mockUserService.isPasswordStrong("StrongPass123"))
        assertEquals("Usuario con ID 1 debería ser Juan Pérez", "Juan Pérez", mockUserService.getUserById(1))
    }

    @Test
    fun testUserFullName() {
        // Arrange - Crear userData usando constructor real
        val userData = UserData(
            firstName = "Ana",
            lastName = "García",
            email = "ana@test.com",
            password = "password123",
            birthDate = System.currentTimeMillis()
        )

        // Act - Crear nombre completo (lógica de negocio simple)
        val fullName = "${userData.firstName} ${userData.lastName}"

        // Assert - Verificar con JUnit
        assertEquals("Ana García", fullName)
        assertTrue("El nombre completo debería contener el nombre", fullName.contains("Ana"))
        assertTrue("El nombre completo debería contener el apellido", fullName.contains("García"))
    }

    @Test
    fun testUserManagerBasicFunctionality() {
        // Arrange - Usar el UserManager real de la aplicación
        val userManager = UserManager
        val testUser = UserData(
            firstName = "Test",
            lastName = "User",
            email = "test@unittest.com",
            password = "testpass123",
            birthDate = System.currentTimeMillis()
        )

        // Act - Agregar usuario
        userManager.addUser(testUser)

        // Assert - Verificar que el usuario fue agregado
        val usuarioEncontrado = userManager.findUserByEmail(testUser.email)
        assertNotNull("El usuario debería existir", usuarioEncontrado)
        assertEquals("El email debería coincidir", testUser.email, usuarioEncontrado?.email)
        
        // Act - Autenticar usuario
        val usuarioAutenticado = userManager.loginUser(testUser.email, testUser.password)
        
        // Assert - Verificar autenticación
        assertNotNull("La autenticación debería ser exitosa", usuarioAutenticado)
        assertEquals("El email debería coincidir", testUser.email, usuarioAutenticado?.email)
        
        // Cleanup - Eliminar usuario de prueba
        userManager.deleteUserByEmail(testUser.email)
    }
}
