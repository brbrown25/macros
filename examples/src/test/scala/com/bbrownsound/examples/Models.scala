package com.bbrownsound.examples

import com.bbrownsound.macros.ToStringObfuscate

object Obfuscated {
  @ToStringObfuscate("password", "pinCode")
  case class TestPassword(name: String, username: String, password: String, pinCode: String)

  @ToStringObfuscate("cardNumber")
  case class TestCreditCard(cardNumber: String, cvv: Int, endDate: String)

  @ToStringObfuscate("password")
  case class UserPassword(username: String, password: String)

  @ToStringObfuscate("password", "pinCode")
  case class TestUser(username: String, password: String, pinCode: Long)

  case class NestedExample(user: TestUser)
}

object NonObfuscated {
  case class TestPassword(name: String, username: String, password: String, pinCode: String)

  case class TestCreditCard(cardNumber: String, cvv: Int, endDate: String)

  case class UserPassword(username: String, password: String)

  case class TestUser(username: String, password: String, pinCode: Long)

  case class NestedExample(user: TestUser)
}
