## Installation
For latest snapshot use

```scala
libraryDependencies += "@ORG@" % "@NAME@" % "@SNAPSHOT_VERSION@"
```

And for the latest stable release use
```scala
libraryDependencies += "@ORG@" % "@NAME@" % "@RELEASE_VERSION@"
```

For ease of useability, this project is cross compiled against @CROSS_VERSIONS@ 

## ProcessTimer Usage
The ProcessTimer annotation can be used to log runtime information about a method
```scala mdoc
import com.bbrownsound.macros._

@ProcessTimer
def testMethod[String]: Double = {
  val x = 2.0 + 2.0
  Math.pow(x, x)
}

@ProcessTimer
def methodWithArguments(a: Double, b: Double) = {
  val c = Math.pow(a, b)
  c > a + b
}

@ProcessTimer
def longRunning(n: Int) = {
  for (i <- 0 until n) yield Math.pow(i, i)
}
```

## StringObfuscation Usage
The StringObfuscation annotation can be used to obfuscate sensitive values by overwriting the `toString` method
```scala mdoc
import com.bbrownsound.macros.ToStringObfuscate

@ToStringObfuscate("password", "pinCode")
case class TestPassword(name: String, username: String, password: String, pinCode: String)

@ToStringObfuscate("cardNumber")
case class TestCreditCard(cardNumber: String, cvv: Int, endDate: String)

@ToStringObfuscate("password")
case class UserPassword(username: String, password: String)

@ToStringObfuscate("password", "pinCode")
case class TestUser(username: String, password: String, pinCode: Long)

case class NestedExample(user: TestUser)
```