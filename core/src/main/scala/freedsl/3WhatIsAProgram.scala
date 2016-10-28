package freedsl

object WhatIsAProgram {

    // removing flatMap from the specification of programs in favor of flatten, 
    // programs constructed with an instruction set need to at minimum contain:
    // - functions mapped over other programs
    // - instructions from the instruction set
    // - returned values
    // - programs returning other programs

    // thus, this will be our encoding of a program.
    sealed abstract class Program[ISet[_], A]

    final case class MapProgram[ISet[_], A, I](program: Program[ISet, I], function: I => A) 
        extends Program[ISet, A]

    final case class InjectInstruction[ISet[_], A](instruction: ISet[A]) 
        extends Program[ISet, A]

    final case class ReturnValue[ISet[_], A](value: A) 
        extends Program[ISet, A]

    final case class ProgramReturningProgram[ISet[_], A](program: Program[ISet, Program[ISet, A]]) 
        extends Program[ISet, A]

    object Program {
        def returnValue[ISet[_], A](value: A): Program[ISet, A] = ReturnValue[ISet, A](value)
        def inject[ISet[_], A](instr: ISet[A]): Program[ISet, A] = InjectInstruction[ISet, A](instr)
    }

    implicit class ProgramOps[ISet[_], A](val program: Program[ISet, A]) extends AnyVal {
        def map[B](f: A => B): Program[ISet, B] = 
            MapProgram[ISet, B, A](program, f)
        def flatMap[B](f: A => Program[ISet, B]): Program[ISet, B] = 
            ProgramReturningProgram[ISet, B](MapProgram[ISet, Program[ISet, B], A](program, f))
    }

    object Why {
        import scala.util.{Either, Right, Left}
        import WhatIsAnInstruction.Examples._
        import Logging._, Storage._
        import Program._

        final case class EitherInstruction[L[_], R[_], A](run: Either[L[A], R[A]])

        implicit class eitherInstructionOps[ISet[_], A](val instruction: ISet[A]){
            def left[R[_]]: EitherInstruction[ISet, R, A] = EitherInstruction(Left[ISet[A], R[A]](instruction))
            def right[L[_]]: EitherInstruction[L, ISet, A] = EitherInstruction(Right[L[A], ISet[A]](instruction))
        }

        type LoggingAndStorageOps[A] = EitherInstruction[LoggingOps, StorageOps, A]

        def logValueAtKey(key: String): Program[LoggingAndStorageOps, Unit] = for {
            storedValue <- inject[LoggingAndStorageOps, Option[String]](Storage.Get(key).right)
            _ <- inject[LoggingAndStorageOps, Unit](Logging.Info(() => s"value at $key: ${storedValue.toString}").left)
        } yield ()
        
    }

}
