package br.com.zup.ot6.izabel.dto

import br.com.zup.ot6.izabel.CadastrarChavePixRequest
import br.com.zup.ot6.izabel.TipoChavePix
import br.com.zup.ot6.izabel.TipoConta
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

val TAMANHO_MAXIMO_CHAVE = 77
private const val MENSAGEM_DE_ERRO = "A chave do tipo %s deve respeitar o padrão: %s."

@Introspected
data class NovaChavePixRequest(
    @JsonProperty("tipoChave")
    @field:NotBlank
    @field:Size(max=77)
    val tipoChave: TipoChaveRequest,
    @JsonProperty("valorChave")
    @field:NotBlank
    val valorChave: String,
    @JsonProperty("tipoConta")
    @field:NotBlank
    val tipoConta: TipoContaRequest
    ){

    fun paraModeloGrpc(idCliente: UUID): CadastrarChavePixRequest {
        return CadastrarChavePixRequest.newBuilder()
            .setIdCliente(idCliente.toString())
            .setTipoChavePix(tipoChave.atributoGrpc ?: TipoChavePix.CHAVE_DESCONHECIDA)
            .setChavePix(valorChave ?: "")
            .setTipoConta(tipoConta.atributoGrpc ?: TipoConta.TIPO_DESCONHECIDO)
            .build()
    }
}

enum class TipoChaveRequest(val atributoGrpc: TipoChavePix) {

    CPF(TipoChavePix.CPF){
        override fun isValida(chave: String?): Boolean {
            if(chave?.let { isTamanhoValido(it) } == true && chave.matches("^[0-9]{11}\$".toRegex())) return true
            throw IllegalArgumentException(MENSAGEM_DE_ERRO.format(CPF.name, "12345678901"))
        }
    },
    EMAIL(TipoChavePix.EMAIL){
        override fun isValida(chave: String?): Boolean {
            val pattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            if (chave?.let { TipoChaveRequest.isTamanhoValido(it) } == true && chave.matches(pattern.toRegex())) return true
            throw IllegalArgumentException(MENSAGEM_DE_ERRO.format(name, "exemplo@email.com"))
        }

    },

    TELEFONE(TipoChavePix.TELEFONE){
        override fun isValida(chave: String?): Boolean {
            if(chave?.let { isTamanhoValido(it) } == true && chave.matches("^\\+[0-9][0-9]\\d{1,14}\$".toRegex())) return true
            throw IllegalArgumentException(MENSAGEM_DE_ERRO.format(TELEFONE.name, "+5585988714077"))
        }
    },

    ALEATORIA(TipoChavePix.CHAVE_ALEATORIA){
        override fun isValida(chave: String?): Boolean {
            if(chave.isNullOrEmpty()) return true
            throw IllegalArgumentException(MENSAGEM_DE_ERRO.format(TipoChaveRequest.ALEATORIA.name, "SER UMA STRING VAZIA"))
        }

    };

    abstract fun isValida(chave: String?): Boolean

    companion object{
        fun isTamanhoValido(chave: String): Boolean{
            if(chave.isNullOrBlank()) throw IllegalArgumentException("O campo chave não pode ser nulo ou vazio.")
            if(chave.length > TAMANHO_MAXIMO_CHAVE)
                throw IllegalArgumentException("Tamanho maximo de $TAMANHO_MAXIMO_CHAVE caracteres excedido para o campo chave.")
            return true
        }
    }
}

enum class TipoContaRequest(val atributoGrpc: TipoConta){
    CONTA_CORRENTE(TipoConta.CONTA_CORRENTE),
    CONTA_POUPANCA(TipoConta.CONTA_POUPANCA)
}