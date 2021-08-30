package br.com.zup.ot6.izabel.dto

import br.com.zup.ot6.izabel.CarregaChavePixResponse
import br.com.zup.ot6.izabel.TipoConta
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Introspected
class DetalheChavePixResponse (chaveResponse: CarregaChavePixResponse){

    val pixId = chaveResponse.pixId
    val tipo = chaveResponse.chave.tipo
    val chave = chaveResponse.chave.chave

    val criadaEm = chaveResponse.chave.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }

    val tipoConta = when(chaveResponse.chave.conta.tipo){
        TipoConta.CONTA_CORRENTE -> "CONTA_CORRENTE"
        TipoConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
        else -> "NAO_RECONHECIDA"
    }

    val conta = mapOf(Pair("tipo", tipoConta),
        Pair("instituicao", chaveResponse.chave.conta.instituicao),
        Pair("titular", chaveResponse.chave.conta.titular),
        Pair("cpf", chaveResponse.chave.conta.cpf),
        Pair("agencia", chaveResponse.chave.conta.agencia),
        Pair("numeroConta", chaveResponse.chave.conta.numeroConta))

}