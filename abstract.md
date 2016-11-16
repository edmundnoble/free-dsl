Purely Functional Programming with Freely-Generated Domain Specific Languages
=============================================================================

Dependency injection is an often-used technique in object-oriented programming 
to easily modify the behaviors of an object by providing it with objects it would 
have otherwise generated on its own, to increase modularity. Aspect-oriented 
programming is a related technique which adds additional behavior ("advice")
to existing code, aiming to address cross-cutting concerns which affect wide 
areas of an application without sacrificing modularity. Dependency injection
might not seem a common topic in functional programming, but application modularity 
is essential to functional programming in a practical setting. A natural analogue 
to dependency injection and aspect-oriented programming in functional 
programming comes from a surprising place, and offers superior modularity 
to either. The free monad F f for a type constructor (and domain-specific language 
instruction set) f provides a syntax tree with internal nodes as domain-specific 
language instructions, which in combination with coproduct functors, 
allow domain-specific languages to be composed and combined easily.
