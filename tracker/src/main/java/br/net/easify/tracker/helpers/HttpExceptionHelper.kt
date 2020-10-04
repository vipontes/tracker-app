package br.net.easify.tracker.helpers


import br.net.easify.tracker.model.Response
import com.google.gson.JsonParser
import retrofit2.HttpException

class HttpExceptionHelper {

    companion object {
        fun parseHttpExceptionToErrorResponse(e: Throwable): Response {

            var errorResponse = Response("")

            if (e is HttpException) {

                val errorJsonString = e.response().errorBody()?.string()
                val message = JsonParser.parseString(errorJsonString!!)
                    .asJsonObject["message"]
                    .asString

                errorResponse.message = message;

            } else {
                errorResponse.message = "Erro desconhecido."
            }

            return errorResponse

        }
    }

}