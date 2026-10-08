package pe.edu.upeu.pharmamobil.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.platform.CompartidorIos

actual val platformModule: Module = module {
<<<<<<< Updated upstream
    // Sin dependencias exclusivas de iOS por ahora.
=======
    single<HttpClientEngine> { Darwin.create() }
    single<Compartidor> { CompartidorIos() }
>>>>>>> Stashed changes
}
