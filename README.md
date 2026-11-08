# Civic Launcher (radio Android 7")

Launcher para radio Android de 7" (1024x600 horizontal) con tema Civic 2002 coupé gris, alerón y rines STR.

## Cómo generar el APK
1. Instala Android Studio.
2. File > Open > carpeta `CivicLauncher`. Espera el Gradle Sync.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. APK en `app/build/outputs/apk/debug/app-debug.apk`.
5. Pásalo al radio por USB/SD e instálalo (activa "orígenes desconocidos").
6. Presiona Home en el radio, elige "Civic Launcher" > "Siempre".

## Personalizar
- Colores: res/values/colors.xml
- Carro: res/drawable/civic_coupe.xml (puedes cambiarlo por un PNG de tu carro)
- Columnas de apps: GridLayoutManager(this, 4) en MainActivity.kt
