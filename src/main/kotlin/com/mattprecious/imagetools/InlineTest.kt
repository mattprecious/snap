package com.mattprecious.imagetools

/**
 * Under the right circumstances, inlining a function can result in measurable performance
 * improvements. Play around with the implementation of this test and see how the results are
 * affected.
 */
object InlineTest {
  private const val NUM_TESTS = 1_000_000
  private const val ITERATIONS_PER_TEST = 2_000_000

  @JvmStatic
  fun main(args: Array<String>) {
    var start: Long
    var end: Long
    var runningAverage = 0f

    for (testCount in 0..NUM_TESTS) {
      start = System.nanoTime()
      for (i in 0..ITERATIONS_PER_TEST) {
        // Calling a method multiple times dwarfs the overhead of the loop.
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
        doSomeMath(i)
      }
      end = System.nanoTime()

      runningAverage += (end - start) / NUM_TESTS.toFloat()
    }

    println("Normal average: $runningAverage")

    runningAverage = 0f

    for (testCount in 0..NUM_TESTS) {
      start = System.nanoTime()
      for (i in 0..ITERATIONS_PER_TEST) {
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
        doSomeMathInlined(i)
      }
      end = System.nanoTime()

      runningAverage += (end - start) / NUM_TESTS.toFloat()
    }

    println("Inlined average: $runningAverage")
  }

  private fun doSomeMath(x: Int) = x * 2

  @Suppress("NOTHING_TO_INLINE")
  private inline fun doSomeMathInlined(x: Int) = x * 2
}