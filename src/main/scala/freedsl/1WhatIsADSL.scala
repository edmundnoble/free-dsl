package freedsl
// traits are used here as specifications of the operations available on types

object WhatIsADSL {
    // a plain DSL: programs made of atomic instructions
    trait NotNecessarilyEmbeddedDSL {
        type Program
        type InstructionSet
    }

    // a DSL which contains the semantics of the host language
    trait DeeplyEmbeddedDSL {
        // the type of programs in this DSL that yield a value of type A
        type Program[A]
        // atomic unit that makes up programs in this DSL
        type InstructionSet[A]
        // make a program that just returns a value
        def returnValue[A](a: A): Program[A]

        // make a program consisting solely of a single instruction
        def singleInstruction[A](instruction: InstructionSet[A]): Program[A]

        // make a program with a normal function mapped over the output of another program
        def mapProgram[A, B](program: Program[A], f: A => B): Program[B]

        // make a new program which executes `nestedProgram`, 
        // then executes the program returned by it and returns its output
        // (can be implemented as: flatMapProgram[Program[A], A](p => p))
        def flattenProgram[A](nestedProgram: Program[Program[A]]): Program[A]

        // make a new program which executes `program`, then applies f to the output and executes f's output
        // (can be implemented as: flattenProgram[B](mapProgram[A, Program[B]](program, f)))
        def flatMapProgram[A, B](program: Program[A], f: A => Program[B]): Program[B]
    }
}
