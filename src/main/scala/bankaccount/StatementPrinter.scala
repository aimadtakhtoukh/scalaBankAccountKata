package bankaccount

import java.io.PrintStream
import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

class StatementPrinter(val printStream : PrintStream = System.out) {
  import printStream._

  implicit class StringPadding(s: String) {
    def padded: String = s.padTo(12, " ").mkString
  }

  def printStatement(accountStatement: AccountStatement) : Unit = {
    println(" Deposits | Withdrawals |  Date  ")
    for(statement <- accountStatement.statements) {
      val deposit : String = Some(statement.amount).filter(_ >= 0).map(_.toString).getOrElse("").padded
      val withdrawal : String = Some(statement.amount).filter(_ < 0).map(_.toString).getOrElse("").padded
      val date : String =
        LocalDateTime.ofInstant(statement.date, ZoneId.systemDefault())
          .toLocalDate
          .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
          .padded
      println(s"$deposit$withdrawal$date")
    }
    println(s"Balance : ${accountStatement.balance}")
  }

}
