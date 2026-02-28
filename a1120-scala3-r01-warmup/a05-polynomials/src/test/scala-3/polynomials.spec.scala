/*
* 
* This file is part of the CS-A1120 Programming 2 course materials at
* Aalto University in Spring 2026, and is for your personal use on that
* course only.
* Distribution of any parts of this file in any form, including posting or
* sharing on public or shared forums or repositories is *prohibited* and
* constitutes a violation of the code of conduct of the course.
* The programming exercises of CS-A1120 are individual and confidential
* assignments---this means that as a student taking the course you are
* allowed to individually and confidentially work with the material,
* to discuss and review the material with course staff, and submit the
* material for grading on course infrastructure.
* All other use - including, having other persons or programs
* (e.g. AI/LLM tools) working on or solving the exercises for you is
* forbidden, and constitutes a violation of the code of conduct of this
* course.
* 
*/


package polynomials

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class PolynomialsSpec extends AnyFlatSpec with Matchers:

    "Variable" should "instansiate and evaluate correctly" in {
        val x = new Variable("x")
        for x0 <- -10 to 10 do
            withClue(s"For constant value $x0") {
                x.set(x0)
                x0 shouldBe x.value
            }
        end for
    }
    "Constant" should "instansiate and evaluate correctly" in {
        for x0 <- -10 to 10 do
            val c = new Constant(x0)
            x0 shouldBe c.value
        end for
    }

    "Sum" should "instansiate and evaluate correctly" in {
        val x = new Variable("x")
        val c0 = 2
        val c = new Constant(c0)
        val s = new Sum(x,c)
        for x0 <- -10 to 10 do
            x.set(x0)
            (x0+c0) shouldBe s.value
        end for
    }

    "Product" should "instansiate and evaluate correctly" in {
        val x = new Variable("x")
        val c0 = 2
        val c = new Constant(c0)
        val s = new Product(x,c)
        for x0 <- -10 to 10 do
            x.set(x0)
            (x0*c0) shouldBe s.value
        end for
    }

    "The Operator +" should "create a Polynomial expressing a sum" in {
        val x = new Variable("x")
        val c0 = 2
        val c = new Constant(c0)
        val s = x + c
        for x0 <- -10 to 10 do
            x.set(x0)
            (x0+c0) shouldBe s.value
        end for
    }

    "The Operator *" should "create a Polynomial expressing a product" in {
        val x = new Variable("x")
        val c0 = 2
        val c = new Constant(c0)
        val s = x*c
        for x0 <- -10 to 10 do
            x.set(x0)
            (x0*c0) shouldBe s.value
        end for
    }

    "Polynomial evaluation" should "work" in {
        val x = new Variable("x")
        val y = new Variable("y")
        // Univariate polynomials
        val poly1 = new Sum(new Product(x,x), new Constant(1)) // x*x+1
        val poly2 =  x*(x + new Constant(1))+x*(x+x+x)*x+x
        for x0 <- -10 to 10 do
            x.set(x0)
            (x0*x0+1) shouldBe poly1.value
            (x0*(x0+1)+x0*(x0+x0+x0)*x0+x0) shouldBe poly2.value
        end for
        // Multivariate polynomial
        val poly3 = x*(y + new Constant(1))+y+x*new Constant(2)
        for x0 <- -10 to 10 do
            x.set(x0)
            for y0 <- -10 to 10 do
                y.set(y0)
                (x0*(y0+1)+y0+2*x0) shouldBe poly3.value
            end for
        end for
    }

end PolynomialsSpec

