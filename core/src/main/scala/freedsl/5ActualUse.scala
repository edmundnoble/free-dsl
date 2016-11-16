package freedsl

import org.atnos.eff._, Eff._, syntax.all._
import cats._, implicits._

object ActualUse {

  sealed abstract class LoggingOps[A]
  final case class Debug(message: () => String) extends LoggingOps[Unit]
  final case class Info(message: () => String) extends LoggingOps[Unit]
  final case class Warning(message: () => String) extends LoggingOps[Unit]
  final case class Error(message: () => String, ex: Option[Exception]) extends LoggingOps[Unit]

  sealed abstract class StorageOps[A] 
  final case class Get(key: String) extends StorageOps[Option[String]]
  final case class Set(key: String, value: String) extends StorageOps[Unit]
  final case class Remove(key: String) extends StorageOps[Unit]

  final case class IO[A](execute: () => A) 

  object SmartConstructors {
    type _storageOps[S] = MemberIn.|=[StorageOps, S]
    type _loggingOps[S] = MemberIn.|=[LoggingOps, S]
    type _io[S] = MemberIn.|=[IO, S]

    def get[S: _storageOps](key: String): Eff[S, Option[String]] = Get(key).send[S]
    def set[S: _storageOps](key: String, data: String): Eff[S, Unit] = Set(key, data).send[S]
    def remove[S: _storageOps](key: String): Eff[S, Unit] = Remove(key).send[S]
    def info[S: _loggingOps](out: => String): Eff[S, Unit] = Info(() => out).send[S]
    def io[S: _io, A](perform: () => A): Eff[S, A] = IO(perform).send[S]
  }

  type Fex = Fx.fx2[StorageOps, LoggingOps]

  import SmartConstructors._

  def removeKeyAtKey(key: String): Eff[Fex, Option[String]] = for {
    keyToRemove <- get[Fex](key)
    _ <- info[Fex]("removing key: " + keyToRemove)
    _ <- keyToRemove.traverseA(remove[Fex])
  } yield keyToRemove

}
