package bankaccount

import java.io.{ByteArrayOutputStream, PrintStream}
import java.nio.charset.StandardCharsets
import java.time.Instant

import org.scalatest.FlatSpec

import scala.util.Success

class StatementPrinterTest extends FlatSpec {

  "A printer" should "print empty data with new account" in {
    val baos = new ByteArrayOutputStream()
    val printStream = new PrintStream(baos, true, "UTF-8")

    val account = new Account()
    new StatementPrinter(printStream).printStatement(account.statement)

    val data = new String(baos.toByteArray, StandardCharsets.UTF_8)
    assert(data.equals(
      " Deposits | Withdrawals |  Date  \r\n" +
      "Balance : 0\r\n"
    ))
  }

  it should "show deposits and withdrawals" in {
    val baos = new ByteArrayOutputStream()
    val printStream = new PrintStream(baos, true, "UTF-8")

    val account = Success(new Account())
      .flatMap(_.addOperation(BankingOperation(amount =  200,    date = Instant.parse("2019-01-16T00:00:00.00Z"))))
      .flatMap(_.addOperation(BankingOperation(amount = -100.50, date = Instant.parse("2019-01-16T03:00:00.00Z"))))
      .toOption
    new StatementPrinter(printStream).printStatement(account.map(_.statement).orNull)

    val data = new String(baos.toByteArray, StandardCharsets.UTF_8)
    assert(data.equals(
      " Deposits | Withdrawals |  Date  \r\n" +
      "200                     16-01-2019  \r\n" +
      "            -100.5      16-01-2019  \r\n" +
      "Balance : 99.5\r\n"
    ))
  }

  it should "show deposits sorted by date" in {
    val baos = new ByteArrayOutputStream()
    val printStream = new PrintStream(baos, true, "UTF-8")

    val account = Success(new Account())
      .flatMap(_.addOperation(BankingOperation(amount =  150, date = Instant.parse("2019-01-16T00:00:00.00Z"))))
      .flatMap(_.addOperation(BankingOperation(amount =   50, date = Instant.parse("2019-01-14T00:00:00.00Z"))))
      .toOption
    new StatementPrinter(printStream).printStatement(account.map(_.statement).orNull)

    val data = new String(baos.toByteArray, StandardCharsets.UTF_8)
    assert(data.equals(
      " Deposits | Withdrawals |  Date  \r\n" +
        "50                      14-01-2019  \r\n" +
        "150                     16-01-2019  \r\n" +
        "Balance : 200\r\n"
    ))
  }

}
