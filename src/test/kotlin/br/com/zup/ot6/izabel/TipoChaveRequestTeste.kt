package br.com.zup.ot6.izabel

import br.com.zup.ot6.izabel.dto.NovaChavePixRequest
import br.com.zup.ot6.izabel.dto.TipoChaveRequest
import net.bytebuddy.utility.RandomString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.IllegalArgumentException

class TipoChaveRequestTeste {

    @Nested
    inner class  ChaveAleatoriaTest{

        @Test
        fun `deve ser valido quando chave aleatoria for nula ou vazia`(){
            val tipoChave = TipoChaveRequest.ALEATORIA

            assertTrue(tipoChave.isValida(null))
            assertTrue(tipoChave.isValida(""))
        }

        @Test
        fun `nao deve ser valido quando chave aleatoria possuir um valor`(){
            val tipoChave = TipoChaveRequest.ALEATORIA
            var thrown = false

            try { tipoChave.isValida("12345abc") }
            catch (e: IllegalArgumentException){thrown = true}

            assertTrue(thrown)
        }

    }

    @Nested
    inner class CpfTest{

        @Test
        fun `deve validar formatacao cpf`(){
            val chaveCpf = TipoChaveRequest.CPF
            assertTrue(chaveCpf.isValida("05077216088"))
        }

        @Test
        fun `nao deve validar formatacao incorreta cpf`(){
            val chaveCpf = TipoChaveRequest.CPF
            var thrown = false

            try { chaveCpf.isValida("0507721608a") }
            catch (e: IllegalArgumentException){thrown = true}

            assertTrue(thrown)

        }
    }

    @Nested
    inner class TelefoneTest{

        @Test
        fun `deve cadastrar telefone valido`(){
            val tipoChave = TipoChaveRequest.TELEFONE
            assertTrue(tipoChave.isValida("+5583996583085"))
        }

        @Test
        fun `nao deve validar formatacao incorreta telefone`(){
            val tipoChave = TipoChaveRequest.TELEFONE
            var thrown = false

            try { tipoChave.isValida("83996583085") }
            catch (e: IllegalArgumentException){thrown = true}

            assertTrue(thrown)
        }

    }

    @Nested
    inner class EmailTest{
        @Test
        fun `deve cadastrar email valido`(){
            val tipoChave = TipoChaveRequest.EMAIL
            assertTrue(tipoChave.isValida("izabel@gmail.com"))
        }

        @Test
        fun `nao deve validar formatacao incorreta email`(){
            val tipoChave = TipoChaveRequest.EMAIL
            var thrown = false

            try { tipoChave.isValida("izabel.com.br") }
            catch (e: IllegalArgumentException){thrown = true}

            assertTrue(thrown)
        }
    }

    @Nested
    inner class QuantidadeDeCaracteresDaChave{
        /**
         * chave Pix não deve exceder 77 caracteres
         */

        @Test
        fun `deve validar quantidade de caracteres`(){
            var thrown = false
            var causa = ""
            var quantidadeDeCaracteres = 78
            val chave: String = RandomString.make(quantidadeDeCaracteres)

            try {
                val tipoChave = TipoChaveRequest.EMAIL
                assertTrue(tipoChave.isValida(chave))
            } catch (e: IllegalArgumentException){
                thrown = true
                causa = e.message.toString()
            }
            assertTrue(thrown)
            assertEquals("Tamanho maximo de 77 caracteres excedido para o campo chave.", causa)
        }
    }
}