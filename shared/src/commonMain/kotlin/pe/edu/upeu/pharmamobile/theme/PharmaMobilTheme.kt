package pe.edu.upeu.pharmamobile.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ==========================================
// PASO 8: Optimización de Light Theme
// ==========================================
private val LightColors = lightColorScheme(
    primary = Color(0xFF0056D2),       // Botones claramente delimitados
    onPrimary = Color.White,           
    background = Color(0xFFF8F9FA),    // Fondos limpios (no blanco puro)
    onBackground = Color(0xFF1E1E1E),  // Textos de alto contraste
    surface = Color.White,             // Superficies claras
    onSurface = Color(0xFF1E1E1E)
)

// ==========================================
// PASO 9: Optimización de Dark Theme
// ==========================================
private val DarkColors = darkColorScheme(
    primary = Color(0xFF80B4FF),       
    onPrimary = Color(0xFF00296B),     
    background = Color(0xFF121212),    // Superficie oscura calibrada (no negro puro)
    onBackground = Color(0xFFE0E0E0),  // Evita inversión simple de blanco y negro
    surface = Color(0xFF1E1E1E),       // Preserva jerarquía visual (un tono más claro que el fondo)
    onSurface = Color(0xFFE0E0E0)
)

// ==========================================
// PASO 7: Identidad Visual Centralizada (Shapes y Typography)
// ==========================================

// Definición de Shapes (Formas de los componentes para evitar estilos aislados)
private val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),   // Bordes unificados para Cards y diálogos
    large = RoundedCornerShape(16.dp)    // Bordes unificados para botones grandes
)

// Definición de Typography (Tipografía unificada)
private val AppTypography = Typography(
    // Al instanciar Typography, Compose asigna los tamaños de letra recomendados 
    // por defecto para todos los títulos, cuerpos de texto y etiquetas.
)

@Composable
fun PharmaMobilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        shapes = AppShapes,         // Se aplica el Paso 7 (Shapes)
        typography = AppTypography, // Se aplica el Paso 7 (Typography)
        content = content
    )
}
