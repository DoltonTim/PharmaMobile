package pe.edu.upeu.pharmamobile.DemoResultadoProductos

import pe.edu.upeu.pharmamobile.Result.ResultadoProductos

fun mostrarResultado(resultado: ResultadoProductos){
    when(resultado){

        // 2. Corregida la palabra "ResultadoProductos" (sin la 'n')
        ResultadoProductos.cargando -> {
            println("cargar productos")
        }

        is ResultadoProductos.Exito -> {
            println("Productos Encontrados: ${resultado.productos.size}")
        }

        is ResultadoProductos.Error -> {
            println("Hubo un Error: ${resultado.mensaje}")
        }
        else -> {
            println("Estado desconocido")
        }
    }
}