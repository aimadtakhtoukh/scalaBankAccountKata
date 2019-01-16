package bankaccount

import java.time.Instant
import java.time.Instant.now

import scala.util.{Failure, Success, Try}

class OverdrawnException(s : String) extends Exception(s)

case class BankingOperation(amount : BigDecimal = 0.0, date : Instant = now())

case class AccountStatement(statements : List[BankingOperation] = Nil, balance : BigDecimal = 0.0)

class Account {

  private var operations : List[BankingOperation] = Nil

  def balance : BigDecimal = operations.map(_.amount).sum

  def isOverdrawing(statement: BankingOperation) : Boolean = (balance + statement.amount) < 0.0

  def addOperation(statement: BankingOperation) : Try[Account] = {
    if (isOverdrawing(statement)) {
      return Failure(new OverdrawnException(s"Trying to take ${statement.amount} when the balance is $balance"))
    }
    operations = (operations ::: statement :: Nil).sortBy(_.date)
    Success(this)
  }

  def statement : AccountStatement = AccountStatement(statements = operations, balance = balance)

}
