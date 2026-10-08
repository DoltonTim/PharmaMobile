package pe.edu.upeu.pharmamobil.di

<<<<<<< Updated upstream
=======
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
>>>>>>> Stashed changes
import org.koin.core.module.Module
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.platform.CompartidorAndroid

actual val platformModule: Module = module {
<<<<<<< Updated upstream
    // Sin dependencias exclusivas de Android por ahora.
=======
    single<HttpClientEngine> { OkHttp.create() }
    single<Compartidor> { CompartidorAndroid(androidContext()) }
>>>>>>> Stashed changes
}
