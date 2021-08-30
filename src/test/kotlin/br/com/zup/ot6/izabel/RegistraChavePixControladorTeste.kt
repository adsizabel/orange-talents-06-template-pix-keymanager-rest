//package br.com.zup.ot6.izabel
//
//import br.com.zup.ot6.izabel.dto.NovaChavePixRequest
//import br.com.zup.ot6.izabel.dto.TipoChaveRequest
//import br.com.zup.ot6.izabel.dto.TipoContaRequest
//import br.com.zup.ot6.izabel.grpc.GerenciadorChavePixFactory
//import io.micronaut.context.annotation.Factory
//import io.micronaut.context.annotation.Replaces
//import io.micronaut.http.HttpRequest
//import io.micronaut.http.HttpRequest.DELETE
//import io.micronaut.http.HttpRequest.POST
//import io.micronaut.http.HttpStatus
//import io.micronaut.http.client.HttpClient
//import io.micronaut.http.client.annotation.Client
//import io.micronaut.test.extensions.junit5.annotation.MicronautTest
//import jakarta.inject.Inject
//import jakarta.inject.Singleton
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Test
//import org.mockito.BDDMockito.any
//import org.mockito.BDDMockito.given
//import org.mockito.Mockito
//import java.util.*
//
//@MicronautTest
//class RegistraChavePixControladorTeste {
//
//    @field:Inject
//    lateinit var gerenciadorStub: GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub
//
//    @field:Inject
//    @field:Client("/")
//    lateinit var cliente: HttpClient
//
//    @Test
//    internal fun `deve registrar uma chave pix`(){
//        val clieteId = UUID.randomUUID()
//        val pixID = UUID.randomUUID()
//
//        val respostaGrpc = CadastrarChavePixResponse.newBuilder()
//            .setIdCliente(clieteId.toString())
//            .setIdChavePix(pixID.toString())
//            .build()
//
//        given(gerenciadorStub.cadastrarChavePix(Mockito.any())).willReturn(respostaGrpc)
//
//        val novaChavePix = NovaChavePixRequest(
//            tipoConta = TipoContaRequest.CONTA_CORRENTE,
//            tipoChave = TipoChaveRequest.EMAIL,
//            valorChave = "izabel@gmail.com")
//
//        val request = POST("/api/v1/clientes/$clieteId/pix", novaChavePix)
//        val response = cliente.toBlocking().exchange(request, NovaChavePixRequest::class.java)
//
//        assertEquals(HttpStatus.CREATED, response.status.code)
//        assertTrue(response.headers.contains("Location"))
//        assertTrue(response.header("Location")!!.contains(pixID.toString()))
//
//   }
//
//    @Test
//    internal fun `deve remover uma chave pix`(){
//        val clieteId = UUID.randomUUID()
//        val pixID = UUID.randomUUID()
//
//        val respostaGrpc = RemoverChavePixResponse.newBuilder()
//            .setClienteId(clieteId.toString())
//            .setPixId(pixID.toString())
//            .build()
//
//        given(gerenciadorStub.removerChavePix(any())).willReturn(respostaGrpc)
//
//        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clieteId/pix/$pixID")
//        val response = cliente.toBlocking().exchange(request, Any::class.java)
//
//        assertEquals(HttpStatus.OK, response.status.code)
//    }
//
//
//}
//
//@Factory
//@Replaces(factory = GerenciadorChavePixFactory::class)
//internal class MockitoStubFactory{
//    @Singleton
//    fun stubMock() = Mockito.mock(GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub::class.java)
//}