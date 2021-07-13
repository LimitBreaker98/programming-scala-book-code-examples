import java.math.BigInteger
import scala.math

1 to 10

1 until 10

val x = 1 until 10 by 3

x.foreach(element => println(element))

val y = 1 to 10 by 3

y.foreach(element => println(element))

(10 to 1 by -4).foreach(x => println(x))

('a' to 'z' by 4).foreach(println)

// Big int example

(BigInt(3) to BigInt(new BigInteger("100000000000000000000")) by BigInt(new BigInteger("10000000000000000000"))).foreach(println)