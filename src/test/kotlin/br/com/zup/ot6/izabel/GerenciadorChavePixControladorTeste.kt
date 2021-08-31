package br.com.zup.ot6.izabel

import br.com.zup.ot6.izabel.dto.NovaChavePixRequest
import br.com.zup.ot6.izabel.dto.TipoChaveRequest
import br.com.zup.ot6.izabel.dto.TipoContaRequest
import br.com.zup.ot6.izabel.grpc.GerenciadorChavePixFactory
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpRequest.DELETE
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@MicronautTest
class GerenciadorChavePixControladorTeste {

    @field:Inject
    lateinit var gerenciadorStub: GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var cliente: HttpClient

    val CHAVE_EMAIL = "izabel@gmail.com"
    val CHAVE_TELEFONE = "+5583123412345"
    val CONTA_CORRENTE = TipoConta.CONTA_CORRENTE
    val TIPO_CHAVE_EMAIL = TipoChavePix.EMAIL
    val TIPO_CHAVE_CELULAR = TipoChavePix.TELEFONE
    val INSTITUICAO = "Itau"
    val TITULAR = "Izabel Silva"
    val CPF = "28416317020"
    val AGENCIA = "0001"
    val NUMERO_CONTA = "1010-1"
    val CRIADA_EM = LocalDateTime.now()

    @Test
    internal fun `deve registrar uma chave pix`(){
        val clieteId = UUID.randomUUID()
        val pixID = UUID.randomUUID()

        val respostaGrpc = CadastrarChavePixResponse.newBuilder()
            .setIdCliente(clieteId.toString())
            .setIdChavePix(pixID.toString())
            .build()

        given(gerenciadorStub.cadastrarChavePix(Mockito.any())).willReturn(respostaGrpc)

        val novaChavePix = NovaChavePixRequest(
            tipoConta = TipoContaRequest.CONTA_CORRENTE,
            tipoChave = TipoChaveRequest.EMAIL,
            valorChave = "izabel@gmail.com")

        val request = POST("/api/v1/clientes/$clieteId/pix", novaChavePix)
        val response = cliente.toBlocking().exchange(request, NovaChavePixRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(pixID.toString()))

   }

    @Test
    internal fun `deve remover uma chave pix`(){
        val clieteId = UUID.randomUUID()
        val pixID = UUID.randomUUID()

        val respostaGrpc = RemoverChavePixResponse.newBuilder()
            .setClienteId(clieteId.toString())
            .setPixId(pixID.toString())
            .build()

        given(gerenciadorStub.removerChavePix(any())).willReturn(respostaGrpc)

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clieteId/pix/$pixID")
        val response = cliente.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.ACCEPTED, response.status)
    }

    @Test
    internal fun `deve carregar uma chave pix existente`(){
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        `when`(gerenciadorStub.carregarChavePix(any())).thenReturn(carregaChavePixResponse(clienteId, pixId))

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = this.cliente.toBlocking().exchange(request, Any::class.java)

        with(response){
            assertEquals(HttpStatus.OK, this.status)
            assertNotNull(this.body())
        }
    }

    @Test
    fun `deve listar todas as chaves Pix existentes`(){

        val clienteId = UUID.randomUUID()

        val responseGrpc = mapChavePixResponse(clienteId.toString())

        `when`(gerenciadorStub.listarChavePix(Mockito.any())).thenReturn(responseGrpc)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/${clienteId}/chaves/")
        val response = this.cliente.toBlocking().exchange(request, List::class.java)

        with(response){
            assertEquals(HttpStatus.OK, response.status)
            assertNotNull(response.body())
            assertEquals(2, response.body()!!.size )
        }

    }

    private fun carregaChavePixResponse(clientId: String, pixId: String): CarregaChavePixResponse {
        return CarregaChavePixResponse
            .newBuilder()
            .setClienteId(clientId)
            .setPixId(pixId)
            .setChave(
                CarregaChavePixResponse.ChavePix
                    .newBuilder()
                    .setTipo(TIPO_CHAVE_EMAIL)
                    .setChave(CHAVE_EMAIL)
                    .setConta(
                        CarregaChavePixResponse.ChavePix.ContaInfo
                            .newBuilder()
                            .setTipo(CONTA_CORRENTE)
                            .setInstituicao(INSTITUICAO)
                            .setTitular(TITULAR)
                            .setCpf(CPF)
                            .setAgencia(AGENCIA)
                            .setNumeroConta(NUMERO_CONTA)
                            .build()
                    )
                    .setCriadaEm(CRIADA_EM.let {
                        val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                        Timestamp.newBuilder()
                            .setSeconds(createdAt.epochSecond)
                            .setNanos(createdAt.nano)
                            .build()
                    })
                    .build()
            )
            .build()
    }

    private fun mapChavePixResponse(clientId: String): ListarChavePixResponse {
        val chaveEmail = ChavePixResponse.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setClienteId(clientId)
            .setTipoChave(TIPO_CHAVE_EMAIL)
            .setValorChave(EMAIL)
            .setTipoConta(CONTA_CORRENTE)
            .setDataCriacao(CRIADA_EM.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()

        val chaveTelefone = ChavePixResponse.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setClienteId(clientId)
            .setTipoChave(TIPO_CHAVE_CELULAR)
            .setValorChave(CHAVE_TELEFONE)
            .setTipoConta(CONTA_CORRENTE)
            .setDataCriacao(CRIADA_EM.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()

        return ListarChavePixResponse.newBuilder()
            .addAllChavesPix(listOf(chaveEmail, chaveTelefone))
            .build()
    }



    companion object {
        val TIPO_CONTA = TipoConta.CONTA_CORRENTE
        val TIPO_EMAIL = TipoChavePix.EMAIL
        val TIPO_TELEFONE = TipoChavePix.TELEFONE
        val EMAIL = "izabel@gmail.com"
        val TELEFONE = "+5583123452345"
        val CRIADA_EM = LocalDateTime.now()
    }

}

@Factory
@Replaces(factory = GerenciadorChavePixFactory::class)
internal class MockitoStubFactory{
    @Singleton
    fun stubMock() = Mockito.mock(GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub::class.java)
}