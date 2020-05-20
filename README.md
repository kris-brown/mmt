# Mathematical Knowledge Representation with MMT

## Objective 

Given some knowledge formalized in [MMT](https://gl.mathhub.info/Teaching/KRMT/blob/master/source/tutorial/mmt-math-tutorial.pdf) format (the contents of which are not in this repository), this project aims to provide a higher-level interface for querying the knowledge graph. 

We want to answer questions like:
- What types of mathematical theories can (partially) give me insight into *this* problem?
- What types of real world domains can be (partially) modeled with *this* theory? 
- Given a solution to some problem represented with some theory, can I solve that problem within another theory (perhaps one less general yet easier to compute in). 

## Usage

Firstly, in the directory with the MMT documents (containing a `build-omdoc.msl` file), run the following commands in the MMT shell
```$xslt
mathpath archive MMT_archive_directory
file path/to/build-omdoc.msl
```

This should start a localhost server.

Then I can run `src/main/scala/com/example/mmtapi/Test.scala` in the IntelliJ IDE (in debug mode, it doesn't seem to work if I just "run").

## Examples
None! 

## To Do
- Implement basic functionality, just getting started with this.
