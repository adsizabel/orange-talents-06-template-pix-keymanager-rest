package br.com.zup.ot6.izabel.grpc

import br.com.zup.ot6.izabel.GerenciadorChavePixGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class GerenciadorChavePixFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun gerenciadorChave() = GerenciadorChavePixGrpcServiceGrpc.newBlockingStub(channel)
}