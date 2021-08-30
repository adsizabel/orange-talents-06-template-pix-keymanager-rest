package br.com.zup.ot6.izabel.dto

import br.com.zup.ot6.izabel.ChavePixResponse


fun ChavePixResponse.paraDTO() : ChavePixResponseDTO {
        return ChavePixResponseDTO(
            pixId = this.pixId,
            clienteId = this.clienteId,
            tipoChave = this.tipoChave,
            valorChave = this.valorChave,
            tipoConta = this.tipoConta,
            dataCriacao = this.dataCriacao.paraLocalDateTime()
        )
    }
