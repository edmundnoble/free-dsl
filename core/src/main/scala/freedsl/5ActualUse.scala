package freedsl

import org.atnos.eff._, Eff._, syntax.all._
import cats._, implicits._
import WhatIsAnInstruction.Examples._, Storage._, Logging._, IO._

object ActualUse {

  object SmartConstructors {
    def get[S](key: String)(implicit ev: Member.<=[StorageOps, S]): Eff[S, Option[String]] = Get(key).send[S]
    def set[S](key: String, data: String)(implicit ev: Member.<=[StorageOps, S]): Eff[S, Unit] = Set(key, data).send[S]
    def remove[S](key: String)(implicit ev: Member.<=[StorageOps, S]): Eff[S, Unit] = Remove(key).send[S]
    def info[S](out: => String)(implicit ev: Member.<=[LoggingOps, S]): Eff[S, Unit] = Info(() => out).send[S]
    def io[S, A](perform: () => A)(implicit ev: Member.<=[IO, S]): Eff[S, A] = IO(perform).send[S]
  }

  type Fex = Fx.fx3[StorageOps, LoggingOps, IO]

  import SmartConstructors._

  def removeKeyAtKey(key: String): Eff[Fex, Option[String]] = for {
    keyToRemove <- get[Fex](key)
    _ <- info[Fex]("removing key: " + keyToRemove)
    _ <- keyToRemove.traverseA(remove[Fex])
  } yield keyToRemove

}
