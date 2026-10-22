package pe.edu.upeu.pharmamobil.util

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun tiempoActualMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
