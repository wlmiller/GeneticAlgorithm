Genetic Algorithm
=================

![Genetic Algorithm](GeneticAlgorithm.png?raw=true)

Simulates a population of simple "evolvers", which move around based on a set of deterministic rules.  The rules take as input the contents of the cell immediately in front of the evolver and the state of the evolver, which is an integer between 0 and 15.  The result of the rule is an action (move forward, turn left, turn right, or move backward) and a new state.

Every 365 steps (1 "year"), the evolvers evolve.  Their sets of rules are represented as arrays of (16*3*2) = 96 integers (there are three possible cell contents: empty, plant, or evolver).  These genomes are combined via crossover and mutated.

This simulation is based roughly on [this example](http://math.hws.edu/xJava/GA/); however, extensive enhancements are planned.

TODO:
- [ ] Add controls of system parameters.
- [ ] Add more direct competition between evolvers - i.e. attacking.
- [ ] Allow evolution of different populations in different environments, then compare performance when placed in the same environment.
