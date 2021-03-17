



    SAMPLE STREAM DATA
    ——————————————————

    Please write a program stream-sampler that receives and processes an input stream consisting of single characters.
    The stream is of unknown and possibly very large length and the program should work regardless of the size of the input.
    The program should take the sample size as a parameter and generate a random representative sample using that many
    characters. As for receiving the data the tool should work with two kinds of inputs:

        1. values piped directly into the process using
           $ stream-sampler 5 < input.txt

        2. values generated by using a random source from within the language

    Clean and maintainable code, efficiency, memory consumption, and good testing are all important criteria we would like
    you to keep in mind when completing the challenge. Try to keep the maximum allocated memory as low as feasible. Should
    something about the challenge be unclear please make an assumption and document it.


    Example
    ———————

        Given a sample size of 5 and the following stream of characters as values: THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG

        A possible random sample could be: EMETN


    Example execution and output for piped processing:

        $ dd if=/dev/urandom count=100 bs=1MB | base64 | stream-sampler 5
        Random Sample: EMETN
