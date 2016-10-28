package freedsl

object WhatIsAnInstruction {
    // instructions are a data structure which represent atomic parts of the DSL
    // all they have in common is an output type parameter!
    // most commonly, they are represented as an algebraic data type.
    trait InstructionSet[A]

    object Examples {
        object Logging {
            sealed abstract class LoggingOps[A] extends InstructionSet[A]
            final case class Debug(message: () => String) extends LoggingOps[Unit]
            final case class Info(message: () => String) extends LoggingOps[Unit]
            final case class Warning(message: () => String) extends LoggingOps[Unit]
            final case class Error(message: () => String, ex: Option[Exception]) extends LoggingOps[Unit]
        }

        object Storage {
            sealed abstract class StorageOps[A] extends InstructionSet[A]
            final case class Get(key: String) extends StorageOps[Option[String]]
            final case class Set(key: String, value: String) extends StorageOps[Unit]
            final case class Remove(key: String) extends StorageOps[Unit]
        }

        object Stock {
            trait StockMarket
            trait CompanySymbol

            sealed abstract class StockOps[A] extends InstructionSet[A]
            final case class Buy(market: StockMarket, symbol: CompanySymbol, price: BigDecimal) extends StockOps[Option[BigDecimal]]
            final case class Sell(market: StockMarket, symbol: CompanySymbol, price: BigDecimal) extends StockOps[Option[BigDecimal]]
        }

        object IO {
            final case class IO[A](execute: () => A) extends InstructionSet[A]
        }
    }
}
