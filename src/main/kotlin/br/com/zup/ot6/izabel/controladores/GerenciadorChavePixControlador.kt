package br.com.zup.ot6.izabel.controladores

import br.com.zup.ot6.izabel.*
import br.com.zup.ot6.izabel.dto.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.Valid

@Controller("/api/v1/clientes/{clienteId}")
@Validated
class GerenciadorChavePixControlador(
    val grpcChavePixCliente: GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub
){

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post("/pix")
    fun registraChavePix(@PathVariable(value = "clienteId") clienteId: UUID,
                         @Valid @Body request: NovaChavePixRequest
    ) : HttpResponse<NovaChavePixResponse> {
        try{
            LOGGER.info("Inicio do cadastro de chave Pix para o cliente: $clienteId")
            val registraChavePixResponse = grpcChavePixCliente.cadastrarChavePix(request.paraModeloGrpc(clienteId))

            LOGGER.info("Chave Pix cadastrada com sucesso")
            return HttpResponse.created(location(clienteId.toString(), registraChavePixResponse.idChavePix))

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

            LOGGER.info("Chave Pix removida com sucesso.")
            return HttpResponse.accepted()

        }catch (ex: StatusRuntimeException){
            if (ex.status.code == Status.Code.NOT_FOUND){
                return HttpResponse.notFound()
            }
        }
        return HttpResponse.serverError()
    }

    @Get("/pix/{pixId}")
    fun detalharChavePix(clienteId: UUID, pixId: UUID): HttpResponse<Any>{

        LOGGER.info("Carregar chave Pix [$pixId] por ID. Cliente: [$clienteId]")

        val chaveResponse = grpcChavePixCliente.carregarChavePix(CarregaChavePixRequest.newBuilder()
                                                                    .setPixId(CarregaChavePixRequest.FiltroPorPixId.newBuilder()
                                                                        .setClienteId(clienteId.toString())
                                                                        .setPixId(pixId.toString())
                                                                        .build())
                                                                    .build())

        return HttpResponse.ok(DetalheChavePixResponse(chaveResponse))
    }

    @Get(value = "chaves")
    fun listarChavePix(@PathVariable(value = "clienteId") clienteId: String) : HttpResponse<List<ChavePixResponseDTO>>{
        try {
            val listarChavePixResponse = grpcChavePixCliente.listarChavePix(
                ListarChavePixRequest.newBuilder()
                    .setClienteId(clienteId)
                    .build()
            )

            val response = mutableListOf<ChavePixResponseDTO>()
            listarChavePixResponse.chavesPixList.map { chave ->
                response.add(chave.paraDTO())
            }

            return HttpResponse.ok(response)
        } catch (ex: Throwable){
            return HttpResponse.serverError()
        }
    }

    private fun location(clienteId: String, pixId: String) = HttpResponse.uri("/api/v1/clientes/$clienteId/pix/${pixId}")

}