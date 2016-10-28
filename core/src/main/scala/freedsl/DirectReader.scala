package freedsl
object DirectReader {
    object Reader {
        def ask[E]: Reader[E, E] = (e: E) => e
        def pure[E, A](a: A): Reader[E, A] = (_: E) => a
    }

    type Reader[E, A] = E => A

    implicit class ReaderOps[E, A](val r: Reader[E, A]) extends AnyVal {
        def flatMap[B](f: A => Reader[E, B]): Reader[E, B] = (e: E) => f(r(e))(e)
        def map[B](f: A => B): Reader[E, B] = (e: E) => f(r(e))
    }

}


