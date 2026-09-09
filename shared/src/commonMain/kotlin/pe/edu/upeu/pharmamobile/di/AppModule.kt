package pe.edu.upeu.pharmamobile.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.edu.upeu.pharmamobile.data.repository.ProductoRepositorioEnMemoria
import pe.edu.upeu.pharmamobile.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobile.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobile.presentation.producto.ProductoViewModel

val dataModule = module {
    single<ProductoRepository> { ProductoRepositorioEnMemoria() }
}

val domainModule = module {
    factory { RegistrarProductoUseCase(get()) }
}

val viewModelModule = module {
    viewModelOf(::ProductoViewModel)
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        dataModule,
        domainModule,
        viewModelModule,
        platformModule
    )
}
