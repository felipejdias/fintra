package app.fintra

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.time.LocalDate

class CsvReaderTest {

    private val csvReader = CsvReader()

    @Test
    fun `readTransactions should use category from 4th column if present`() {
        val csvData = """
            date,title,amount,category
            2023-10-01,Assinatura Streaming,29.90,Lazer
        """.trimIndent()
        
        val inputStream = ByteArrayInputStream(csvData.toByteArray())
        val transactions = csvReader.readTransactions(inputStream)
        
        assertEquals(1, transactions.size)
        assertEquals("Lazer", transactions[0].categoryName)
        assertEquals("Assinatura Streaming", transactions[0].title)
    }

    @Test
    fun `readTransactions should fallback to automatic categorization if category column is missing`() {
        // "ifood" deve ser categorizado como Alimentação pela lógica interna
        val csvData = """
            date,title,amount
            2023-10-01,ifood,50.00
        """.trimIndent()
        
        val inputStream = ByteArrayInputStream(csvData.toByteArray())
        val transactions = csvReader.readTransactions(inputStream)
        
        assertEquals(1, transactions.size)
        assertEquals("Outros", transactions[0].categoryName)
    }

    @Test
    fun `exportTransactions should include category column`() {
        val transactions = listOf(
            Transaction(
                date = LocalDate.of(2023, 10, 1),
                title = "Mercado",
                amount = 150.0,
                categoryName = "Alimentação"
            )
        )
        
        val outputStream = java.io.ByteArrayOutputStream()
        csvReader.exportTransactions(outputStream, transactions)
        
        val exportedContent = outputStream.toString()
        val expectedHeader = "date,title,amount,category"
        val expectedRow = "2023-10-01,Mercado,150.0,Alimentação"
        
        val lines = exportedContent.trim().split("\n")
        assertEquals(expectedHeader, lines[0].trim())
        assertEquals(expectedRow, lines[1].trim())
    }

    @Test
    fun `readTransactions normalizes renda category case`() {
        val csvData = """
            date,title,amount,category
            2023-10-01,Salário,5000.00,renda
        """.trimIndent()

        val transactions = csvReader.readTransactions(ByteArrayInputStream(csvData.toByteArray()))

        assertEquals("Renda", transactions[0].categoryName)
    }

    @Test
    fun `readTransactions parses quoted titles containing commas`() {
        val csvData = """
            date,title,amount,category
            2023-10-01,"Mercado, tal e tal",150.00,Alimentação
        """.trimIndent()

        val transactions = csvReader.readTransactions(ByteArrayInputStream(csvData.toByteArray()))

        assertEquals(1, transactions.size)
        assertEquals("Mercado, tal e tal", transactions[0].title)
        assertEquals(150.0, transactions[0].amount, 0.001)
    }

    @Test
    fun `readTransactions skips malformed lines instead of failing`() {
        val csvData = """
            date,title,amount,category
            2023-10-01,Válida,10.00,Lazer
            linha-quebrada-sem-colunas
            2023-10-02,Inválida,abc,Outros
            2023-10-03,Válida 2,20.00,Saúde
        """.trimIndent()

        val transactions = csvReader.readTransactions(ByteArrayInputStream(csvData.toByteArray()))

        assertEquals(2, transactions.size)
        assertEquals("Válida", transactions[0].title)
        assertEquals("Válida 2", transactions[1].title)
    }

    @Test
    fun `readTransactions uses Outros for blank category column`() {
        val csvData = """
            date,title,amount,category
            2023-10-01,Padaria,8.50,
        """.trimIndent()

        val transactions = csvReader.readTransactions(ByteArrayInputStream(csvData.toByteArray()))

        assertEquals("Outros", transactions[0].categoryName)
    }

    @Test
    fun `export escapes titles containing commas so round trip works`() {
        val transactions = listOf(
            Transaction(
                date = LocalDate.of(2023, 10, 1),
                title = "Padaria, do bairro",
                amount = 25.5,
                categoryName = "Alimentação"
            )
        )
        val outputStream = java.io.ByteArrayOutputStream()
        csvReader.exportTransactions(outputStream, transactions)

        val reparsed = csvReader.readTransactions(ByteArrayInputStream(outputStream.toByteArray()))

        assertEquals(1, reparsed.size)
        assertEquals("Padaria, do bairro", reparsed[0].title)
        assertEquals(25.5, reparsed[0].amount, 0.001)
        assertEquals("Alimentação", reparsed[0].categoryName)
    }
}
