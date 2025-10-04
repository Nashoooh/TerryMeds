# Guía Completa para Firmar y Generar APK para Google Play Store

## ✅ Configuración Completada

Tu app TerryMeds ya está configurada para generar APKs firmadas. Estos son los pasos que se han completado:

### 1. Keystore Generado ✅
- Archivo: `terrymeds-release-key.keystore`
- Alias: `terrymeds`
- Algoritmo: RSA 2048 bits
- Validez: 10,000 días (~27 años)

### 2. Configuración del Proyecto ✅
- `keystore.properties` creado con credenciales
- `build.gradle.kts` configurado para firma automática
- `proguard-rules.pro` optimizado para release
- `.gitignore` actualizado para proteger el keystore

## 🚀 Comandos para Generar APK

### Desde Terminal/CMD:
```bash
# Navegar al directorio del proyecto
cd "C:\Users\ignac\AndroidStudioProjects\TerryMeds"

# Limpiar y generar APK firmada
.\gradlew.bat clean assembleRelease
```

### Desde Android Studio:
1. Abrir Android Studio
2. Ir a `Build` → `Generate Signed Bundle/APK`
3. Seleccionar `APK`
4. Usar el keystore: `terrymeds-release-key.keystore`
5. Alias: `terrymeds`
6. Contraseña: La que configuraste
7. Seleccionar `release`
8. Hacer click en `Finish`

## 📦 Ubicación de la APK Generada

La APK firmada se generará en:
```
app/build/outputs/apk/release/app-release.apk
```

## 🔐 Información de Seguridad

### ⚠️ IMPORTANTE - Guarda esta información:
- **Keystore file**: `terrymeds-release-key.keystore`
- **Keystore password**: [La que configuraste]
- **Key alias**: `terrymeds`
- **Key password**: [La que configuraste]

### 🔒 Backup del Keystore:
1. Haz una copia de `terrymeds-release-key.keystore`
2. Guárdala en un lugar seguro (cloud, disco externo)
3. **NUNCA** subas el keystore al repositorio Git
4. Si pierdes el keystore, no podrás actualizar la app en Play Store

## 📱 Para Subir a Google Play Store

### Requisitos de la APK:
- ✅ Firmada con keystore de release
- ✅ Optimizada (minifyEnabled = true)
- ✅ Recursos comprimidos (shrinkResources = true)
- ✅ ProGuard aplicado

### Pasos en Google Play Console:
1. Crear cuenta de desarrollador en Google Play Console
2. Pagar la tarifa única de $25 USD
3. Crear nueva aplicación
4. Subir la APK generada (`app-release.apk`)
5. Completar información de la store
6. Configurar precios y distribución
7. Enviar para revisión

## 🛠️ Comandos Útiles

### Generar Bundle (Recomendado para Play Store):
```bash
.\gradlew.bat bundleRelease
```
Ubicación: `app/build/outputs/bundle/release/app-release.aab`

### Verificar firma de la APK:
```bash
jarsigner -verify -verbose -certs app-release.apk
```

### Ver información del keystore:
```bash
keytool -list -v -keystore terrymeds-release-key.keystore
```

## 📋 Lista de Verificación Pre-Subida

- [ ] APK generada y firmada
- [ ] Versión y versionCode correctos
- [ ] Íconos de la app en todas las resoluciones
- [ ] Screenshots para Play Store
- [ ] Descripción de la app en español
- [ ] Política de privacidad (si recolectas datos)
- [ ] Términos de servicio
- [ ] Categoría de la app seleccionada
- [ ] Rating de contenido configurado

## 🎯 Próximos Pasos

1. **Generar APK**: Ejecutar `.\gradlew.bat assembleRelease`
2. **Probar APK**: Instalar en dispositivo y probar
3. **Crear cuenta**: Google Play Console
4. **Preparar assets**: Íconos, screenshots, descripción
5. **Subir APK**: A Google Play Console
6. **Configurar store**: Información, precios, distribución
7. **Enviar para revisión**: Proceso puede tomar 1-3 días

## 🔄 Para Futuras Actualizaciones

1. Incrementar `versionCode` en `build.gradle.kts`
2. Actualizar `versionName` si es necesario
3. Generar nueva APK firmada
4. Subir como actualización en Play Console

---
**¡Tu app TerryMeds está lista para ser publicada en Google Play Store!** 🎉
