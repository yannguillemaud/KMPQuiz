package ygmd.kmpquiz.ktor

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.routes() {
//    val repository: SavedQandaRepository by inject()

    install(StatusPages){
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Erreur inconnue")
        }
    }

    routing {
        get("/qandas") {
//            val internalQandas = repository.getQandas()
//            val dtos = internalQandas.map { it.toDto() }
//            call.respond(dtos)
        }

        post("/qandas") {
//            val qanda = call.receive<QandaDto>()
//            val internalQanda = qanda.toInternalQanda()
//            try {
//                repository.saveQanda(internalQanda)
//                call.respond(HttpStatusCode.Created, "QANDA saved successfully")
//            } catch (e: Exception) {
//                call.respond(HttpStatusCode.InternalServerError, "Failed to save QANDA: ${e.localizedMessage}")
//            }
        }

        delete("/qandas"){
//            try {
//                call.respond(HttpStatusCode.Created, "QANDA saved successfully")
//            } catch (e: Exception) {
//                call.respond(HttpStatusCode.InternalServerError, "Failed to save QANDA: ${e.localizedMessage}")
//            }
        }
    }
}
