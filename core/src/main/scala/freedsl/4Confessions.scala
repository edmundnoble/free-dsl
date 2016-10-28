package freedsl

object Confessions {
  // the map function corresponds to the fmap operator on Haskell Functors
  // the returnValue and flatten functions correspond to the return and join operators on Haskell Monads

  // so to find out exactly what we're doing when we make a program, we'll look at
  // ways these operations can be grafted on to the instruction set ISet[_] to make it a program type

  // thanks to the contravariant Yoneda lemma, we know a way to make ANY F[_] into a Functor
  trait Coyoneda[F[_], A] {
    type I

    val fi: F[I]

    def fun(i: I): A

    def map[B](f: A => B) = Coyoneda(fi, (i: I) => f(fun(i)))
  }
  // look familiar? this is precisely MapProgram from earlier, with F[_] set to
  // Program[ISet, _] for some fixed ISet[_].

  object Coyoneda {
    def apply[F[_], A, I0](fi0: F[I0], funFunc: I0 => A) = new Coyoneda[F, A] {
      override type I = I0
      override val fi: F[I0] = fi0
      override def fun(i: I): A = funFunc(i)
    }
    def returnValue[F[_], A](fa: F[A]) = new Coyoneda[F, A] {
      override type I = A
      override val fi: F[A] = fa
      override def fun(i: A): A = i
    }
  }

  // also, there is a technique for creating a Monad from a Functor and adding the minimal amount of structure required to do so.
  // it's called the Free monad, and one encoding of it goes as follows:
  sealed abstract class Free[F[_], A]
  final case class Return[F[_], A](a: A) extends Free[F, A]
  final case class Suspend[F[_], A](expand: F[Free[F, A]]) extends Free[F, A]

  object Free {
    def returnValue[F[_], A](a: A): Free[F, A] = Return(a)
    def suspend[F[_], A](expand: F[Free[F, A]]): Free[F, A] = Suspend[F, A](expand)
  }

  // thus, the Program[ISet, A] type we came up with earlier is equivalent to Free[Coyoneda[ISet, ?], A].
  type Program[ISet[_], A] = Free[Coyoneda[ISet, ?], A]

  object Program {

    // where ISet represents the instruction set (plus an extra function, to make it a Functor),
    // Return is exactly ReturnValue and Suspend is a very similar operation to ProgramReturningProgram:
    def programReturningProgram[F[_], A](f: Program[F, Program[F, A]]): Program[F, A] = f match {
      case Return(a) => a
      case Suspend(fa) =>
        Suspend[Coyoneda[F, ?], A](fa.map(programReturningProgram))
    }

    def inject[ISet[_], A](instr: ISet[A]): Program[ISet, A] =
      Free.suspend[Coyoneda[ISet, ?], A](Coyoneda(instr, Free.returnValue))

    def map[ISet[_], A, B](program: Program[ISet, A])(f: A => B): Program[ISet, B] =
      flatMap[ISet, A, B](program)(a => Free.returnValue(f(a)))

    def flatMap[ISet[_], A, B](program: Program[ISet, A])(f: A => Program[ISet, B]): Program[ISet, B] = program match {
      case Return(a) => f(a)
      case Suspend(ffree) =>
        Free.suspend[Coyoneda[ISet, ?], B](ffree.map(flatMap(_)(f)))
    }

  }

}

