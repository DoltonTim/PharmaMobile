package pe.edu.upeu.pharmamobil.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.data.local.ProductoLocal
import pe.edu.upeu.pharmamobil.db.PharmaDb
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.crearHttpClient
import pe.edu.upeu.pharmamobil.data.repository.ClienteRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioOffline
import pe.edu.upeu.pharmamobil.domain.repository.ClienteRepository
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.ActualizarProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.EliminarProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ListarClientesUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ListarProductosUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarClienteUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobil.presentation.cliente.ClienteViewModel
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoViewModel

import pe.edu.upeu.pharmamobil.data.repository.AuthRepositorioRest
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobil.domain.usecase.CerrarSesionUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.IniciarSesionUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ObtenerSesionGuardadaUseCase
import pe.edu.upeu.pharmamobil.presentation.sesion.SesionViewModel

const val CATEGORIA_POR_DEFECTO: Long = 1L

val dataModule = module {
    single { PharmaDb(get()) }
    single { ProductoLocal(get()) }
    single<HttpClient> { crearHttpClient(get<HttpClientEngine>(), get<AlmacenSeguro>()) }
    single { ProductoApi(get(), CATEGORIA_POR_DEFECTO) }
    single<ProductoRepository> {
        ProductoRepositorioOffline(get(), get())
    }
    single<ClienteRepository> { ClienteRepositorioEnMemoria() }
    single<AuthRepository> { AuthRepositorioRest(get(), get()) }
}

val domainModule = module {
    factory { ListarProductosUseCase(get()) }
    factory { RegistrarProductoUseCase(get()) }
    factory { ActualizarProductoUseCase(get()) }
    factory { EliminarProductoUseCase(get()) }
    factory { RegistrarClienteUseCase(get()) }
    factory { ListarClientesUseCase(get()) }
    factory { IniciarSesionUseCase(get()) }
    factory { ObtenerSesionGuardadaUseCase(get()) }
    factory { CerrarSesionUseCase(get()) }
}

val presentationModule = module {
    viewModel { ProductoViewModel(get(), get(), get(), get(), get()) }
    viewModel { ClienteViewModel(get(), get()) }
    viewModel { SesionViewModel(get(), get(), get()) }
}

expect val platformModule: Module

fun initKoin(configuracionAdicional: KoinApplication.() -> Unit = {}) {
    startKoin {
        configuracionAdicional()
        modules(
            dataModule,
            domainModule,
            presentationModule,
            platformModule
        )
    }
}
