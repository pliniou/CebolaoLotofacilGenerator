# Regras de ProGuard para o Cebolão Lotofácil Generator

# Preservar informações de linha para facilitar depuração
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Regras para o Room Database
-keep class com.example.cebolaolotofacilgenerator.data.model.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# Regras para o DataStore
-keep class androidx.datastore.**.** { *; }
-keep class com.example.cebolaolotofacilgenerator.data.preferences.** { *; }

# Regras para ViewModels
-keep class com.example.cebolaolotofacilgenerator.viewmodel.** { *; }

# Regras para classes de utilitários
-keep class com.example.cebolaolotofacilgenerator.util.** { *; }

# Manter construtores padrão para classes com anotações Kotlin
-keepclassmembers class * {
    <init>(...);    
}

# Mantenha as anotações kotlin.Metadata
-keepattributes *Annotation*,Signature,InnerClasses
-keep class kotlin.Metadata { *; }

# Para kotlinx.coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# Regras para Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt

# Regras para Jetpack Compose
-keep class androidx.compose.runtime.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclassmembers class * implements androidx.compose.runtime.Composer {
    <methods>;
}
-keepclassmembers class * implements androidx.compose.ui.tooling.preview.PreviewParameterProvider {
    <init>();
}
-keep class androidx.compose.ui.tooling.preview.** { *; }

# Regras para Navigation Component
-keepnames class androidx.navigation.fragment.NavHostFragment

# Regras para LiveData e ViewModel
-keep class androidx.lifecycle.** { *; }

# Evitar warnings comuns
-dontwarn org.jetbrains.annotations.**
-dontwarn kotlin.Unit
-dontwarn kotlin.reflect.**