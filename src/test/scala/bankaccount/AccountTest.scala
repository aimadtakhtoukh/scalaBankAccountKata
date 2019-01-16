package bankaccount

import org.scalatest.FlatSpec

class AccountTest extends FlatSpec {

  "An account" should "be empty on initialization" in {
    val account = new Account()
    assert(account.balance == 0)
    assert(account.statement.statements == Nil)
  }

  it should "increase its balance when making a deposit" in {
    val amount = 1
    val account = new Account().addOperation(BankingOperation(amount)).toOption
    assert(account.isDefined)
    assert(account.map(_.balance).contains(amount))
  }

  it should "fail if the withdrawal is over the current balance" in {
    val amount = -1
    val account = new Account().addOperation(BankingOperation(amount)).toOption
    assert(account.isEmpty)
  }

  it should "be empty if the account is deposited then withdrawn for the same amount" in {
    val amount = 1
    val account = new Account().addOperation(BankingOperation(amount)).flatMap(_.addOperation(BankingOperation(-amount))).toOption
    assert(account.isDefined)
    assert(account.map(_.balance).contains(0))
    assert(account.map(_.statement).map(_.statements).map(_.size).contains(2))
  }

}
