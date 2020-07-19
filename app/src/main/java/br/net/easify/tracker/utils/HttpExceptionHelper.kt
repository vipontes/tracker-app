package br.net.easify.tracker.utils


import br.net.easify.tracker.model.ErrorResponse
import com.google.gson.JsonParser
import retrofit2.HttpException

class HttpExceptionHelper {

    companion object {
        fun parseHttpExceptionToErrorResponse(e: Throwable): ErrorResponse {

            var errorResponse = ErrorResponse("")

            if (e is HttpException) {

                val errorJsonString = e.response().errorBody()?.string()
                val message = JsonParser().parse(errorJsonString)
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