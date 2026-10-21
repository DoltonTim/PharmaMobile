package pe.edu.upeu.pharmamobil.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguroIos
import pe.edu.upeu.pharmamobil.db.PharmaDb

actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
    single<AlmacenSeguro> { AlmacenSeguroIos() }
    single<SqlDriver> {
        NativeSqliteDriver(PharmaDb.Schema, "pharma.db")
    }
}
