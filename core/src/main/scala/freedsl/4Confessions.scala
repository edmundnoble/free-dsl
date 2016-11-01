package freedsl

object Confessions {
  // the returnValue and flatMap functions correspond to the return and bind operators on Haskell Monads
  // so to find out exactly what we're doing when we make a program, we'll look at
  // ways these operations can be grafted on to the instruction set ISet[_] to make it a program type

  // there is a technique for creating a Monad from a Functor and adding the minimal amount of structure required to do so.
  // it's called the Free monad, and one encoding of it goes as follows:
  sealed abstract class Free[F[_], A]
  final case class Pure[F[_], A](a: A) extends Free[F, A]
  final case class Inject[F[_], A](fa: F[A]) extends Free[F, A]
  abstract class FlatMap[F[_], A] extends Free[F, A] {
    type I
    val fa: Free[F, I]
    def fun(in: I): Free[F, A]
  }

  object Free {
    def pure[F[_], A](a: A): Free[F, A] = Pure(a)
  }

  // thus, the Program[ISet, A] type we came up with earlier is equivalent to Free[ISet, A],
  type Program[ISet[_], A] = Free[ISet, A]

  object Program {

    // where ISet represents the instruction set (plus an extra function, to make it a Functor),
    // Return is exactly ReturnValue and FlatMap is flatMap
    def programReturningProgram[F[_], A](f: Program[F, Program[F, A]]): Program[F, A] = new FlatMap[F, A] {
      override type I = Program[F, A]
      override val fa: Program[F, Program[F, A]] = f
      override def fun(p: Program[F, A]): Program[F, A] = p
    }

    def inject[ISet[_], A](instr: ISet[A]): Program[ISet, A] =
      Inject(instr)

    def map[ISet[_], A, B](program: Program[ISet, A])(f: A => B): Program[ISet, B] = new FlatMap[ISet, B] {
      override type I = A
      override val fa: Program[ISet, A] = program
      override def fun(a: A): Program[ISet, B] = Pure(f(a))

    }

    import cats.~>, cats.implicits._

    def interpret[ISet[_], E[_] : cats.Monad, A](program: Program[ISet, A])(trans: ISet ~> E): E[A] = program match {
      case Pure(a) => a.pure[E]
      case Inject(fa) => trans(fa)
      case fm: FlatMap[ISet, A] => interpret(fm.fa)(trans).flatMap(i => interpret(fm.fun(i))(trans))
    }

  }

}

