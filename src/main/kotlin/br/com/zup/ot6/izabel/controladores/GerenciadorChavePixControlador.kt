package br.com.zup.ot6.izabel.controladores

import br.com.zup.ot6.izabel.CadastrarChavePixRequest
import br.com.zup.ot6.izabel.GerenciadorChavePixGrpcServiceGrpc
import br.com.zup.ot6.izabel.RemoverChavePixRequest
import br.com.zup.ot6.izabel.dto.RegistraChavePixRequestDTO
import br.com.zup.ot6.izabel.dto.RegistraChavePixResponseDTO
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import org.slf4j.LoggerFactory

@Controller("/api/v1/clientes/{clienteId}")
class GerenciadorChavePixControlador(
    val grpcChavePixCliente: GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub
){

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post("/pix")
    fun registraChavePix(@PathVariable(value = "clienteId") clienteId: String,
                         @Body requestBody: RegistraChavePixRequestDTO
    ) : HttpResponse<RegistraChavePixResponseDTO> {
        try{
            val cadastraChavePixRequest = CadastrarChavePixRequest.newBuilder()
                .setIdCliente(clienteId)
                .setTipoChavePix(requestBody.tipoChave)
                .setChavePix(requestBody.valorChave)
                .setTipoConta(requestBody.tipoConta)
                .build()

            LOGGER.info("Inicio do cadastro de chave Pix para o cliente: $clienteId")
            val registraChavePixResponse = grpcChavePixCliente.cadastrarChavePix(cadastraChavePixRequest)

            return HttpResponse.created(location(clienteId, registraChavePixResponse.idChavePix))
            LOGGER.info("Chave Pix cadastrada com sucesso")

        } catch (ex: StatusRuntimeException){
            if (ex.status.code == Status.Code.ALREADY_EXISTS){
                return HttpResponse.unprocessableEntity()
            }
        }
        return HttpResponse.serverError()
    }

    @Delete("/pix/{pixId}")
    fun removerChavePix(@PathVariable(value = "clienteId") clienteId: String,
                        @PathVariable(value = "pixId") pixId: String): HttpResponse<Any>{

        LOGGER.info("Inicio da remoção de chave Pix para o cliente: $clienteId")

        try {
            grpcChavePixCliente.removerChavePix(
                RemoverChavePixRequest.newBuilder()
                    .setPixId(pixId)
                    .setClienteId(clienteId)
                    .build()
            )
            return HttpResponse.accepted()
            LOGGER.info("Chave Pix removida com sucesso.")
        }catch (ex: StatusRuntimeException){
            if (ex.status.code == Status.Code.NOT_FOUND){
                return HttpResponse.notFound()
            }
        }
        return HttpResponse.serverError()
    }

    private fun location(clienteId: String, pixId: String) = HttpResponse.uri("/api/v1/clientes/$clienteId/pix/${pixId}")

}