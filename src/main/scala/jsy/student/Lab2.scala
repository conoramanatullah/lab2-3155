package jsy.student

object Lab2 extends jsy.util.JsyApplication {
  import jsy.lab2.Parser
  import jsy.lab2.ast._

  /*
   * CSCI 3155: Lab 2
   * Conor Amanatullah
   * 
   * Partner: Prayash Thapa
   * Collaborators: <Any Collaborators>
   */

  /*
   * Fill in the appropriate portions above by replacing things delimited
   * by '<'... '>'.
   * 
   * Replace 'YourIdentiKey' in the object name above with your IdentiKey.
   * 
   * Replace the '???' expression with  your code in each function.
   * 
   * Do not make other modifications to this template, such as
   * - adding "extends App" or "extends Application" to your Lab object,
   * - adding a "main" method, and
   * - leaving any failing asserts.
   * 
   * Your lab will not be graded if it does not compile.
   * 
   * This template compiles without error. Before you submit comment out any
   * code that does not compile or causes a failing assert. Simply put in a
   * '???' as needed to get something  that compiles without error. The ???
   * is a Scala expression that throws the exception scala.NotImplementedError.
   *
   */

  /* We represent a variable environment as a map from a string of the
   * variable name to the value to which it is bound.
   * 
   * You may use the following provided helper functions to manipulate
   * environments, which are just thin wrappers around the Map type
   * in the Scala standard library.  You can use the Scala standard
   * library directly, but these are the only interfaces that you
   * need.
   */

  type Env = Map[String, Expr]
  val emp: Env = Map()
  def get(env: Env, x: String): Expr = env(x)
  def extend(env: Env, x: String, v: Expr): Env = {
    require(isValue(v))
    env + (x -> v)
  }

  /* Some useful Scala methods for working with Scala values include:
   * - Double.NaN
   * - s.toDouble (for s: String)
   * - n.isNaN (for n: Double)
   * - n.isWhole (for n: Double)
   * - s (for n: Double)
   * - s format n (for s: String [a format string like for printf], n: Double)
   */

  def toNumber(v: Expr): Double = {
    require(isValue(v))
    (v: @unchecked) match {
      case N(n) => n
      case B(b) => if(b) 1 else 0
      case S(s) => s.toDouble
      case _ => throw new UnsupportedOperationException
    }
  }

  def toBoolean(v: Expr): Boolean = {
    require(isValue(v))
    (v: @unchecked) match {
      case B(b) => b
      case N(0) => false
      case N(n) => if(n.isNaN())false else true
      case Undefined => false
//        Empty String Case
      case S("") => false
      case S(_) => true
      case _ => throw new UnsupportedOperationException
    }
  }

  def toStr(v: Expr): String = {
    require(isValue(v))
    (v: @unchecked) match {
      case S(s) => s
      case B(b) => if(b)"true" else "false"
      case N(n) => n.toString
      case Undefined => "undefined"
      case _ => throw new UnsupportedOperationException
    }
  }

  def eval(env: Env, e: Expr): Expr = {
    /* Some helper functions for convenience. */
    def eToVal(e: Expr): Expr = eval(env, e)

    e match {
      /* Base Cases */
      case Unary(uop, e1) => uop match {
//          Neg means to negate the number so we want a num back so we send toNumber our value and simply negate it
        case Neg => N(-toNumber(eToVal(e1)))
//          toBoolean returns a boolean value so we not it to get the not.
        case Not => B(!toBoolean(e1))

        case _ => throw new UnsupportedOperationException
      }
      case Binary(bop, e1, e2) => bop match {
//      Plus can have multiple things in it so we need to convert?
        case Plus => N(toNumber(eToVal(e1))+toNumber(eToVal(e2)))
//          Simply Subtract
        case Minus => N(toNumber(e1)-toNumber(e2))
//          Multiply
        case Times => N(toNumber(e1)*toNumber(e2))
//          Divide
        case Div => N(toNumber(e1)/toNumber(e2))
//          Eq should return a Boolean
        case Eq => B(eToVal(e1) == eToVal(e2))
//          Ne should return a Boolean
        case Ne => B(eToVal(e1) != eToVal(e2))
//          Less Than will return a boolean based on two cases
//          EZ PZZZZ
        case Lt => B(toNumber(e1) < toNumber(e2))
        case Le => B(toNumber(e1) <= toNumber(e2))
        case Gt => B(toNumber(e1) > toNumber(e2))
        case Ge => B(toNumber(e1) >= toNumber(e2))
//
        case And => (eToVal(e1), eToVal(e2)) match {
//          case (S(_), S(_)) => ???
//            for and we only have to look at the first value
          case (N(_), B(_)) => N(toNumber(e1))
          case (B(_), N(_)) => B(toBoolean(e1))
          case ((_),(_)) => B(toBoolean(e1) && toBoolean(e2))
        }
        case Or => (eToVal(e1), eToVal(e2)) match {
          case _ => ???
        }

        case Seq => ???

        case _ => throw new UnsupportedOperationException

      }










      /* Inductive Cases */
      case Print(e1) => println(pretty(eToVal(e1))); Undefined

      case _ => e
    }
  }

  // Interface to run your interpreter starting from an empty environment.
  def eval(e: Expr): Expr = eval(emp, e)

  // Interface to run your interpreter from a string.  This is convenient
  // for unit testing.
  def eval(s: String): Expr = eval(Parser.parse(s))

  /* Interface to run your interpreter from the command-line.  You can ignore what's below. */
  def processFile(file: java.io.File) {
    if (debug) { println("Parsing ...") }

    val expr = Parser.parseFile(file)

    if (debug) {
      println("\nExpression AST:\n  " + expr)
      println("------------------------------------------------------------")
    }

    if (debug) { println("Evaluating ...") }

    val v = eval(expr)

     println(pretty(v))
  }

}
