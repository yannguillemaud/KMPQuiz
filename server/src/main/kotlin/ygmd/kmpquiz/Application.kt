package ygmd.kmpquiz

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ygmd.kmpquiz.db.conf.DBFactory
import ygmd.kmpquiz.db.conf.SERVER_PORT
import ygmd.kmpquiz.koin.serverModule
import ygmd.kmpquiz.ktor.routes

fun main() {
    DBFactory.init()

    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0"){
        install(ContentNegotiation){ json() }
        install(Koin){
            slf4jLogger()
            modules(serverModule)
        }
        routes()
    }.start(wait = true)
}