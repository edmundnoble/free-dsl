package freedsl
// traits are used here as specifications of the operations available on types

object WhatIsADSL {

    // a plain DSL: programs made of atomic and non-atomic instructions
    trait NotNecessarilyEmbeddedDSL {
        type Program
    }

    // a DSL which contains the semantics of the host language
    trait DeeplyEmbeddedDSL {

        // 1. the type of programs in this DSL that yield a value of type A
        type Program[A]

        // 2. atomic unit that makes up programs in this DSL
        type InstructionSet[A]

        // 3. program that just returns a value
        def returnValue[A](a: A): Program[A]

        // 4. program consisting solely of a single instruction
        def singleInstruction[A](instruction: InstructionSet[A]): Program[A]

        // 5. program with a normal function mapped over the output of another program
        // (can be implemented as: flatMapProgram[A, B](program, (a: A) => returnValue[Program[B]](f(a))))
        def mapProgram[A, B](program: Program[A], f: A => B): Program[B]

        // 6. program which executes `nestedProgram`, 
        // then executes the program returned by it and returns its output
        // (can be implemented as: flatMapProgram[Program[A], A](p => p))
        def flattenProgram[A](nestedProgram: Program[Program[A]]): Program[A]

        // 7. program which returns the output of (`f` applied to the output of `program`)
        // (can be implemented as: flattenProgram[B](mapProgram[A, Program[B]](program, f)))
        def flatMapProgram[A, B](program: Program[A], f: A => Program[B]): Program[B]
    }
}
